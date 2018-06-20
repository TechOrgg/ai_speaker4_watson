package com.model;

/**
 * @author 최의신 (choies@kr.ibm.com)
 *
 * MP3 파일의 URL 및 인증정보를 저장한다.
 */
public class MusicData 
{
	private String songUrl;
	private String authToken;
	
	public String getSongUrl() {
		return songUrl;
	}
	public void setSongUrl(String songUrl) {
		this.songUrl = songUrl;
	}
	public String getAuthToken() {
		return authToken;
	}
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}
}
