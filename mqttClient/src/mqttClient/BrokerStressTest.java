package mqttClient;

public class BrokerStressTest {
	public static void main(String[] args) {
		//new PahoDemo().init();
		for(int i=0; i<1; i++)
			ThreadPool.getInstance().runWorker("Dev_State" + i+1,  new PahoDemo( 1000*i +1 ) ) ;
	}
}
