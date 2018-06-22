package com.ext.act;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.klab.ctx.ConversationSession;
import com.klab.data.AdapterUtil;
import com.klab.data.IResultSet;
import com.klab.svc.BaseAction;
import com.klab.svc.CommChannel;

/**
 * @author 최의신 (choies@kr.ibm.com)
 *
 */
public class CoffeeAction extends BaseAction
{
	@Override
	protected Object execute(String actionId, Map<String, Object> params, ConversationSession session, CommChannel channel)
	{
		Object exeResult = null;
		
		try
		{
			if ( "SELECT_ALL".equals(actionId) )
			{
				StringBuffer sql = new StringBuffer();
				sql.append("SELECT	PROD_K_NM ");
				sql.append(",PROD_E_NM ");
				sql.append("FROM	COFFEE_PROD");
				
				final Map<String, String> result = new HashMap<String, String>();
				
				AdapterUtil.executeQuery(sql.toString(), new IResultSet() {
					@Override
					public void fetch(ResultSet rs)
					{
						List<String> allString = new ArrayList<String>();
						
						try{
							while(rs.next())
							{
								allString.add(rs.getString("PROD_K_NM"));
							}
						}catch(Exception ex) {
							ex.printStackTrace();
						}

						result.put("ALL_COFFEE", String.join(",", allString));
					}
				});

				exeResult = result;
			}
			else if ( "SELECT_PRICE".equals(actionId) )
			{
				StringBuffer sql = new StringBuffer();
				sql.append("SELECT	PROD_PRICE ");
				sql.append("FROM	COFFEE_PROD ");
				sql.append("WHERE	PROD_CD = ?");
				
				final Map<String, String> result = new HashMap<String, String>();
				
				Object [] prm = new Object[1];
				prm[0] = params.get("prodCd");
				
				AdapterUtil.executeQuery(sql.toString(), prm, new IResultSet()
				{
					@Override
					public void fetch(ResultSet rs)
					{
						try{
							if ( rs.next() )
							{
								result.put("PROD_PRICE", rs.getString("PROD_PRICE"));
							}
						}catch(Exception ex) {
							ex.printStackTrace();
						}
					}
				});

				exeResult = result;
			}
			else if ( "SELECT_PICTURE".equals(actionId) )
			{
				StringBuffer sql = new StringBuffer();
				sql.append("SELECT	PROD_IMAGE ");
				sql.append("FROM	COFFEE_PIC ");
				sql.append("WHERE	PROD_CD = ?");
				
				final Map<String, String> result = new HashMap<String, String>();
				
				Object [] prm = new Object[1];
				prm[0] = params.get("prodCd");
				
				AdapterUtil.executeQuery(sql.toString(), prm, new IResultSet()
				{
					@Override
					public void fetch(ResultSet rs)
					{
						try{
							if ( rs.next() )
							{
								result.put("PROD_IMAGE", rs.getString("PROD_IMAGE"));
							}
						}catch(Exception ex) {
							ex.printStackTrace();
						}
					}
				});

				exeResult = result;				
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}

		return exeResult;
	}
}
