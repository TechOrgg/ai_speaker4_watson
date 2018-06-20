package com.svc;

import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.sound.sampled.TargetDataLine;

import com.ibm.watson.developer_cloud.http.HttpMediaType;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechRecognitionResult;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechRecognitionResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.RecognizeCallback;
import com.io.SPIAgent;
import com.klab.ctx.ConversationLogInfo;
import com.klab.ctx.ConversationSession;
import com.klab.ctx.SessionManager;
import com.klab.svc.AppsPropertiy;
import com.klab.svc.ChannelMessage;
import com.klab.svc.CommChannel;
import com.klab.svc.ConsoleLogger;
import com.klab.svc.ConversationLogger;
import com.klab.svc.SimpleAppFrame;
import com.utils.SwingWorker;

import ai.kitt.snowboy.SnowboyDetect;

/**
 * @author 최의신 (choies@kr.ibm.com)
 * 
 * 인공지능 스피커 메인 클래스
 *
 */
public class AiSpeakerAgent extends BaseThread implements RecognizeCallback
{
	static {
		System.loadLibrary("snowboy-detect-java");
	}
	
	public static final int BYTES_PER_SAMPLE = 2; // bytes per sample for LINEAR16
	public static final int DEFAULT_SAMPLING_RATE = 16000;
	public static final int DEFAULT_CHANNEL = 1;
	
	public static final int MODE_HOTWORD = 1;
	public static final int MODE_RECOGNITION = 2;
	
	public static final int LOW_FREQUENCE = 200;
	
	public static final String CRLF = "\n";
	private static final String ATTR_NAME = "Conversation";
	
	private SimpleAppFrame appFrame;
	private ConversationLogger convLogger;

	private int mode = MODE_HOTWORD;
	
	private String audioId;
	private TargetDataLine audioLine;
	
	// snowboy
	private SnowboyDetect detector; 
	
	//Watson STT
	private SpeechToText service;
	private PipedOutputStream recogOut = null;
	private PipedInputStream recogIn = null;
	
	// TTS
	private ITts tts = null;

	private boolean connected = false;
	private boolean reserveDing = false;
	
	// RGB LED
	private SPIAgent spiAgent;
	
	// Speaker
	private AudioAgent audioAgent;
	
	// MQTT
	private MqttAgent mqttAgent;

	// Channel
	private CommChannel commChannel; 
	
	public AiSpeakerAgent(String audioId)
	{
		this.audioId = audioId;
	}
	
	private void createWSocket()
	{
		if ( connected == false )
		{
			try
			{
				if ( recogIn != null )
				{
					recogIn.close();
					recogOut.close();
				}
	
				recogOut = null;
				recogIn = null;
				
				recogOut = new PipedOutputStream();
				recogIn = new PipedInputStream(recogOut);
	
				String custid = AppsPropertiy.getInstance().getProperty("stt.custid");
				RecognizeOptions.Builder opts = new RecognizeOptions.Builder();
				
				opts.interimResults(true)
					.inactivityTimeout(-1)
			        .timestamps(false)
					.contentType(HttpMediaType.AUDIO_RAW + ";rate=" + DEFAULT_SAMPLING_RATE + ";channels=" + DEFAULT_CHANNEL)
					.model("ko-KR_BroadbandModel")
					.audio(recogIn);
				
				if ( custid != null && custid.length() > 0 )
					opts.customizationId(custid);
				
				RecognizeOptions options = opts.build();
				
//						.interimResults(true)
//						.inactivityTimeout(-1)
//				        .timestamps(false)
//						.contentType(HttpMediaType.AUDIO_RAW + ";rate=" + DEFAULT_SAMPLING_RATE + ";channels=" + DEFAULT_CHANNEL)
//						.model("ko-KR_BroadbandModel")
//						.audio(recogIn)
//						.build();
				service.recognizeUsingWebSocket(options, this);
			}catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * @return
	 */
	public boolean prepare()
	{
		boolean rs = false;
		
		try
		{
			/*
			 * Watson Conversation
			 */
			appFrame = new SimpleAppFrame();
			appFrame.setUsername(AppsPropertiy.getInstance().getProperty("wcs.user"));
			appFrame.setPassword(AppsPropertiy.getInstance().getProperty("wcs.passwd"));
			appFrame.setWorkspaceId(AppsPropertiy.getInstance().getProperty("wcs.workid"));
			
			/*
			 * 대화를 저장할 로거를 생성
			 */
			convLogger = new ConversationLogger();
			
//			String logger = AppsPropertiy.getInstance().getProperty("logger.className");
//			if ( logger != null && logger.length() > 0 )
//			{
//				try {
//					convLogger.setLogger((ILogger)Utils.loadClass(logger));
//				} catch (Exception e) {
//					convLogger.setLogger(new ConsoleLogger());
//				}
//			}
//			else {
//				convLogger.setLogger(new ConsoleLogger());
//			}
			
			convLogger.setLogger(new ConsoleLogger());
			convLogger.start();		

		    ConversationSession session = SessionManager.getInstance().getSession(ATTR_NAME);
		    processText(session, true, null);
			/*
			 * Watson Conversation
			 */

			/*
			 * 채널 구성
			 */
			commChannel = new CommChannel();
			
			BlockingQueue<ChannelMessage> sound = new ArrayBlockingQueue<ChannelMessage>(100);
			BlockingQueue<ChannelMessage> toBroker = new ArrayBlockingQueue<ChannelMessage>(100);
			BlockingQueue<ChannelMessage> fromBroker = new ArrayBlockingQueue<ChannelMessage>(100);
			BlockingQueue<ChannelMessage> led = new ArrayBlockingQueue<ChannelMessage>(100);
			
			commChannel.addChannel(Message.CHANNEL_SOUND, sound);
			commChannel.addChannel(Message.CHANNEL_BROKER, toBroker);
			commChannel.addChannel(Message.CHANNEL_LED, led);
			
			this.spiAgent = new SPIAgent(led);
			this.spiAgent.start();
			
			/*
			 * Sound & Music
			 */
			audioAgent = new AudioAgent(sound);
			audioAgent.start();
			
			// MQTT
			mqttAgent = new MqttAgent(toBroker, fromBroker);
			mqttAgent.start();
		    
			/*
			 * MICROPHONE
			 */
	        MicrophoneSelector ms = new MicrophoneSelector(audioId, DEFAULT_SAMPLING_RATE, BYTES_PER_SAMPLE * 8, DEFAULT_CHANNEL);	// 16000, 16bit, mono
	        audioLine = ms.getMicrophone();
	        audioLine.open(ms.getAudioFormat(), audioLine.getBufferSize() * 2);
	        audioLine.start();
	        
	        /*
	         * SNOWBOY
	         */
	        detector = new SnowboyDetect("resources/common.res", "resources/Hey_Watson.pmdl");
			detector.SetSensitivity("0.6");
			detector.SetAudioGain(2);
			
			/*
			 * TTS
			 */
			tts = TtsFactory.getTtsService();
			
	        /*
	         * Watson STT
	         */
			service = new SpeechToText();
			service.setUsernameAndPassword(AppsPropertiy.getInstance().getProperty("stt.watson.user"),
					AppsPropertiy.getInstance().getProperty("stt.watson.passwd"));
			
			rs = true;
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return rs;
	}
	
	/**
	 * 
	 */
	private void cleanup()
	{
		System.out.println("@.@ CLEANUP");

		audioLine.stop();
        audioLine.close();
	}
	
	/**
	 * @param msgId
	 */
	@SuppressWarnings("unchecked")
	private void sendToLed(int msgId)
	{
		BlockingQueue<ChannelMessage> queue = commChannel.getChannel(Message.CHANNEL_LED);
		if ( queue != null ) {
			try {
				ChannelMessage msg = new ChannelMessage(msgId);
				queue.put(msg);
			}catch(Exception e) {}
		}		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run()
	{
		running = prepare();
		
		// Snowboy
		// Reads 0.1 second of audio in each call.
		byte[] targetData = new byte[3200];
		short[] snowboyData = new short[1600];
		
		// Google
		int bytesPerBuffer = DEFAULT_SAMPLING_RATE * BYTES_PER_SAMPLE / 10; // 100 ms
		byte[] gBuffer = new byte[bytesPerBuffer];
	    int bytesRead;

	    System.out.println("@.@ STARTED !!  " + gBuffer.length);
	    
	    //whiteSound = new byte[bytesPerBuffer];
	    
	    sendToLed(Message.MESSAGE_STARTUP);
	    
	    audioAgent.addWave("resources/start.wav");
	    
		while(running)
		{
			try
			{
				if ( mode == MODE_HOTWORD ) {
					bytesRead = audioLine.read(targetData, 0, targetData.length);

					if (bytesRead == -1) {
						System.out.print("Fails to read audio data.");
						Thread.sleep(100);
						continue;
					}

					// Converts bytes into int16 that Snowboy will read.
					ByteBuffer.wrap(targetData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(snowboyData);

					// Detection.
					int result = detector.RunDetection(snowboyData, snowboyData.length);
					if (result > 0) {
						createWSocket();
						
						mode = MODE_RECOGNITION;

						sendToLed(Message.MESSAGE_WAKEUP);
						
						if ( connected ) {
							audioAgent.addWave("resources/ding2.wav");
						}
						else {
							reserveDing = true;
						}
					}
				}
				else {
			        if((bytesRead = audioLine.read(gBuffer, 0, gBuffer.length)) > 0) {
			        	recogOut.write(gBuffer, 0, bytesRead);
		        	}
				}
			}catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		
		cleanup();
	}
	
	/**
	 * @param session
	 * @param clean
	 * @param text
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String processText(ConversationSession session, boolean clean, String text) throws Exception
	{
		ConversationLogInfo log = null;
		
		if ( clean ) {
			session.getContext().clear();
			log = appFrame.message(session, "", commChannel);
		}
		else {
			log = appFrame.message(session, text, commChannel);
		}
		
		String newline = CRLF;
		Object obj = session.getProperty("NEWLINE");
		if ( obj != null )
			newline = obj.toString();
		
		StringBuffer resText = new StringBuffer();
		List<String> list = session.getOutputString();
		for(int i = 0; i < list.size(); i++)
		{
			resText.append(list.get(i));
			if ( i < list.size()-1 )
				resText.append(newline);
		}
		
		/*
		 * 대화 이력을 저장한다.
		 */
		if ( convLogger != null && log != null )
			convLogger.addDialog(log);
		
		return resText.toString();
	}	
	
	/**
	 * 
	 */
	public void shutdown()
	{
		running = false;
		
		mqttAgent.shutdown();
		spiAgent.shutdown();
		audioAgent.shutdown();
	}

	@Override
	public void onConnected() {
		connected = true;
		if ( reserveDing == true ) {
			reserveDing = false;
			audioAgent.addWave("resources/ding2.wav");
		}
		System.out.println("@.@ onConnected...");
	}

	@Override
	public void onDisconnected() {
		connected = false;
		System.out.println("@.@ onDisconnected...");
	}

	@Override
	public void onError(Exception ex) {
		mode = MODE_HOTWORD;
	}

	@Override
	public void onInactivityTimeout(RuntimeException ex) {
	}

	@Override
	public void onListening() {
	}

	@Override
	public void onTranscription(SpeechRecognitionResults speechResults)
	{
		List<SpeechRecognitionResult> rs = speechResults.getResults();
		
		for(SpeechRecognitionResult ts : rs)
		{
			if ( ts.isFinalResults() )
			{
				String speech = ts.getAlternatives().get(0).getTranscript();
				
				System.out.println("@.@ STT : " + speech);
				
				mode = MODE_HOTWORD;
				
				sendToLed(Message.MESSAGE_PROGRESS);
				
			    try {
			    	ConversationSession session = SessionManager.getInstance().getSession(ATTR_NAME);
					final String res = processText(session, false, speech);
					if ( res.length() > 0 ) {
						SwingWorker work = new SwingWorker() {
							@Override
							public Object construct() {
								try {
									if ( tts != null ) {
										InputStream is = tts.streamTTS(res);
										if ( is != null ) {
											audioAgent.addWave(is);
											sendToLed(Message.MESSAGE_COMPLETED);
										}
									}
								}catch(Exception e) {
									e.printStackTrace();
								}
								
								return null;
							}
						};
						
						work.start();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				break;
			}
		}
	}

	@Override
	public void onTranscriptionComplete() {
	}
	
	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		MicrophoneSelector.showMicList();
		
		AiSpeakerAgent sa = new AiSpeakerAgent(AppsPropertiy.getInstance().getProperty("mic.id"));
		sa.start();
		sa.join();
	}

}
