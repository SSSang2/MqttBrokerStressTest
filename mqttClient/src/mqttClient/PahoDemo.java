package mqttClient;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class PahoDemo implements MqttCallback, Runnable{
	static String clientID     = "JavaSample";
	int num=1;
	int offset =0;
	int LOOP = 1000;
    long startTime;
    long diffTime;
    int recevedNode = 0;
	ArrayList<Integer> check;
	BufferedWriter writer ;
	PahoDemo(int num){
		offset = num;
		init();
	}
	public void init(){
		//AssignBroker tmp = new AssignBroker("163.180.117.97", 1883);
		
		String slave = null;//tmp.getSlaveAddr();
		String SUB_TOPIC 	= "client";
		String topic        = "MQTTex";
        String content      = "Message from MqttPublishSample";
        int qos             = 2;
        String broker		= "tcp://10.180.117.97:10000";// + slave;

        slave = "tcp://" + slave;
        check = new ArrayList<Integer>();
        MemoryPersistence persistence = new MemoryPersistence();
        slave = slave.substring(0, slave.length()-1);
        
		System.out.println("Broker Addr : " + broker + ", " + broker.length());
		System.out.println("Slave Addr : " + slave + ", " + slave.length());
		
        try {
        	writer = new BufferedWriter(new FileWriter("test.txt"));
        	MqttClient sampleClient = null;
        	for(int i = offset; i<=offset+LOOP; i++){
        		String clientId = clientID + i;
        		sampleClient = new MqttClient(broker, clientId, persistence);
	            MqttConnectOptions connOpts = new MqttConnectOptions();
	            connOpts.setCleanSession(true);
	            System.out.println("Connecting to broker: "+broker);
	            sampleClient.connect(connOpts);
	            sampleClient.setCallback(this);
	            sampleClient.subscribe(SUB_TOPIC);
	            System.out.println("Connected");
	            System.out.println("Publishing message: "+content);
	            MqttMessage message = new MqttMessage(content.getBytes());
	            message.setQos(qos);
	            sampleClient.publish(topic, message);
	            System.out.println("Message published");
	            check.add(0);
	            //sampleClient.disconnect();
	            //System.out.println("Disconnected");
	            //System.exit(0);
        	}
            while(true)
            {
            	//for(int i=0; i<check.size(); i ++){
                	//System.out.print(check.get(i) + " ");
                	//writer.write(check.get(i) + " ");
                	//Date dt = new Date();
            		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd, hh:mm:ss a"); 
            		//writer.write(sdf.format(dt).toString());
            	//}
            	String msg = "Test msg";
            	MqttMessage message1 = new MqttMessage(msg.getBytes());
                message1.setQos(0);
                
                System.out.println("Start Time : " + startTime + ",  Diff time : " + diffTime + ",	# of Node" + recevedNode);
                recevedNode = 0;
                startTime = 0;
                diffTime = 0;
                
                sampleClient.publish(SUB_TOPIC, message1);
                Calendar cal=Calendar.getInstance();
            	Date startDate=cal.getTime();
            	startTime=startDate.getTime();
                Thread.sleep(5000);
                
            }
        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        } catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void connectionLost(Throwable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	synchronized public void messageArrived(String arg0, MqttMessage arg1) throws Exception {
		Date dt = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd, hh:mm:ss a"); 
		//System.out.println("Msg from Broker : " + clientID + " , " + arg0 + " , " +  arg1 + ",  " + sdf.format(dt).toString() + " , ");
		check.set(num-1, check.get(num-1)+1);
		num++;
		if(num==LOOP+1)
			num = 1;
		
		Calendar cal=Calendar.getInstance();
		Date endDate=cal.getTime();
		long endTime=endDate.getTime();
		recevedNode++;
		if( endTime - startTime > diffTime)
			diffTime = endTime-startTime;
		
		
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
