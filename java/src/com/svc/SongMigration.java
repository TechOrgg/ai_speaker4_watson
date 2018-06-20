package com.svc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.klab.svc.AppsPropertiy;
import com.utils.SqlSessionManager;

/**
 * @author 최의신 (choies@kr.ibm.com)
 * 
 * Media Server에서 입력한 MP3 메타 정보를 MySQL에 저장한다.
 *
 */
public class SongMigration
{
	/**
	 * @throws Exception
	 */
	private Connection connect() throws Exception
	{
		Connection sqlite = null;
		
		try
		{
			java.sql.DriverManager.registerDriver(new org.sqlite.JDBC());
			
			// song3.db 파일의 경로
			String dbFile = AppsPropertiy.getInstance().getProperty("songs.file");
			StringBuffer url = new StringBuffer();

			url.append ( "jdbc:sqlite:" ).append(dbFile);
			
			System.out.println("[SQLite] " + url);

			sqlite = java.sql.DriverManager.getConnection (url.toString()); 
			
			System.out.println("[SQLite] Connected ...");
		}catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		
		return sqlite;
	}
	
	/**
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void migration() throws Exception
	{
		Connection conn = null;
		
		try
		{
			conn = connect();
			
			/*
			 * 이전 데이터 삭제
			 */
			SqlSessionManager.getSqlMapClient().delete("MYHOME.deleteSong");

			/*
			 * Batch
			 */
			SqlSessionManager.getSqlMapClient().startTransaction();
			SqlSessionManager.getSqlMapClient().startBatch();

			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT id,fname,title,artist,album,genre,type FROM songs");
			int index = 1;
			Map parm = new HashMap();
			while(rs.next())
			{
				parm.put("id", rs.getInt("id"));
				parm.put("fname", rs.getString("fname"));
				parm.put("title", rs.getString("title").replaceAll("\\s",""));
				parm.put("artist", rs.getString("artist").replaceAll("\\s",""));
				parm.put("album", rs.getString("album"));
				parm.put("genre", rs.getString("genre"));
				parm.put("type", rs.getString("type"));
				
				SqlSessionManager.getSqlMapClient().insert("MYHOME.insertSong", parm);
				
		        if (index++ % 500 == 0) {
		        	SqlSessionManager.getSqlMapClient().executeBatch();
		            SqlSessionManager.getSqlMapClient().startBatch();
		        }
			}
			
			rs.close();
			st.close();
		}catch(Exception ex) {
			throw ex;
		}finally{
			if ( conn != null )
				try{ conn.close(); }catch(Exception e) {}
			
			try
			{
				SqlSessionManager.getSqlMapClient().executeBatch();
				SqlSessionManager.getSqlMapClient().commitTransaction();
			    SqlSessionManager.getSqlMapClient().endTransaction();
			}catch(Exception ex) {
				ex.printStackTrace();
			}
		}		
	}
	
	/**
	 * @param args
	 */
	public static void main(String [] args)
	{
		try
		{
			SongMigration sm = new SongMigration();
			sm.migration();
			System.out.println("Completed....");
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
}
