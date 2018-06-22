package com.klab.svc;

import java.util.Map;

import com.google.gson.JsonObject;
import com.klab.ctx.ConversationSession;

/**
 * @author 최의신 (choies@kr.ibm.com)
 *
 */
public interface IAction
{
	/**
	 * @param actionId 외부 액션 ID (쿼리 또는 Watson 서비스 호출)
	 * @param params 액션의 파라미터
	 * @return 액션 결과를 맵 형태로 반환한다. 이 결과는 Context의 actionResult에 저장된다.
	 */
	public JsonObject doAction(String actionId, Map<String, Object> params, ConversationSession session, CommChannel channel);
}
