package com.klab.svc;

import com.klab.ctx.ConversationLogInfo;

/**
 * @author 최의신 (choies@kr.ibm.com)
 * 
 * 대화 로그를 콘솔로 출력한다.
 *
 */
public class ConsoleLogger implements ILogger {

	@Override
	public void insertLog(ConversationLogInfo session) throws Exception
	{
		System.out.println("==[INPUT]");
		System.out.println(session.getInputString());
		
		System.out.println("==[OUTPUT]");
		for(int i =0 ; i < session.getOutputString().size() ; i++)
			System.out.println(session.getOutputString().get(i));
	}
}
