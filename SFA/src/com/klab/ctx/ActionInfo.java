package com.klab.ctx;

import java.util.Map;

/**
 * @author 최의신 (choies@kr.ibm.com)
 *
 */
@SuppressWarnings("rawtypes")
public class ActionInfo
{
	private String actionId;
	private String actionType;
	private Map actionParams;
	public String getActionId() {
		return actionId;
	}
	public void setActionId(String actionId) {
		this.actionId = actionId;
	}
	public String getActionType() {
		return actionType;
	}
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
	public Map getActionParams() {
		return actionParams;
	}
	public void setActionParams(Map actionParams) {
		this.actionParams = actionParams;
	}
}
