package com.svc;

import com.klab.svc.AppsPropertiy;

/**
 * @author 최의신 (choies@kr.ibm.com)
 * 
 * TTS를 처리할 서비스를 정의한다.
 * - Watson
 * - NAVER
 *
 */
public class TtsFactory
{
	public static ITts getTtsService()
	{
		ITts tts = null;
		String svc = AppsPropertiy.getInstance().getProperty("tts.svc");
		
		if ("naver".equals(svc)) {
			tts = new NaverTTS();
		}
		else if ("watson".equals(svc)) {
			tts = new WatsonTTS();
		}
		else {
			tts = new NaverTTS();
		}
		
		return tts;
	}
}
