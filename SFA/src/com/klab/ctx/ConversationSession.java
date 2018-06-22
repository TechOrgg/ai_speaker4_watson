package com.klab.ctx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ibm.watson.developer_cloud.conversation.v1.model.Context;
import com.ibm.watson.developer_cloud.conversation.v1.model.RuntimeEntity;
import com.ibm.watson.developer_cloud.conversation.v1.model.RuntimeIntent;
import com.klab.svc.Utils;

/**
 * @author 최의신 (choies@kr.ibm.com)
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ConversationSession
{
	private long contextUpdateTime;
	private Context context;
	private List<RuntimeIntent> intents;
	private List<RuntimeEntity> entities;
	private List<String> outputString;
	private String inputString;
	private JsonElement postResult;
	private String debug;
	private String warning;
	private Map<String, Object> properties = new HashMap<String, Object>();
	private String conversationId;
	
	public ConversationSession()
	{
		context = new Context();
		outputString = new ArrayList<String>();
		contextUpdateTime = System.currentTimeMillis();
	}

	/**
	 * @return
	 */
	public String getConversationId() {
		return conversationId;
	}

	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}

	/**
	 * @param name
	 * @param value
	 */
	public void addProperty(String name, Object value)
	{
		if ( name != null && value != null )
			properties.put(name, value);
	}
	
	/**
	 * @param name
	 * @return
	 */
	public Object getProperty(String name)
	{
		return properties.get(name);
	}
	
	/**
	 * @param name
	 */
	public void deleteProperty(String name)
	{
		properties.remove(name);
	}
	
	
	public List<RuntimeIntent> getIntents() {
		return intents;
	}

	public void setIntents(List<RuntimeIntent> intents) {
		this.intents = intents;
	}

	public void setEntities(List<RuntimeEntity> entities) {
		this.entities = entities;
	}

	public List<RuntimeEntity> getEntities() {
		return entities;
	}

	/**
	 * @return the warning
	 */
	public String getWarning() {
		return warning;
	}

	/**
	 * @param warning the warning to set
	 */
	public void setWarning(String warning) {
		this.warning = warning;
	}

	public JsonElement getPostResult() {
		return postResult;
	}

	public void setPostResult(JsonElement postResult) {
		this.postResult = postResult;
	}

	/**
	 * 
	 */
	public void reset()
	{
		outputString.clear();
		postResult = null;
		context.put("actionResult", new HashMap<String, String>());
	}

	/**
	 * 
	 */
	public void clear()
	{
		context.clear();
	}
	
	/**
	 * @return the context
	 */
	public Context getContext()
	{
		if ( System.currentTimeMillis() - contextUpdateTime >= SessionManager.CONVERSATION_TIMEOUT ) {
			context.clear();
			contextUpdateTime = System.currentTimeMillis();
		}
		
		return context;
	}
	
	/**
	 * @param context the context to set
	 */
	public void putAll(Map<String, Object> context) {
		this.context.putAll(context);
		contextUpdateTime = System.currentTimeMillis();
	}

	/**
	 * @param key
	 * @param value
	 */
	public void put(String key, Object value)
	{
		this.context.put(key, value);
		contextUpdateTime = System.currentTimeMillis();
	}


	/**
	 * @return the outputString
	 */
	public List<String> getOutputString() {
		return outputString;
	}
	/**
	 * @param outputString the outputString to set
	 */
	public void setOutputString(List<String> outputString) {
		this.outputString = outputString;
	}
	/**
	 * @return the inputString
	 */
	public String getInputString() {
		return inputString;
	}
	/**
	 * @param inputString the inputString to set
	 */
	public void setInputString(String inputString) {
		this.inputString = inputString;
	}
	
	/**
	 * @return
	 */
	public Object getActionResult()
	{
		Object obj = context.get("actionResult");
		
		if ( obj instanceof Map )
		{
			obj = Utils.convertMapToJson((Map)obj);
		}
		
		return obj;
	}
	
	/**
	 * @param rs
	 */
	public void setActionResult(JsonElement rs)
	{
		if ( rs != null )
			context.put("actionResult", rs);
		else
			context.put("actionResult", new JsonObject());
	}
	
	/**
	 * @return the contextUpdateTime
	 */
	public long getContextUpdateTime() {
		return contextUpdateTime;
	}

	/**
	 * @return the debug
	 */
	public String getDebug() {
		return debug;
	}

	/**
	 * @param debug the debug to set
	 */
	public void setDebug(String debug) {
		this.debug = debug;
	}
}
