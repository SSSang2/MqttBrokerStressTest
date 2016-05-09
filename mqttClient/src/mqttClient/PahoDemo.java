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
	int LOOP = 10;
    long startTime;
    long diffTime;
    long firstTime;
    long avgTime;
    int recevedNode = 0;
    String SUB_TOPIC = "client";
    MqttClient sampleClient = null;
    MqttClient publishClient = null;
    ArrayList<MqttClient> clientList = null;
	ArrayList<Integer> check;
	BufferedWriter writer ;
	String slave = null;//tmp.getSlaveAddr();
	String topic        = "MQTTex";
    String content      = "Message from MqttPublishSample";
    int qos             = 2;
    String broker		= "tcp://10.180.117.97:10000";// + slave;
    //String broker		= "tcp://163.180.117.231:1883";// + slave;
    
    
	PahoDemo(int num){
		offset = num;
		init();
		run();
	}
	
	
	public void init(){
		//AssignBroker tmp = new AssignBroker("163.180.117.97", 1883);
		clientList = new ArrayList<MqttClient>();
		

        slave = "tcp://" + slave;
        check = new ArrayList<Integer>();
        MemoryPersistence persistence;
        slave = slave.substring(0, slave.length()-1);
        
		System.out.println("Broker Addr : " + broker + ", " + broker.length());
		System.out.println("Slave Addr : " + slave + ", " + slave.length());
		try {
       
        	writer = new BufferedWriter(new FileWriter("test.txt"));
        	sampleClient = null;
        	persistence = new MemoryPersistence();
			publishClient = new MqttClient(broker, "pubClient", persistence);
			MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            publishClient.connect(connOpts);
            publishClient.setCallback(this);
            
			
			
        	for(int i = offset; i<offset+LOOP; i++){
        		String clientId = clientID + i;
        		persistence = new MemoryPersistence();
				sampleClient = new MqttClient(broker, clientId, persistence);
				clientList.add(sampleClient);
	            
				connOpts = new MqttConnectOptions();
	            connOpts.setCleanSession(true);
	            System.out.println("Connecting to broker: "+broker);
	            sampleClient.connect(connOpts);
	            sampleClient.setCallback(this);
	            sampleClient.subscribe(SUB_TOPIC);
	            System.out.println("Connected");
	            //System.out.println("Publishing message: "+content);
	            //MqttMessage message = new MqttMessage(content.getBytes());
	            //message.setQos(qos);
	            //sampleClient.publish(topic, message);
	            //System.out.println("Message published");
	            check.add(0);
	            //sampleClient.disconnect();
	            //System.out.println("Disconnected");
	            //System.exit(0);
        	}
			} catch (MqttException | IOException e) {
				System.out.println("Init Error");
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
		//check.set(num-1, check.get(num-1)+1);
		if(num == LOOP+1){
			num = 1;
		}
		
		Calendar cal=Calendar.getInstance();
		Date endDate=cal.getTime();
		long endTime=endDate.getTime();
		recevedNode++;
		diffTime = endTime-startTime;
		//System.out.println("NUM : " + num + ",	Time : " + diffTime + ",	Start : " + startTime + ",	End : " + endTime);
		
		
//		if( endTime - startTime > diffTime)
//			diffTime = endTime-startTime;
//		if(num==1)
//			firstTime = diffTime;
//		
//		avgTime = avgTime + endTime - startTime ;
//		if(num == LOOP)
//			avgTime = avgTime / LOOP;
		num++;
	}
	@Override
	public void run() {
		try{
		while(true)
        {
			
        	//for(int i=0; i<check.size(); i ++){
            	//System.out.print(check.get(i) + " ");
            	//writer.write(check.get(i) + " ");
            	//Date dt = new Date();
        		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd, hh:mm:ss a"); 
        		
        	//}
        	String msg = "Test msg";
        	MqttMessage message1 = new MqttMessage(msg.getBytes());
            message1.setQos(0);
            
            //System.out.println( "# of Node" + recevedNode + ",	First Time : " + firstTime + ",  Last time : " + diffTime + ",	Avg Time : "+ avgTime);
            System.out.println(diffTime);
            try {
				writer.write((int) diffTime);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            recevedNode = 0;
            startTime = 0;
            diffTime = 0;
            firstTime =0;
            avgTime =0;
            
            //System.out.println("#Client ID : " + publishClient.getClientId());
            //System.out.println("Is Connected : " + publishClient.isConnected());
            Calendar cal=Calendar.getInstance();
        	Date startDate=cal.getTime();
        	startTime=startDate.getTime();
        	publishClient.publish(SUB_TOPIC, message1);
        	
            Thread.sleep(5000);
		}
		        
		    }catch(MqttException me) {
		        System.out.println("reason "+me.getReasonCode());
		        System.out.println("msg "+me.getMessage());
		        System.out.println("loc "+me.getLocalizedMessage());
		        System.out.println("cause "+me.getCause());
		        System.out.println("excep "+me);
		        me.printStackTrace();
		        
		    } catch (InterruptedException e) {
				e.printStackTrace();
			}	
        
	}
		
}

