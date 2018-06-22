package com.klab.svc;

/**
 * @author 최의신 (choies@kr.ibm.com)
 *
 */
public class ChannelMessage
{
	private int messageId;
	private Object messageData;
	
	public ChannelMessage(int id) {
		this.messageId = id;
	}
	
	/**
	 * @return the messageId
	 */
	public int getMessageId() {
		return messageId;
	}
	/**
	 * @param messageId the messageId to set
	 */
	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}
	/**
	 * @return the messageData
	 */
	public Object getMessageData() {
		return messageData;
	}
	/**
	 * @param messageData the messageData to set
	 */
	public void setMessageData(Object messageData) {
		this.messageData = messageData;
	}
}
