package com.klab.data;

import java.sql.Connection;
import java.util.Properties;

import com.klab.svc.AppsPropertiy;

/**
 * @author 최의신 (choies@kr.ibm.com)
 * 
 * 관계형 데이터베이스 (RDB)
 *
 */
public class RDBAdapter
{
	private static RDBAdapter ins;
	private Connection database;
	protected boolean connect = false;
	
	public static RDBAdapter getInstance()
	{
		if ( ins == null )
			ins = new RDBAdapter();
		
		return ins;
	}
	
	private RDBAdapter()
	{
		_connection();
	}
	
	/**
	 * 
	 */
	private void _connection()
	{
		String driver = AppsPropertiy.getInstance().getProperty("jdbc.driver");
		String jdbcUrl = AppsPropertiy.getInstance().getProperty("jdbc.url");
		String userId = AppsPropertiy.getInstance().getProperty("jdbc.userid");
		String passwd = AppsPropertiy.getInstance().getProperty("jdbc.passwd");
		
		try
		{
			Class.forName(driver).newInstance();
			
			Properties info = new Properties();
			info.put ("user", userId);
			info.put ("password", passwd);

			database = java.sql.DriverManager.getConnection (jdbcUrl , info); 

			connect = true;
			
			System.out.println("@.@ RDB Connected...");
		}catch(Throwable ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	public boolean isConnect()
	{
		return connect;
	}

	/**
	 * @return
	 */
	public Connection getDB()
	{
		return database;
	}
}
