package com.mycode;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import com.ibm.watson.developer_cloud.conversation.v1.model.RuntimeEntity;
import com.klab.ctx.ConversationSession;
import com.klab.svc.BaseAction;
import com.klab.svc.ChannelMessage;
import com.klab.svc.CommChannel;
import com.svc.Message;

/**
 * @author 최의신 (choies@kr.ibm.com)
 *
 * 기본 클래스를 확장하여 액션 구현을 위한 공통 기능을 제공한다. 
 */
@SuppressWarnings("rawtypes")
public abstract class MyBaseAction extends BaseAction
{
	/**
	 * @param session
	 * @param name
	 * @return
	 */
	protected RuntimeEntity findEntity(ConversationSession session, String name)
	{
		List list = session.getEntities();
		
		for(int i = 0; i < list.size(); i++)
		{
			RuntimeEntity e = (RuntimeEntity)list.get(i);
			if ( name.equals(e.getEntity()) ) {
				return e;
			}
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	protected void sendMessage(CommChannel channel, String channelName, int messageId, Object data)
	{
		if ( channel == null )
			return;
		
		BlockingQueue<ChannelMessage> queue = channel.getChannel(channelName);
		if ( queue != null ) {
			try {
				ChannelMessage msg = new ChannelMessage(messageId);
				msg.setMessageData(data);
				queue.put(msg);
			}catch(Exception e) {}
		}
	}
	
	
	/* (non-Javadoc)
	 * @see com.klab.svc.BaseAction#execute(java.lang.String, java.util.Map, com.klab.ctx.ConversationSession, com.klab.svc.CommChannel)
	 */
	protected Object execute(String actionId, Map params, ConversationSession session, CommChannel channel)
	{
		Object rs = executeAction(actionId, params, session, channel);
		
		sendMessage(channel, Message.CHANNEL_LED, Message.MESSAGE_COMPLETED, null);
		
		return rs;
	}
	
	/**
	 * @param actionId
	 * @param params
	 * @param session
	 * @param channel
	 * @return
	 */
	protected abstract Object executeAction(String actionId, Map params, ConversationSession session, CommChannel channel);
}
