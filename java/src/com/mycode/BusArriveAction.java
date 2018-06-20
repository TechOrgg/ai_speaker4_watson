package com.mycode;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.klab.ctx.ConversationSession;
import com.klab.svc.CommChannel;
import com.svc.MessageResource;

/**
 * @author 최의신 (choies@kr.ibm.com)
 * 
 * 인천 11번 버스에 대한 버스 도착정보를 처리한다.
 *
 */
@SuppressWarnings("rawtypes")
public class BusArriveAction extends MyBaseAction
{
	private static final String URL = "http://bus.incheon.go.kr/iwcm/retrievebusstopcararriveinfo.laf?bstopid=165000735&routeid=16500001";
	private static final String NUMBER = "11";
	private static final String SELECTOR = "#cont1 > tbody > tr > td > div > table:last-child td";
	
	public String busArrive()
	{
		String str = "0000";
		
		try
		{
			Document doc = Jsoup.connect(URL).get();
			Elements info = doc.select(SELECTOR);
			
			for(int i = 0; i < info.size()/6; i++)
			{
				String num = info.get(i*6).text();
				if ( NUMBER.equals(num) )
				{
		            String data = info.get(i*6+5).text();
		            int mpos = data.indexOf("분");
		            int spos = data.indexOf("초");
		            int min = 0;
		            int sec = 0;
		            
		            if ( mpos != -1 )
		            {
		                min = Integer.parseInt(data.substring(0, mpos).trim());
		                
		                if ( spos != -1 )
		                {
		                    sec = Integer.parseInt(data.substring(mpos+1, spos).trim());
		                }
		            }
		            else {
		                if ( spos != -1 )
		                {
		                    sec = Integer.parseInt(data.substring(0, spos).trim());
		                }
		            }

		            str = "";
		            if ( min < 10 ) str += "0";
		            str += min;
		            if ( sec < 10 ) str += "0";
		            str += sec;
				}
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		
		return str;
	}
	
	/* (non-Javadoc)
	 * @see com.klab.svc.BaseAction#execute(java.lang.String, java.util.Map)
	 */
	@Override
	protected Object executeAction(String actionId, Map params, ConversationSession session, CommChannel channel)
	{
		int min = 0;
		int sec = 0;
		String time = busArrive();
		
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
}
