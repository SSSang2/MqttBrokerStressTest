package mqttClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

public class AssignBroker {
	String masterBrokerIp = null;
	String slaveBrokerIp = null;
	String clientId = null;
	MqttClient relay = null;
	
	public AssignBroker(String ip, int port){
		try {
			Socket socket = new Socket(ip, port);
			setSlaveAddr(socket);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public AssignBroker(String ip, String id){
		
		masterBrokerIp = ip;
		clientId = id;
		
		try {
			if(!masterBrokerIp.equals(null) && !clientId.equals(null)){
				System.out.println("===============================================");
				System.out.println("-----------------------------------------------");
				System.out.println("# Connect to Master Broker");
				System.out.println("# Mater Broker URL : " + masterBrokerIp);
				System.out.println("# Clinet ID : " + clientId);
				System.out.println("-----------------------------------------------");
				System.out.println("===============================================");
				relay = new MqttClient(masterBrokerIp, clientId);
			}
			
		} catch (MqttException e) {
			e.printStackTrace();
		}
		
	}
	public String getSlaveAddr(){
		return slaveBrokerIp;
	}
	public void setSlaveAddr(Socket soc){
		try {
			OutputStream out = soc.getOutputStream();
			// 서버에 데이터 송신
			out.write("client".getBytes());
			out.flush();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			System.out.println("Wait message from master....");
			slaveBrokerIp = in.readLine();
			System.out.println("Msg from master : " + slaveBrokerIp);
			
			// 서버 접속 끊기
			in.close();
			out.close();
			soc.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public String setSlaveBrokerIp(){
		return (!slaveBrokerIp.equals(null)) ? slaveBrokerIp : null;
	}
}
