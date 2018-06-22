package com.klab.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author 최의신 (choies@kr.ibm.com)
 * 
 * 관계형 데이터베이스 (RDB)
 *
 */
public class AdapterUtil
{
	/**
	 * @param prepareSql
	 * @param parm
	 * @param rsCallback
	 */
	public static void executeQuery(String prepareSql, Object [] parm, IResultSet rsCallback)
	{
		PreparedStatement pst = null;
		ResultSet rs = null;
		try
		{
			pst = RDBAdapter.getInstance().getDB().prepareStatement(prepareSql);
			
			for(int i = 0; i < parm.length; i++)
			{
				Object o = parm[i];
				if ( o instanceof Integer )
				{
					pst.setInt(i+1, (Integer)o);
				}
				else if ( o instanceof Long )
				{
					pst.setLong(i+1, (Long)o);
				}
				else {
					pst.setString(i+1, o.toString());
				}
			}
			
			rs = pst.executeQuery();
			if ( rsCallback != null )
				rsCallback.fetch(rs);
			
		}catch(Exception ex) {
			ex.printStackTrace();
		}finally{
			try{rs.close();}catch(Exception ig){}
			try{pst.close();}catch(Exception ig){}
		}
	}
	
	/**
	 * @param sqlString
	 * @param rsCallback
	 */
	public static void executeQuery(String sqlString, IResultSet rsCallback)
	{
		Statement st = null;
		ResultSet rs = null;
		try
		{
			st = RDBAdapter.getInstance().getDB().createStatement();
			rs = st.executeQuery(sqlString);
			if ( rsCallback != null )
				rsCallback.fetch(rs);
			
		}catch(Exception ex) {
			ex.printStackTrace();
		}finally{
			try{rs.close();}catch(Exception ig){}
			try{st.close();}catch(Exception ig){}
		}
	}
	
	/**
	 * @param prepareSql
	 * @param parm
	 */
	public static void executeupdate(String prepareSql, Object [] parm)
	{
		PreparedStatement pst = null;
		try
		{
			pst = RDBAdapter.getInstance().getDB().prepareStatement(prepareSql);
			
			for(int i = 0; i < parm.length; i++)
			{
				Object o = parm[i];
				if ( o instanceof Integer )
				{
					pst.setInt(i+1, (Integer)o);
				}
				else if ( o instanceof Long )
				{
					pst.setLong(i+1, (Long)o);
				}
				else {
					pst.setString(i+1, o.toString());
				}
			}
			
			pst.executeUpdate();
		}catch(Exception ex) {
			ex.printStackTrace();
		}finally{
			try{pst.close();}catch(Exception ig){}
		}
	}
}
