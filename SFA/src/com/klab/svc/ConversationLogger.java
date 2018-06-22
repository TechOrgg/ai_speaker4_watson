package com.klab.svc;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.klab.ctx.ConversationLogInfo;

/**
 * @author 최의신 (choies@kr.ibm.com)
 *
 */
public class ConversationLogger extends Thread
{
	private BlockingQueue<ConversationLogInfo> logQueue;
	private boolean running = true;
	private ILogger logger;
	
	public ConversationLogger()
	{
		logQueue = new ArrayBlockingQueue<ConversationLogInfo>(100);
	}
	
	/**
	 * 
	 */
	public void shutdown()
	{
		running = false;
	}
	
	/**
	 * 대화를 저장한다.
	 * @param log
	 */
	public void addDialog(ConversationLogInfo log)
	{
		try {
			logQueue.put(log);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 대화를 저장할 구현 클래스를 지정한다.
	 * @param log
	 */
	public void setLogger(ILogger log)
	{
		this.logger = log;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run()
	{
        while(running)
        {
        	try
        	{
        		ConversationLogInfo log = logQueue.poll(500, TimeUnit.MILLISECONDS);
            	if ( log != null && logger != null )
            	{
            		logger.insertLog(log);
            	}
        	}catch(Exception e) {
        		e.printStackTrace();
        	}
        }
	}
}
