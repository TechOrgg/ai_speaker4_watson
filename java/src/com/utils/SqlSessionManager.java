package com.utils;

import java.net.URL;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

/**
 * @author 최의신 (choies@kr.ibm.com)
 *
 * iBatis
 */
public class SqlSessionManager
{
	private static SqlMapClient instance;
	
	/**
	 * 
	 */
	private static void _connection()
	{
		try
		{
			URL u = SqlSessionManager.class.getResource("/sql-map-config.xml");
			instance = SqlMapClientBuilder.buildSqlMapClient(u.openStream()); 
		}catch(Throwable ex) {
			throw new RuntimeException(ex);
		}		
	}
	
	/**
	 * @return
	 */
	public static SqlMapClient getSqlMapClient()
	{
		if ( instance == null )
			_connection();
		
		return instance;
	}
	
}
