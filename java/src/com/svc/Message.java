package com.svc;

/**
 * @author 최의신 (choies@kr.ibm.com)
 *
 */
public interface Message
{
	public final String CHANNEL_SOUND = "sound";
	public final String CHANNEL_BROKER = "broker";
	public final String CHANNEL_LED = "led";
	
	public final int MESSAGE_WAVE = 1;
	public final int MESSAGE_PLAY_MUSIC = 2;
	public final int MESSAGE_STOP_MUSIC = 3;
	
	public final int MESSAGE_SPEECH = 4;
	
	public final int MESSAGE_STARTUP = 10;
	public final int MESSAGE_WAKEUP = 11;
	public final int MESSAGE_PROGRESS = 12;
	public final int MESSAGE_COMPLETED = 13;
	
	public final int MESSAGE_CONTROL = 100;
}
