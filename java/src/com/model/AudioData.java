package com.model;

import java.io.InputStream;

/**
 * @author 최의신 (choies@kr.ibm.com)
 * 
 * 스피커로 출력되는 오디오(노래) 데이터를 저장한다.
 * audioSource는 오디오 바이너리 또는 MP3 URL을 저장한다.
 *
 */
public class AudioData
{
	public static final int SOURCE_PATH = 1;
	public static final int SOURCE_STREAM = 2;

	private int audioType;
	private Object audioSource;
	private String extraData;
	
	public AudioData(String path)
	{
		this.audioType = SOURCE_PATH;
		this.audioSource = path;
	}
	
	public AudioData(InputStream is)
	{
		this.audioType = SOURCE_STREAM;
		this.audioSource = is;
	}
	
	public int getAudioType() {
		return audioType;
	}
	public void setAudioType(int audioType) {
		this.audioType = audioType;
	}
	public String getExtraData() {
		return extraData;
	}
	public void setExtraData(String extraData) {
		this.extraData = extraData;
	}
	public Object getAudioSource() {
		return audioSource;
	}
	public void setAudioSource(Object audioSource) {
		this.audioSource = audioSource;
	}
}
