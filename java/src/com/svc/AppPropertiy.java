package com.svc;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * @author 최의신 (choies@kr.ibm.com)
 *
 * apps.conf 파일에서 정보를 읽어 들인다.
 */
public class AppPropertiy extends Properties
{
	private static AppPropertiy inc;
	
	/**
	 * @return
	 * @throws Exception
	 */
	public static AppPropertiy getInstance()
	{
		if ( inc == null )
		{
			inc = new AppPropertiy();
			
			try
			{
				FileInputStream fin = new FileInputStream("resources/apps.conf");
				inc.load(fin);
				fin.close();
			}catch(Exception x) {
				x.printStackTrace();
			}
		}
		
		return inc;
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
