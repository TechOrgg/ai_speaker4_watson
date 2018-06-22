package com.klab.svc;

import com.klab.ctx.ConversationLogInfo;

/**
 * @author 최의신 (choies@kr.ibm.com)
 * 
 * 대화를 저장하는 클래스가 구현해야 하는 인터페이스
 *
 */
public interface ILogger
{
	/**
	 * @param session 대화정보를 저장하고 있는 클래스
	 * @throws Exception
	 */
	public void insertLog(ConversationLogInfo info) throws Exception;
}
