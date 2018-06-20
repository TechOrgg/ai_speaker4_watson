package com.mycode;

import java.util.HashMap;
import java.util.Map;

import com.klab.ctx.ConversationSession;
import com.klab.svc.CommChannel;
import com.svc.Message;

/**
 * @author 최의신 (choies@kr.ibm.com)
 * 
 * 음악재생 중지 요청을 처리한다.
 *
 */
@SuppressWarnings("rawtypes")
public class BreakAction extends MyBaseAction
{
	/* (non-Javadoc)
	 * @see com.klab.svc.BaseAction#execute(java.lang.String, java.util.Map)
	 */
	@Override
	protected Object executeAction(String actionId, Map params, ConversationSession session, CommChannel commChannel)
	{
		Object exeResult = null;
		
		try
		{
			final Map<String, String> result = new HashMap<String, String>();

			result.put("BREAK", "");
			
			sendMessage(commChannel, Message.CHANNEL_SOUND, Message.MESSAGE_STOP_MUSIC, "BREAK");

			exeResult = result;				
		}catch(Exception ex) {
			ex.printStackTrace();
		}

		return exeResult;
	}
	
}
