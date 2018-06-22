package com.klab.svc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.stringtemplate.v4.ST;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ibm.watson.developer_cloud.conversation.v1.Conversation;
import com.ibm.watson.developer_cloud.conversation.v1.model.InputData;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageOptions;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.klab.ctx.ConversationLogInfo;
import com.klab.ctx.ConversationSession;

/**
 * @author 최의신 (choies@kr.ibm.com)
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class SimpleAppFrame
{
	static class ATTR
	{
		String attrName;
		String attrValue;
	}
	
	private String username;
	private String password;
	private String workspaceId;
	
	private Conversation service;
	private ActionCollection actionCollection;

	public SimpleAppFrame()
	{
		actionCollection = new ActionCollection();
	}
	
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
		_startService();
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
		_startService();
	}

	/**
	 * @return the workspaceId
	 */
	public String getWorkspaceId() {
		return workspaceId;
	}

	/**
	 * @param workspaceId the workspaceId to set
	 */
	public void setWorkspaceId(String workspaceId) {
		this.workspaceId = workspaceId;
	}

	private void _startService()
	{
		if ( username != null && password != null )
		{
			service = new Conversation("2018-02-16");
			service.setUsernameAndPassword(username, password);			
		}
	}
	
	/**
	 * 지정된 액션을 실행한다.
	 * 
	 * @param actionId
	 * @param action
	 * @param actionParam
	 * @return
	 */
	private JsonObject executeAction(String actionId, IAction action, Map actionParam, ConversationSession session, CommChannel channel)
	{
		Map<String, Object> parm = new HashMap<String, Object>();
		
		if ( actionParam != null )
			parm.putAll((Map)actionParam);

		return action.doAction(actionId, parm, session, channel);
	}
	
	/**
	 * @param json
	 * @return
	 */
	private List<ATTR> toList(JsonElement json)
	{
		List<ATTR> list = new ArrayList<ATTR>();

		if ( json != null )
		{
			JsonObject jobj = null;
			
			if ( json.isJsonArray() )
			{
				JsonArray arr = json.getAsJsonArray();
				if ( arr.size() > 0 )
					jobj = arr.get(0).getAsJsonObject();
			}
			else {
				jobj = json.getAsJsonObject();
			}

			if ( jobj != null )
			{
				for(Iterator<Entry<String, JsonElement>> it = jobj.entrySet().iterator(); it.hasNext(); )
				{
					Entry<String, JsonElement> e = it.next();
					ATTR a = new ATTR();
					a.attrName = e.getKey();
					a.attrValue = e.getValue().getAsString(); 
					list.add(a);
				}
			}
		}
		
		return list;
	}
	
	/**
	 * @param text
	 * @param parm
	 * @return
	 */
	private String replace(String text, List<ATTR> parm)
	{
		if ( parm == null )
			return text;
		
		ST temp = new ST(text, '%', '%');

		for(ATTR a : parm)
			temp.add(a.attrName, a.attrValue);

		return temp.render();
	}
	
	/**
	 * @param response
	 * @param session
	 */
	private void setOutput(MessageResponse response, ConversationSession session)
	{
		Object rs = response.getOutput().get("text");
		if ( rs instanceof ArrayList & ((ArrayList)rs).size() > 0 )
			session.getOutputString().addAll((ArrayList)rs);
	}
	
	/**
	 * @param session
	 * @param text
	 * @throws Exception
	 */
	public ConversationLogInfo message(ConversationSession session, String text, CommChannel channel) throws Exception
	{
		if ( service == null )
			throw new Exception("서비스를 사용할 수 없습니다.");
		
		session.setWarning(null);

		return _innerMessage(session, text, channel);
	}

	/**
	 * @param session
	 * @param text
	 * @throws Exception
	 */
	private ConversationLogInfo _innerMessage(ConversationSession session, String text, CommChannel channel) throws Exception
	{
		ConversationLogInfo log = new ConversationLogInfo();
		
		session.reset();
		session.setInputString(text);

		
		InputData input = new InputData.Builder(text).build();
		
		MessageOptions options = new MessageOptions.Builder(workspaceId)
				.input(input)
				.context(session.getContext())
				.build();

		MessageResponse response = service.message(options).execute();
//		System.err.println("@.@ --------------------------------------");
//		System.err.println(response);
//		System.err.println(response.getEntities());
//		System.err.println(response.getInput());
//		System.err.println("@.@ --------------------------------------");
		
		log.setConversationId(workspaceId);
		session.setConversationId(workspaceId);
		
		/*
		 * Intent, Entity
		 */
		session.setIntents(response.getIntents());
		session.setEntities(response.getEntities());
		
		Map<String, Object> output= response.getOutput();
		
		/*
		 * POST ACTION
		 */
		Map postAction = (Map)output.get("postAction");

		/*
		 * ACTION
		 */
		JsonObject actionResult = null;
		Map action = (Map)output.get("action");
		if ( action != null )
		{
			String type = (String)action.get("type");
			String actionId = (String)action.get("id");
			Map actionParams = (Map)action.get("params");
			
			log.addActionInfo(actionId, type, actionParams);

			IAction exAct = actionCollection.getAction(actionId);
			if ( exAct != null )
			{
				/*
				 * 지정된 Action을 수행한다.
				 */
				actionResult = executeAction(actionId, exAct, actionParams, session, channel);
				
				if ( "RECALL".equals(type) )
				{
					//System.out.println("@.@ CONTEXT:\n" + session.getContext());
					
					if (actionResult != null) {
						session.setActionResult(actionResult.get("result"));
						actionResult = null;
					}
					
					
					input = new InputData.Builder(text).build();
					
					options = new MessageOptions.Builder(workspaceId)
							.input(input)
							.context(session.getContext())
							.build();

					response = service.message(options).execute();

					//System.out.println("@.@ RECALL:\n" + response);

					/*
					 * 새로 정의된 POST ACTION이 있는지 확인한다.
					 */
					Object newAction = response.getContext().get("postAction");
					if ( newAction != null )
						postAction = (Map)newAction;
					
					/*
					 * Conversation 응답
					 */
					setOutput(response, session);
				}
				else {	// REPLACE
					List<ATTR> prmList = null;
					
					if ( actionResult != null )
						prmList = toList(actionResult.get("result"));
					
					/*
					 * 응답 결과의 변수를 값으로 대체한다.
					 */
					Object rs = response.getOutput().get("text");
					if ( rs instanceof ArrayList & ((ArrayList)rs).size() > 0 )
						for(String s : (ArrayList<String>)rs)
							session.getOutputString().add(replace(s, prmList));
				}
			}
			else {
				// 지정한 액션이 없는 경우
				session.setWarning("액션을 찾을 수 없습니다 [" + actionId + "]");
				setOutput(response, session);
			}
		}
		else
			setOutput(response, session);

		/*
		 * CONTEXT
		 */
		session.putAll(response.getContext());
		if (actionResult != null)
			session.setActionResult(actionResult.get("result"));
		else
			session.setActionResult(null);
		
		/*
		 * Intent, Entity
		 */
		session.setIntents(response.getIntents());
		session.setEntities(response.getEntities());
		
		//System.out.println("@.@ RESPONSE TEXT = " + session.getOutputString());
		
		/*
		 * POST ACTION을 수행한다.
		 */
		if ( postAction != null )
		{
			String postId = (String)postAction.get("id");
			Map postParams = (Map)postAction.get("params");
			
			log.addActionInfo(postId, "POST", postParams);

			IAction postAct = actionCollection.getAction(postId);
			if ( postAct != null )
			{
				JsonObject postResult = executeAction(postId, postAct, postParams, session, channel);
				if ( postResult != null )
					session.setPostResult(postResult.get("result"));
			}
			else {
				// 지정한 액션이 없는 경우
				session.setWarning("액션을 찾을 수 없습니다 [POST:" + postId + "]");
			}
		}
		
		session.setDebug(response.toString());
		
		log.setInputString(session.getInputString());
		log.putOutputString(session.getOutputString());
		log.putIntents(session.getIntents());
		log.putEntities(session.getEntities());
		
		return log;
	}
}
