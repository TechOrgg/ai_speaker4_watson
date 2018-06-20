package com.mycode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.klab.ctx.ConversationSession;
import com.klab.svc.AppsPropertiy;
import com.klab.svc.CommChannel;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.model.AirQuality;
import com.model.WeatherData;
import com.utils.Utils;

/**
 * @author 최의신 (choies@kr.ibm.com)
 *
 * 지정된 지역의 날씨 정보를 조회한다.
 * RSS 지역 선택 : http://www.weather.go.kr/weather/lifenindustry/sevice_rss.jsp
 *
 */
@SuppressWarnings("rawtypes")
public class WeatherAction extends MyBaseAction
{
	/**
	 * @param date
	 * @return
	 */
	private AirQuality getAirQuality(String date)
	{
		AirQuality result = new AirQuality();
		
		try {
			String url = String.format(AppsPropertiy.getInstance().getProperty("air.url"),
					date, AppsPropertiy.getInstance().getProperty("air.key"));
			
			HttpResponse<JsonNode> jsonResponse = Unirest
					.post(url.toString())
					.asJson();
			
			JSONObject rs = jsonResponse.getBody().getObject();
			
			JSONArray list = rs.getJSONArray("list");
			
			JSONObject one = list.getJSONObject(0);
			
			String over = one.getString("informOverall");
			int ix = over.indexOf("]");
			if ( ix != -1 )
				result.setInformOverall(over.substring(ix+1).trim());
			else
				result.setInformOverall(over);
			
			String [] grade = one.getString("informGrade").split(",");
			for(int i = 0; i < grade.length; i++) {
				String [] ent = grade[i].split(":");
				
				result.addGrade(ent[0].trim(), ent[1].trim());
			}
		} catch (UnirestException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * 
	 */
	private List<WeatherData> getWeather(String rss) 
	{
		List<WeatherData> weatherList = new ArrayList<WeatherData>();
		
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(rss);

			if (document != null) {
				NodeList list = document.getElementsByTagName("data");

				for (int i = 0; i < list.getLength(); i++) {
					Node node = list.item(i);
					NodeList childList = node.getChildNodes();
					WeatherData wd = new WeatherData();
					weatherList.add(wd);
					
					for (int k = 0; k < childList.getLength(); k++) {
						Node one = childList.item(k);
						if (one.getNodeType() == Node.ELEMENT_NODE)
							wd.putData(one.getNodeName(), one.getTextContent());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return weatherList;
	}

	/* (non-Javadoc)
	 * @see com.mycode.MyBaseAction#executeAction(java.lang.String, java.util.Map, com.klab.ctx.ConversationSession, com.klab.svc.CommChannel)
	 */
	protected Object executeAction(String actionId, Map params, ConversationSession session, CommChannel channel)
	{
		Object exeResult = null;
		
		String city = params.get("city").toString();
		String zone = params.get("zone").toString();
		
		List<WeatherData> weather = getWeather("http://www.weather.go.kr/wid/queryDFSRSS.jsp?zone=" + zone);

		AirQuality airQuality = getAirQuality(Utils.currentTime("-", null, false, false));

		StringBuffer text = new StringBuffer();
		
		text.append("The temperature is ").append(getTemperture(weather)).append(". And fine dust is ").append(airQuality.getGrade(city)).append(".");
		if ( isRainSnow(weather) )
			text.append("and Take your umbrella with you.");
		
		Map<String, String> map = new HashMap<String, String>();
		exeResult = map;
		map.put("WEATHER", text.toString());

		return exeResult;
	}
	
	/**
	 * @param weather
	 * @return
	 */
	private String getTemperture(List<WeatherData> weather)
	{
		StringBuffer str = new StringBuffer();
		
		int h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		
		for(WeatherData w : weather) {
			if ((w.getDay() == 0 && h < w.getHour()) || w.getDay() == 1) {
				if ( w.getTemp() < 0 ) {
					str.append("minus ").append((int)(w.getTemp()*-1));
				}
				else {
					str.append((int)(w.getTemp()));
				}
				str.append(" degrees");
				break;
			}
		}		
		
		return str.toString();
	}
	
	/**
	 * @param weather
	 * @return
	 */
	private boolean isRainSnow(List<WeatherData> weather)
	{
		boolean rs = false;
		
		for(WeatherData w : weather) {
			if (w.getDay() == 0 && (w.getWfKor().indexOf("비") != -1 || w.getWfKor().indexOf("눈") != -1)) {
				rs = true;
				break;
			}
		}
		
		return rs;
	}
}
