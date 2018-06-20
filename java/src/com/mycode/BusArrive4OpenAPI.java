package com.mycode;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.json.XML;

import com.klab.ctx.ConversationSession;
import com.klab.svc.CommChannel;
import com.svc.MessageResource;

import org.json.JSONArray;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequest;

/**
 * @author 최의신 (choies@kr.ibm.com)
 *
 * getArrInfoByRoute API가 정상적을 동작하지 않음.
 * 
 */
@SuppressWarnings("rawtypes")
public class BusArrive4OpenAPI extends MyBaseAction
{
	private static final String ROUTE_ALL = "http://ws.bus.go.kr/api/rest/arrive/getArrInfoByRouteAll?serviceKey=%s&busRouteId=%s";
	private static final String ARR_INFO = "http://ws.bus.go.kr/api/rest/arrive/getArrInfoByRoute?serviceKey=%s&busRouteId=%s&stId=%s&ord=%s";
	
	
	/**
	 * @return
	 */
	public String busArrive(String routeId, int arsId)
	{
		String apiKey = "API KEY";
		String arrInfo = null;
		
		try
		{
			/*
			 * 정류장 ID 검색
			 */
			String stId = null;
			String staOrd = null;
			
			HttpRequest req = Unirest.get(String.format(ROUTE_ALL, apiKey, routeId));
			HttpResponse<String> res = req.asString();
			String xml = res.getBody();
			JSONObject body = XML.toJSONObject(xml);
					
			if (body.has("ServiceResult")) {
				JSONArray list = body.getJSONObject("ServiceResult").getJSONObject("msgBody").getJSONArray("itemList");
				for(int i = 0; i < list.length(); i++) {
					JSONObject o = list.getJSONObject(i);
					if ( arsId == o.getInt("arsId") ) {
						staOrd = o.get("staOrd").toString();
						stId = o.get("stId").toString();
						break;
					}
				}
			}
			
			System.out.println("@.@ stId = " + stId);
			System.out.println("@.@ staOrd = " + staOrd);

			if ( staOrd != null && stId != null ) {
				/*
				 * 버스 도착정보 조회
				 */
				req = Unirest.get(String.format(ARR_INFO, apiKey, routeId, stId, staOrd));
				res = req.asString();
				body = XML.toJSONObject(xml);
				
				if (body.has("ServiceResult")) {
					JSONArray info = body.getJSONObject("ServiceResult").getJSONObject("msgBody").getJSONArray("itemList");
					if ( info.length() > 0 ) {
						JSONObject o = info.getJSONObject(0);
						
						if ( o.has("arrmsg1") ) {
							String msg = o.getString("arrmsg1");
							System.out.println("@.@ " + msg);
						}
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
		int min = 0;
		int sec = 0;
		
		String routeId = (String)params.get("routeId");
		int arsId = Integer.parseInt((String)params.get("arsId"));
		String time = busArrive(routeId, arsId);
		
		Object exeResult = null;
		
		try
		{
			min = Integer.parseInt(time.substring(0, 2));
			sec = Integer.parseInt(time.substring(2));
			
			Map<String, String> map = new HashMap<String, String>();
			if ( min == 0 )
			{
				if ( sec == 0 )
					map.put("BUS_ARRIVE", MessageResource.getString("bus.1")); //곧 도착합니다. The bus will arrive soon.
				else
					map.put("BUS_ARRIVE", String.format(MessageResource.getString("bus.2"), sec)); // sec + "초 후에 도착합니다." // The bus will arrive in 30 seconds.
			}
			else {
				map.put("BUS_ARRIVE", String.format(MessageResource.getString("bus.3"), min, sec)); // min + "분 " + sec + "초 후에 도착합니다." // The bus will arrive in 3 minutes and 10 seconds.
			}
			
			exeResult = map;
		}catch(Exception ex) {
			ex.printStackTrace();
		}

		return exeResult;
	}
	
	public static void main(String [] args)
	{
		System.out.println((new BusArrive4OpenAPI()).busArrive("232000067", 17234));
	}
}
