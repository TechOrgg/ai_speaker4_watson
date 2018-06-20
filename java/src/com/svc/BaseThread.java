package com.svc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 최의신 (choies@kr.ibm.com)
 *
 * 특정 기능을 수행하는 스레드의 기본 클래스
 */
public class BaseThread extends Thread
{
	protected Logger logger = LoggerFactory.getLogger(BaseThread.class);
	
	protected boolean running = false;
	protected boolean error;
	protected String errorString;
	protected String startTime;
	
	public void shutdown()
	{
		running = false;
	}
	
	/* (non-Javadoc)
	 * @see com.choi.pi.svr.IServerError#isError()
	 */
	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	/* (non-Javadoc)
	 * @see com.choi.pi.svr.IServerError#getErrorString()
	 */
	public String getErrorString() {
		return errorString;
	}

	public void setErrorString(String errorString) {
		this.errorString = errorString;
	}
}
