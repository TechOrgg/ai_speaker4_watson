package com.klab.ctx;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

/**
 * @author 최의신 (choies@kr.ibm.com)
 *
 */
public class ContextEntity
{
	private List<JsonObject> entityList = new ArrayList<JsonObject>();
	
	/**
	 * 엔티티 이력을 삭제한다.
	 */
	public void clear()
	{
		entityList.clear();
	}
	
	/**
	 * @param entityName
	 * @param entityValue
	 * @param confidence
	 */
	public void addEntity(String entityName, String entityValue, double confidence)
	{
		JsonObject obj = new JsonObject();
		
		entityList.add(obj);
	}

	
	
}
