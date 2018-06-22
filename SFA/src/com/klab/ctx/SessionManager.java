package com.klab.ctx;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author 최의신 (choies@kr.ibm.com)
 *
 */
public class SessionManager
{
	public static final int CONVERSATION_TIMEOUT = 5 * 60 * 1000;	// 5분
	public static final int CLEANUP_INTERVAL = 60 * 60 * 1000;	// 60분
	
	private static SessionManager ins;
	private Map<String, ConversationSession> sessions;
	private long lastCleanup;
	
	private SessionManager()
	{
		sessions = new HashMap<String, ConversationSession>();
		lastCleanup = System.currentTimeMillis();
	}
	
	public static SessionManager getInstance()
	{
		if ( ins == null )
		{
			ins = new SessionManager();
		}
		
		return ins;
	}
	
	/**
	 * @param sessionId
	 * @return
	 */
	public ConversationSession getSession(String sessionId)
	{
		ConversationSession cs = sessions.get(sessionId);
		
		if ( cs != null ) {
			if ( System.currentTimeMillis() - cs.getContextUpdateTime() >= SessionManager.CONVERSATION_TIMEOUT ) {
				cs.clear();
			}
		}
		else {
			cs = new ConversationSession();
			sessions.put(sessionId, cs);
		}

		if ( System.currentTimeMillis() - lastCleanup >= CLEANUP_INTERVAL )
		{
			Runnable r = new Runnable() {
				@Override
				public void run() {
					cleanSession();
				}
			};
			
			(new Thread(r)).start();
		}
		
		return cs;
	}
	
	/**
	 * 
	 */
	private void cleanSession()
	{
		for(Iterator<String> it = sessions.keySet().iterator(); it.hasNext(); )
		{
			String key = it.next();
			ConversationSession cs = sessions.get(key);
			if ( System.currentTimeMillis() - cs.getContextUpdateTime() >= (CONVERSATION_TIMEOUT*2) ) {
				sessions.remove(key);
			}
		}
	}
}
