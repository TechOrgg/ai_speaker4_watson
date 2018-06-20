package com.svc;

import java.util.Locale;
import java.util.ResourceBundle;

import com.klab.svc.AppsPropertiy;

/**
 * @author 최의신 (choies@kr.ibm.com)
 * 
 * 국가별 메시지 파일을 처리한다.(영어, 한글)
 *
 */
public class MessageResource
{
	private static final String RESOURCE_NAME = "Message"; 
			
	static
	{
		String loc = AppsPropertiy.getInstance().getProperty("locale");
		if ( loc == null )
			loc = "ko";
		Locale locale = new Locale(loc, "");
		rb = ResourceBundle.getBundle(RESOURCE_NAME, locale);
	}
	
	
	private static ResourceBundle rb;

	/**
	 * @param name
	 * @return
	 */
	public static String getString(String name)
	{
		return rb.getString(name);
	}
}
