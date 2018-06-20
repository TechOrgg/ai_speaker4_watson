package com.svc;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

/**
 * @author 최의신 (choies@kr.ibm.com)
 *
 * 시스템에 등록된 마이크로 폰을 선택한다.
 */
public class MicrophoneSelector
{
	private String microphoneId;
	private String errorString;
	private AudioFormat audioFormat;
	private int sampleRate = 16000;
	private int bitPerSample = 16;
	private int channel = 1;
	
	public MicrophoneSelector(String mic)
	{
		this.microphoneId = mic;
		this.errorString = null;
	}

	public MicrophoneSelector(String mic, int sample, int bit, int channel)
	{
		this.microphoneId = mic;
		this.errorString = null;
		this.sampleRate = sample;
		this.bitPerSample = bit;
		this.channel = channel;
	}

	
	/**
	 * @return
	 */
	public String getErrorString()
	{
		return errorString;
	}
	
	/**
	 * @return
	 */
	public AudioFormat getAudioFormat()
	{
		return audioFormat;
	}
	
	/**
	 * @return
	 */
	public TargetDataLine getMicrophone()
	{
		boolean bigEndian = false;

//		audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, VoiceConstants.DEFAULT_SAMPLE_RATE, VoiceConstants.DEFAULT_SAMPLE_SIZE, 
//				VoiceConstants.DEFAULT_CHANNELS, (VoiceConstants.DEFAULT_SAMPLE_SIZE / 8) * VoiceConstants.DEFAULT_CHANNELS, VoiceConstants.DEFAULT_SAMPLE_RATE, bigEndian);
		audioFormat = new AudioFormat(sampleRate, bitPerSample, channel, true, bigEndian);

		TargetDataLine line = null;

		if ( microphoneId == null || "_DEFAULT_".equals(microphoneId) )
		{
			line = getDefaultLine(audioFormat);
		}
		else {
			line = selectLine(audioFormat, microphoneId);
		}
		
		return line;
	}
	
	
	/**
	 * @param format
	 * @param id
	 * @return
	 */
	private TargetDataLine selectLine(AudioFormat format, String id)
	{
		TargetDataLine targetLine = null;
		
		try
		{
			Mixer.Info[] mixerInfo;
			mixerInfo = AudioSystem.getMixerInfo();
			Line.Info targetDLInfo = new Line.Info(TargetDataLine.class);

			for(int cnt = 0; cnt < mixerInfo.length; cnt++)
			{
				Mixer currentMixer = AudioSystem.getMixer(mixerInfo[cnt]);
				
				if ( currentMixer.isLineSupported(targetDLInfo) )
				{
					if ( mixerInfo[cnt].getName().indexOf(id) != -1 )
					{
						DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, format );
						targetLine = (TargetDataLine) currentMixer.getLine(dataLineInfo) ;
						break;
					}
				}
			}			
			
		}catch(Exception ex) {
			errorString = ex.getMessage();
		}
		
		return targetLine;
	}
	
	/**
	 * @param format
	 * @return
	 */
	private TargetDataLine getDefaultLine(AudioFormat format)
	{
		TargetDataLine targetLine = null;
		
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

		if (!AudioSystem.isLineSupported(info)) {
			errorString = "Line matching " + info + " not supported.";
			return null;
		}
		
		try {
			targetLine = (TargetDataLine) AudioSystem.getLine(info);
		} catch (Exception ex) {
			errorString = "Unable to open the line: " + ex;
		}
		
		return targetLine;
	}
	
	/**
	 * 
	 */
	public static void showMicList()
	{
		try
		{
			Mixer.Info[] mixerInfo;
			mixerInfo = AudioSystem.getMixerInfo();
			Line.Info targetDLInfo = new Line.Info(TargetDataLine.class);

			for(int cnt = 0; cnt < mixerInfo.length; cnt++)
			{
				Mixer currentMixer = AudioSystem.getMixer(mixerInfo[cnt]);
				if ( currentMixer.isLineSupported(targetDLInfo) )
					System.out.println("[MIC] " + mixerInfo[cnt].getName());
			}			
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}	
}