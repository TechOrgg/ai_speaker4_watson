package com.svc;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import com.klab.svc.AppsPropertiy;

/**
 * @author 최의신 (choies@kr.ibm.com)
 *
 * 네이버 TTS 사용
 */
public class NaverTTS implements ITts
{
	@Override
	public InputStream streamTTS(String text)
	{
		if ( text == null || text.length() == 0 )
            return null;

        InputStream is = null;

        try {
            text = URLEncoder.encode(text, "UTF-8");
            String apiURL = "https://openapi.naver.com/v1/voice/tts.bin";
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("X-Naver-Client-Id", AppsPropertiy.getInstance().getProperty("tts.naver.cli_id"));
            con.setRequestProperty("X-Naver-Client-Secret", AppsPropertiy.getInstance().getProperty("tts.naver.cli_secret"));
            // post request
            String postParams = "speaker=mijin&speed=0&text=" + text;
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            if(responseCode==200) { // 정상 호출
                is = con.getInputStream();
            } else {  // 에러 발생
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
                throw new Exception(response.toString());
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }

        return is;
	}

	@Override
	public String fileTTS(String text) {
		return null;
	}
}
