package com.klab.ctx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibm.watson.developer_cloud.conversation.v1.model.RuntimeEntity;
import com.ibm.watson.developer_cloud.conversation.v1.model.RuntimeIntent;

/**
 * @author 최의신 (choies@kr.ibm.com)
 *
 */
public class ConversationLogInfo
{
	private List<RuntimeIntent> intents;
	private List<RuntimeEntity> entities;
	private List<String> outputString;
	private String inputString;
	private List<ActionInfo> actionInfo;
	private String conversationId;
	private String userId;

	public ConversationLogInfo()
	{
		actionInfo = new ArrayList<ActionInfo>();
		intents = new ArrayList<RuntimeIntent>();
		entities = new ArrayList<RuntimeEntity>();
		outputString = new ArrayList<String>();
	}

	public String getConversationId() {
		return conversationId;
	}

	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public List<RuntimeIntent> getIntents() {
		return intents;
	}

	public void putIntents(List<RuntimeIntent> intents) {
		this.intents.addAll(intents);
	}

	public List<RuntimeEntity> getEntities() {
		return entities;
	}

	public void putEntities(List<RuntimeEntity> entities) {
		this.entities.addAll(entities);
	}

	public List<String> getOutputString() {
		return outputString;
	}

	public void putOutputString(List<String> outputString) {
		this.outputString.addAll(outputString);
	}

	public String getInputString() {
		return inputString;
	}

	public void setInputString(String inputString) {
		this.inputString = inputString;
	}

	public List<ActionInfo> getActionInfo() {
		return actionInfo;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addActionInfo(String id, String type, Map parm)
	{
		ActionInfo ai = new ActionInfo();
		ai.setActionId(id);
		ai.setActionType(type);
		Map p = new HashMap();
		p.putAll(parm);
		ai.setActionParams(p);
		
		this.actionInfo.add(ai);
	}
}
