package com.tools;

import java.util.EventObject;

/**
 * @author 최의신 (choies@kr.ibm.com)
 *
 */
public class ClickEventObject extends EventObject
{
	private String command;
	private Object data;
	
	public ClickEventObject(Object source, String cmd, Object data) {
		super(source);
		this.command = cmd;
		this.data = data;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
