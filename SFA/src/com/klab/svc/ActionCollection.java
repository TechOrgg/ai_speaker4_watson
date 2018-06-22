package com.klab.svc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.klab.svc.AppsPropertiy.ActionEntry;

/**
 * @author 최의신 (choies@kr.ibm.com)
 * 
 * 사용자 등록 Action 인스턴스를 생성한다.
 */
public class ActionCollection
{
	private Map<String, IAction> actions;
	
	public ActionCollection()
	{
		actions = new HashMap<String, IAction>();
		_init();
	}
	
	/**
	 * 새로운 액션을 수행할 객체를 추가한다.
	 * 객체는 IAction 인터페이스를 구현해야 한다.
	 */
	private void _init()
	{
		Map<String, Object> temp = new HashMap<String, Object>();
		List<ActionEntry> actList = AppsPropertiy.getInstance().getActionList();
		
		for(ActionEntry ae : actList)
		{
			try
			{
				Object o = temp.get(ae.classQName);
				if ( o == null )
				{
					o = Utils.loadClass(ae.classQName);
					if ( o != null)
						temp.put(ae.classQName, o);
				}
				
				if ( o != null )
					actions.put(ae.actionId, (IAction)o);
			}catch(Exception ig) {
				ig.printStackTrace();
			}
		}
	}
	
	/**
	 * 지정된 ID의 action 객체를 반환한다.
	 * 
	 * @param actionId
	 * @return
	 */
	public IAction getAction(String actionId)
	{
		IAction action = null;
		
		if ( actionId != null )
		{
			action = actions.get(actionId);
			if ( action == null )
				System.out.println("@.@ NOT FOUND : " + actionId);
		}
		
		return action;
	}
}
