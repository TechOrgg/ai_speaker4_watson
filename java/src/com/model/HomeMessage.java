package com.model;

/**
 * @author 최의신 (choies@kr.ibm.com)
 * 
 * 시스템을 구성하는 스레드간 전달되는 메시지를 정의한다.
 *
 */
public class HomeMessage
{
	private MessageEnum messageId;
	private Object messageData;

	public HomeMessage(MessageEnum id, Object data)
	{
		this.messageId = id;
		this.messageData = data;
	}

	public MessageEnum getMessageId() {
		return messageId;
	}


	public void setMessageId(MessageEnum messageId) {
		this.messageId = messageId;
	}


	public Object getMessageData() {
		return messageData;
	}

	public void setMessageData(Object messageData) {
		this.messageData = messageData;
	}
}
