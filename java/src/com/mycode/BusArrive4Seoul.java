package com.mycode;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.klab.ctx.ConversationSession;
import com.klab.svc.AppsPropertiy;
import com.klab.svc.CommChannel;
import com.svc.MessageResource;

import org.json.JSONObject;
import org.json.JSONArray;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequestWithBody;

/**
 * @author 최의신 (choies@kr.ibm.com)
 * 
 * 서울 버스에 대한 버스 도착정보를 처리한다.
 *
 */
@SuppressWarnings("rawtypes")
public class BusArrive4Seoul extends MyBaseAction
{
	private Pattern pattern = Pattern.compile("([0-9]*)분(([0-9]*)초)?");
	
	/**
	 * @return
	 */
	public String busArrive(String routeId, String arsId)
	{
		String parmUrl = AppsPropertiy.getInstance().getProperty("bus.seoul.parm");
		String arrInfo = null;
		
		try
		{
			HttpRequestWithBody req = Unirest.post(AppsPropertiy.getInstance().getProperty("bus.seoul.url"));

			req.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36 encors/0.0.6");
			req.header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			req.header("Accept", "application/json, text/plain, */*");
			req.header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
			req.header("Cache-Control", "no-cache, no-store, must-revalidate");
			req.header("Accept-Encoding", "gzip, deflate, br");
			req.header("Connection", "keep-alive");
			
			req.queryString("url", parmUrl);
			req.queryString("arsId", arsId);
			
			HttpResponse<JsonNode> res = req.asJson();
			JSONObject resBody = res.getBody().getObject();
			
			if ( resBody.has("rows") ) {
				JSONArray list = resBody.getJSONArray("rows");
				for(int i = 0; i < list.length(); i++) {
					JSONObject o = list.getJSONObject(i);
					
					if ( routeId.equals(o.get("busRouteId").toString()) ) {
						arrInfo = o.getString("arrmsgSec1");
						break;
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return arrInfo;
	}
	
	/* (non-Javadoc)
	 * @see com.klab.svc.BaseAction#execute(java.lang.String, java.util.Map)
	 */
	@Override
	protected Object executeAction(String actionId, Map params, ConversationSession session, CommChannel channel)
	{
		String routeId = (String)params.get("routeId");
		String arsId = (String)params.get("arsId");
		String time = busArrive(routeId, arsId);
		
		Object exeResult = null;
		
		try
		{
			Map<String, String> map = new HashMap<String, String>();
			
			if ( time != null ) {
				if ( "곧도착".equals(time) ) {
					map.put("BUS_ARRIVE", MessageResource.getString("bus.1")); //곧 도착합니다. The bus will arrive soon.
				}
				else if ( "운행종료".equals(time) ) {
					map.put("BUS_ARRIVE", "Bus operation has been terminated");
				}
				else {
					Matcher m = pattern.matcher(time);
					String min = null;
					String sec = null;
					if(m.find())
			        {
						min = m.group(1);
						sec = m.group(3);
			        }
					
					if ( min != null || sec != null ) {
						if ( min != null && sec != null ) {
							map.put("BUS_ARRIVE", String.format(MessageResource.getString("bus.3"), Integer.parseInt(min), Integer.parseInt(sec))); // min + "분 " + sec + "초 후에 도착합니다."
						}
						else {
							map.put("BUS_ARRIVE", String.format(MessageResource.getString("bus.2"), Integer.parseInt(sec))); // sec + "초 후에 도착합니다."
						}
					}
					else {
						map.put("BUS_ARRIVE", "Not found");
					}
				}
			}
			else {
				map.put("BUS_ARRIVE", "Not found");
			}
			
			exeResult = map;
		}catch(Exception ex) {
			ex.printStackTrace();
		}

		return exeResult;
	}
}
