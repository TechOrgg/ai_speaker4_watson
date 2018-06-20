package com.svc;

import java.util.concurrent.BlockingQueue;

import com.klab.svc.ChannelMessage;

/**
 * @author 최의신 (choies@kr.ibm.com)
 *
 */
public interface ITask
{
	public void execute(BlockingQueue<ChannelMessage> pub, ChannelMessage payload);
}
