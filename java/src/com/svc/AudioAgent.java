package com.svc;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

import com.klab.svc.AppsPropertiy;
import com.klab.svc.ChannelMessage;
import com.model.AudioData;
import com.model.MusicData;

import javazoom.jl.player.Player;
import main.java.goxr3plus.javastreamplayer.stream.Status;
import main.java.goxr3plus.javastreamplayer.stream.StreamPlayer;
import main.java.goxr3plus.javastreamplayer.stream.StreamPlayerEvent;
import main.java.goxr3plus.javastreamplayer.stream.StreamPlayerListener;

/**
 * @author 최의신 (choies@kr.ibm.com)
 * 
 * 오디오(음성, MP3) 처리를 담당하는 스레드
 *
 */
public class AudioAgent extends BaseThread implements StreamPlayerListener 
{
	private StreamPlayer musicPlayer;
	private Player soundClip;
	private BlockingQueue<ChannelMessage> sounds;
	
	public AudioAgent(BlockingQueue<ChannelMessage> queue)
	{
		this.sounds = queue;
		
		musicPlayer = new StreamPlayer();
		musicPlayer.addStreamPlayerListener(this);
	}
	
	/**
	 * @param src
	 */
	public void addWave(InputStream src)
	{
		try
		{
			ChannelMessage msg = new ChannelMessage(Message.MESSAGE_WAVE);
			msg.setMessageData(new AudioData(src));
			
			sounds.put(msg);
		}catch(Exception x) {}
	}
	
	/**
	 * @param file
	 */
	public void addWave(String file)
	{
		try
		{
			ChannelMessage msg = new ChannelMessage(Message.MESSAGE_WAVE);
			msg.setMessageData(new AudioData(file));
			
			sounds.put(msg);
		}catch(Exception x) {}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run()
	{
		running = true;
		
		while(running == true || sounds.size() > 0)
		{
			try
			{
				ChannelMessage vc = sounds.poll(50, TimeUnit.MICROSECONDS);
				if ( vc != null )
				{
					switch(vc.getMessageId())
					{
						case Message.MESSAGE_WAVE:
							AudioData data = (AudioData)vc.getMessageData();
							
							if ( data.getAudioType() == AudioData.SOURCE_PATH)
								playWave(data.getAudioSource().toString());
							else
								playWave((InputStream)data.getAudioSource());
							break;
							
						case Message.MESSAGE_PLAY_MUSIC:
							playMusic((MusicData)vc.getMessageData());
							break;
							
						case Message.MESSAGE_STOP_MUSIC:
							stopMusic();
							break;
							
						default:
							break;
					}
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * @param wave
	 */
	private void playWave(InputStream wave)
	{
		try
		{
			String svc = AppsPropertiy.getInstance().getProperty("tts.svc");
			
			if ("naver".equals(svc)) {
				if (soundClip != null && soundClip.isComplete() == false )
					soundClip.close();
				
				soundClip = new Player(wave);
				soundClip.play();
			}
			else {
				_play(AudioSystem.getAudioInputStream(wave));
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param wave
	 */
	private void playWave(String wave)
	{
		try
		{
			_play(AudioSystem.getAudioInputStream(new File(wave)));
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param audioIn
	 */
	private void _play(AudioInputStream audioIn) throws Exception
	{
		Clip clip = AudioSystem.getClip();
		clip.addLineListener(new LineListener() {
			public void update(LineEvent event) {
				if (event.getType() == LineEvent.Type.STOP) {
					clip.flush();
					try {
						event.getLine().close();
					} catch (Exception ig) {
					}
				}
			}
		});
		clip.open(audioIn);
		clip.start();
	}
	
	
	/**
	 * @param input
	 */
	public void playMusic(MusicData data)
	{
		if ( data == null )
			return;

		try {
			if ( musicPlayer.isPlaying() )
				musicPlayer.stop();
			
            URL url = new URL(data.getSongUrl());
            URLConnection connection = url.openConnection();

            HttpURLConnection httpConn = (HttpURLConnection) connection;
            httpConn.setRequestProperty("Authorization", "Basic " + data.getAuthToken());
            httpConn.connect();

            BufferedInputStream bin = new BufferedInputStream(httpConn.getInputStream(), 256 * 1024);
			musicPlayer.open(bin);
			musicPlayer.play();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	public void stopMusic()
	{
		if ( musicPlayer.isPlaying() )
			musicPlayer.stop();
	}
	
	@Override
	public void opened(Object dataSource, Map<String, Object> properties) {
	}

	@Override
	public void progress(int nEncodedBytes, long microsecondPosition, byte[] pcmData, Map<String, Object> properties) {
	}

	@Override
	public void statusUpdated(StreamPlayerEvent event) {
		if ( event.getPlayerStatus() == Status.STOPPED ) {
			Object src = event.getSource();
			if ( src instanceof InputStream ) {
				try {
					((InputStream)src).close();
				} catch (IOException e) {
				}
			}
		}
	}	
}
