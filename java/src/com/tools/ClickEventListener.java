package com.tools;

import java.util.EventListener;

/**
 * @author 최의신 (choies@kr.ibm.com)
 *
 */
public interface ClickEventListener extends EventListener {
	public void click(ClickEventObject obj);
}
