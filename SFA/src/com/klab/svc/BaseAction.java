package com.klab.svc;

import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.klab.ctx.ConversationSession;

/**
 * @author 최의신 (choies@kr.ibm.com)
 *
 */
public abstract class BaseAction implements IAction
{
	@Override
	public JsonObject doAction(String actionId, Map<String, Object> params, ConversationSession session, CommChannel channel)
	{
		JsonObject result = new JsonObject();
		
		Gson gson = new Gson();
		JsonElement jsonElement = gson.toJsonTree(execute(actionId, params, session, channel));
		result.add("result", jsonElement);
		
		return result;
	}
	
	/**
	 * @param actionId
	 * @param params
	 * @return
	 */
	protected abstract Object execute(String actionId, Map<String, Object> params, ConversationSession session, CommChannel channel);
}
