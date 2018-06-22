package com.klab.svc;

import java.util.Iterator;
import java.util.Map;

import com.google.gson.JsonObject;

/**
 * @author 최의신 (choies@kr.ibm.com)
 *
 */
public class Utils
{
	/**
	 * Map 객체를 JsonObject로 변환한다.
	 * 
	 * @param map
	 * @return
	 */
	public static JsonObject convertMapToJson(Map<String, Object> map)
	{
		JsonObject json = new JsonObject();
		
		if ( map != null )
		{
			for(Iterator<String> it = map.keySet().iterator(); it.hasNext(); )
			{
				String key = it.next();
				Object val = map.get(key);
				if ( val != null )
					json.addProperty(key, val.toString());
			}
		}
		
		return json;
	}	
	
    /**
     * 지정된 이름의 클래스를 로드한다.
     *
     * @param className 로그할 클래스 이름
     * @return 로드한 클래스 인스턴
     * @throws Exception className이 null 이거나, 지정된 클래스를 로드할 수 없는 경
     *
     */
    public static Object loadClass(String className) throws Exception {
        if (className == null)
            throw new Exception("className = null");

        Object o = null;

        try {
            o = Class.forName(className).newInstance();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        return o;
    }
}
