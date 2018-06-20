package com.svc;

import java.io.InputStream;

/**
 * @author 최의신 (choies@kr.ibm.com)
 * 
 * TTS 기능을 정의한 인터페이스
 *
 */
public interface ITts
{
	public InputStream streamTTS(String text);
	
	public String fileTTS(String text);
}
