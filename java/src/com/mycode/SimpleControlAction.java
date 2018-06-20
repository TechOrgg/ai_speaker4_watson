package com.mycode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ibm.watson.developer_cloud.conversation.v1.model.RuntimeEntity;
import com.klab.ctx.ConversationSession;
import com.klab.svc.CommChannel;
import com.svc.Message;
import com.svc.MessageResource;
import com.utils.SqlSessionManager;

/**
 * @author 최의신 (choies@kr.ibm.com)
 * 
 * 사용자 요청에 부합되는 리모콘 데이터를 IoT 디바이스로 전송한다. 이 때 MQTT를 사용한다.
 *
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class SimpleControlAction extends MyBaseAction
{
	
	/* (non-Javadoc)
	 * @see com.klab.svc.BaseAction#execute(java.lang.String, java.util.Map)
	 */
	@Override
	protected Object executeAction(String actionId, Map params, ConversationSession session, CommChannel channel)
	{
		Object exeResult = null;

		RuntimeEntity target = findEntity(session, "target");
		RuntimeEntity action = findEntity(session, "action");
		RuntimeEntity temp = findEntity(session, "temp");
		RuntimeEntity channelId = findEntity(session, "channel");
		
		if (target != null )
			System.out.println(">> @DEV_CNTR[target] " + target.getEntity() + "/" + target.getValue() );
		if (action != null )
			System.out.println(">> @DEV_CNTR[action] " + action.getEntity() + "/" + action.getValue() );
		if ( temp != null )
			System.out.println(">> @DEV_CNTR[number] " + temp.getEntity() + "/" + temp.getValue() );
		if ( channelId != null )
			System.out.println(">> @DEV_CNTR[channel] " + channelId.getEntity() + "/" + channelId.getValue() );
		
		if ( action != null && target != null )
		{
			if ( "tv".equals(target.getValue()) || "volumn".equals(target.getValue()) || "light".equals(target.getValue()) ) {
				exeResult = controlDevice(action.getValue(), target.getValue(), channel);
			}
			else if ( "aircon".equals(target.getValue()) ) {
				if ( temp != null ) {
					exeResult = controlDevice(action.getValue(), temp.getValue(), channel);
				}
				else {
					exeResult = controlDevice(action.getValue(), target.getValue(), channel);
				}
			}
		}
		else if ( action != null && channelId != null ) {
			exeResult = controlDevice(action.getValue(), channelId.getValue(), channel);
		}
		else {
			Map result = new HashMap();
			result.put("MESSAGE", MessageResource.getString("no.device"));
			exeResult = result;				
		}

		return exeResult;
	}
	

	/**
	 * @param action
	 * @param target
	 * @return
	 */
	private Object controlDevice(String action, String target, CommChannel commChannel)
	{
		Object exeResult = null;
		
		try
		{
			JsonObject root = new JsonObject();
			JsonArray cntrList = new JsonArray();
			root.add("devControl", cntrList);

			JsonObject targetDev = new JsonObject();

			/*
			 * 리모콘 키 정보
			 */
			Map parm = new HashMap();
			parm.put("actionVal", action);
			parm.put("targetVal", target);
			List<Map> irKey = SqlSessionManager.getSqlMapClient().queryForList("MYHOME.selectIrKey", parm);

			if ( irKey.size() > 0 ) {
				JsonObject payload = new JsonObject();
				JsonArray cmd = new JsonArray();
				payload.add("cmd", cmd);
				
				String [] key = irKey.get(0).get("irKey").toString().split(",");
				
				getSingleIR(cmd, key);
				
				targetDev.add("PAYLOAD", payload);
				
				/*
				 * 토픽
				 */
				parm = new HashMap();
				parm.put("devId", irKey.get(0).get("devId").toString());
				List<Map> topic = SqlSessionManager.getSqlMapClient().queryForList("MYHOME.selectTopic", parm);
				
				targetDev.addProperty("TOPIC", topic.get(0).get("subTopic").toString());
				
				cntrList.add(targetDev);
			}
			
			final Map result = new HashMap();
			
			if ( cntrList.size() > 0 ) {
				System.out.println("* MQTT DATA>> " + root.toString());
				sendMessage(commChannel, Message.CHANNEL_BROKER, Message.MESSAGE_CONTROL, root);
				result.put("MESSAGE", "");
			}
			else {
				result.put("MESSAGE", MessageResource.getString("no.device")); // "제어 대상이 없습니다"
			}
			
			exeResult = result;				
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		
		return exeResult;
		
	}
	
	/**
	 * @param cmd
	 * @param key
	 */
	private void getSingleIR(JsonArray cmd, String [] key) throws Exception
	{
		Map parm = new HashMap();
		
		for(int i = 0; i < key.length; i++)
		{
			parm.clear();
			parm.put("key", key[i]);

			List keyList = SqlSessionManager.getSqlMapClient().queryForList("MYHOME.selectIrDataSingle", parm);
		
			Map m = (Map)keyList.get(0);
			
			Object rData = m.get("rawData");
			if ( rData != null ) {
				String data = rData.toString();
				if ( data.length() > 0 ) {
					JsonObject raw = new JsonObject();
					raw.addProperty("rawLen", Integer.parseInt(m.get("rawDataLen").toString()));
					raw.addProperty("rawData", data);

					cmd.add(raw);
				}
			}
		}
	}
}
