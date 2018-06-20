package com.svc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.klab.svc.AppsPropertiy;
import com.klab.svc.ChannelMessage;
import com.utils.Utils;


/**
 * @author 최의신 (choies@kr.ibm.com)
 *
 * MQTT 메시지를 보내거나 받는 스레드
 */
@SuppressWarnings("rawtypes")
public class MqttAgent extends BaseThread implements MqttCallback
{
	private MqttClient mqttClient;
	
	private BlockingQueue<ChannelMessage> toBroker;
	private BlockingQueue<ChannelMessage> fromBroker;
	
	public MqttAgent(BlockingQueue<ChannelMessage> toWk, BlockingQueue<ChannelMessage> fromWk)
	{
		this.toBroker = toWk;
		this.fromBroker = fromWk;
	}

	@Override
	public void run()
	{
		try {
			running = true;
			
			connect();
			
			super.startTime = Utils.currentTime4();
			
			while(running)
			{
				try {
					ChannelMessage co = toBroker.poll(100, TimeUnit.MILLISECONDS);
					
					if (co != null )
					{
						JsonObject root = (JsonObject)co.getMessageData();
						JsonArray list = root.get("devControl").getAsJsonArray();

						for(int i = 0; i < list.size(); i++)
						{
							JsonObject obj = list.get(i).getAsJsonObject();
							
							String topic = obj.get("TOPIC").getAsString();
							String payload = obj.get("PAYLOAD").toString();

							MqttMessage mm = new MqttMessage();
							mm.setQos(2);
							mm.setPayload(payload.getBytes());
							mqttClient.publish(topic, mm);

							Thread.sleep(500);
						}
					}
					
					super.setError(false);
				} catch (Exception e) {
					e.printStackTrace();
					super.setError(true);
					super.setErrorString(e.getMessage());
					logger.debug("MqttReceiverThread", e);
					running = false;
				}
			};

			disconnect();
		} catch (Exception e1) {
			e1.printStackTrace();
			super.setError(true);
			super.setErrorString(e1.getMessage());
		}
		
		System.out.println("@.@ SHUTDOWN...");
	}

	/**
	 * 
	 */
	private void connect() throws Exception
	{
		AppsPropertiy conf = AppsPropertiy.getInstance();
		String broker = conf.getProperty("mqtt.server") + ":" + conf.getProperty("mqtt.port");
		MemoryPersistence persistence = new MemoryPersistence();

		mqttClient = new MqttClient(broker, conf.getProperty("client.id"), persistence);
		mqttClient.setCallback(this);

		MqttConnectOptions connOpts = new MqttConnectOptions();
	
		connOpts.setCleanSession(true);
		connOpts.setUserName(conf.getProperty("mqtt.id"));
		connOpts.setPassword(conf.getProperty("mqtt.pwd").toCharArray());
		
		mqttClient.connect(connOpts);
		
		Map<String, TaskEntry> list = getTopicList(conf);
		System.out.println("Connected.. " + list);

		for(Iterator it = list.keySet().iterator(); it.hasNext(); )
		{
			String key = it.next().toString();
			TaskEntry te = list.get(key);
			
			if ( te.getTopic().indexOf("+") == -1 ) {
				mqttClient.subscribe(te.getTopic());
				logger.debug("@.@ SUBS : " + te.getTopic());
			}
			else {
				Object ins = com.klab.svc.Utils.loadClass(te.getTaskName());
				te.setInstance((ITask)ins);
				mqttClient.subscribe(te.getTopic(), (IMqttMessageListener)ins);
				logger.debug("@.@ SUBS : " + te.getTopic());
			}
		}
		
		logger.debug("MQTT Connected.");
	}
	
	/**
	 * @param conf
	 * @return
	 */
	private Map<String, TaskEntry> getTopicList(AppsPropertiy conf)
	{
		Map<String, TaskEntry> topic = new HashMap<String, TaskEntry>();
		
		for(Iterator it = conf.keySet().iterator(); it.hasNext(); )
		{
			String key = it.next().toString();
			if ( key.startsWith("topic.") )
			{
				String val = conf.getProperty(key);
				String [] tok = val.split(",");
				
				TaskEntry te = new TaskEntry();
				te.setTaskName(tok[1]);
				te.setTopic(tok[0]);
				
				topic.put(te.getTopic(), te);
			}
		}
		
		return topic;
	}
	
	/**
	 * 
	 */
	private void disconnect()
	{
		if ( mqttClient == null )
			return;
		
		try {
			mqttClient.disconnect();
		} catch (Exception ig) {
		}
	}	
	
	public String getIdentity() {
		return "MQTT Receiver";
	}

	public String getStartTime() {
		return startTime;
	}

	@Override
	public void connectionLost(Throwable t) {
		System.out.println(t);
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken t) {
		System.out.println(t);
	}

	@Override
	public void messageArrived(String topic, MqttMessage msg) throws Exception
	{
		ChannelMessage chMsg = new ChannelMessage(0);
		
		logger.debug("[RECV-LocalMqttThread-" + topic + "] " + msg);
		
		fromBroker.put(chMsg);
	}
}
