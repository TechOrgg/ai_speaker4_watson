package com.klab.svc;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * @author 최의신 (choies@kr.ibm.com)
 *
 */
public class CommChannel
{
	private Map<String, BlockingQueue<ChannelMessage>> channels = new HashMap<String, BlockingQueue<ChannelMessage>>();
	
	public CommChannel(){
	}
	
	public void addChannel(String channelId, BlockingQueue<ChannelMessage> queue)
	{
		channels.put(channelId, queue);
	}
	
	public BlockingQueue<ChannelMessage> getChannel(String channelId)
	{
		return channels.get(channelId);
	}
}
