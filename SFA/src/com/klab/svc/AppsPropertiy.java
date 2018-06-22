package com.klab.svc;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * @author 최의신 (choies@kr.ibm.com)
 * 
 * 파일에서 환경정보를 읽어 들인다.
 *
 */
public class AppsPropertiy extends Properties
{
	public static class ActionEntry
	{
		public String actionId;
		public String classQName;
		
		public String toString()
		{
			return actionId + "::" + classQName;
		}
	}
	
	private static AppsPropertiy inc;
	
	/**
	 * @return
	 */
	public static AppsPropertiy getInstance()
	{
		if ( inc == null )
			inc = new AppsPropertiy();

		return inc;
	}
	
	private AppsPropertiy()
	{
		try
		{
			load(AppsPropertiy.class.getResourceAsStream("/apps.conf"));
		}catch(Exception x) {
			x.printStackTrace();
		}
	}
	
	/**
	 * @param source
	 */
	public void loadConfig(InputStream source)
	{
		try
		{
			if ( source != null)
				load(source);
		}catch(Exception x) {
			x.printStackTrace();
		}
	}
	
	/**
	 * 등록된 Action 정보를 반환한다.
	 * @return
	 */
	public List<ActionEntry> getActionList()
	{
		List<ActionEntry> list = new ArrayList<ActionEntry>();
		
		for(Enumeration<Object> e = keys(); e.hasMoreElements(); )
		{
			String key = e.nextElement().toString();
			if ( key.startsWith("ac.") )
			{
				ActionEntry ae = new ActionEntry();
				ae.actionId = key.substring(3);
				ae.classQName = getProperty(key);
				
				list.add(ae);
			}
		}
		
		return list;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Properties#getProperty(java.lang.String)
	 */
	public String getProperty(String key)
	{
		String val = System.getenv(key);
		if ( val != null )
			return val.trim();
		else
			return super.getProperty(key);
	}

	/**
	 * @param prop
	 * @return
	 */
	public int getIntProperty(String prop)
	{
		int val = 0;
		try { val = Integer.parseInt(getProperty(prop)); }catch(Exception ig) {}
		return val;
	}

	/**
	 * @param prop
	 * @return
	 */
	public double getDoubleProperty(String prop)
	{
		double val = 0;
		try { val = Double.parseDouble(getProperty(prop)); }catch(Exception ig) {}
		return val;
	}
}
