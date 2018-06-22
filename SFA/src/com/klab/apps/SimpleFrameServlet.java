package com.klab.apps;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.klab.ctx.ConversationLogInfo;
import com.klab.ctx.ConversationSession;
import com.klab.svc.AppsPropertiy;
import com.klab.svc.ConsoleLogger;
import com.klab.svc.ConversationLogger;
import com.klab.svc.ILogger;
import com.klab.svc.SimpleAppFrame;
import com.klab.svc.Utils;

/**
 * @author 최의신 (choies@kr.ibm.com)
 *
 */
public class SimpleFrameServlet extends HttpServlet
{
	private static final String ATTR_NAME = "Watson.Conversation";
	
	private SimpleAppFrame appFrame;
	private ConversationLogger convLogger;
	private boolean debug = false;
	
	@Override
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
		
		debug = "true".equals(AppsPropertiy.getInstance().getProperty("wcs.debug"));

		String username = config.getInitParameter("wcs.user");
		String password = config.getInitParameter("wcs.passwd");
		String workId = config.getInitParameter("wcs.workid");

		if ( username == null || password == null || workId == null )
		{
			username = AppsPropertiy.getInstance().getProperty("wcs.user");
			password = AppsPropertiy.getInstance().getProperty("wcs.passwd");
			workId = AppsPropertiy.getInstance().getProperty("wcs.workid");
		}

		appFrame = new SimpleAppFrame();
		appFrame.setUsername(username);
		appFrame.setPassword(password);
		appFrame.setWorkspaceId(workId);

		/*
		 * 대화를 저장할 로거를 생성
		 */
		convLogger = new ConversationLogger();
		
		String logger = AppsPropertiy.getInstance().getProperty("logger.className");
		if ( logger != null && logger.length() > 0 )
		{
			try {
				convLogger.setLogger((ILogger)Utils.loadClass(logger));
			} catch (Exception e) {
				convLogger.setLogger(new ConsoleLogger());
			}
		}
		else {
			convLogger.setLogger(new ConsoleLogger());
		}
		
		convLogger.start();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#destroy()
	 */
	@Override
	public void destroy()
	{
		convLogger.shutdown();
		super.destroy();
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		JsonObject result = new JsonObject();
		
		String text = request.getParameter("say");
		if ( text == null || text.length() == 0 )
		{
			result.addProperty("returnCode", "FAIL");
			result.addProperty("errorString", "EMPTY");
		}
		else {
			ConversationSession session = null;
			
			Object attr = request.getSession().getAttribute(ATTR_NAME);
			
			if ( attr == null ) {
				session = new ConversationSession();
				request.getSession().setAttribute(ATTR_NAME, session);
			}
			else {
				session = (ConversationSession)attr;
			}
			
			try
			{
				ConversationLogInfo log = null;
				if ( "__INIT__".equals(text) ) {
					session.getContext().clear();
					appFrame.message(session, "", null);
				}
				else
					log = appFrame.message(session, text, null);

				/*
				 * 대화 이력을 저장한다.
				 */
				if ( log != null )
					convLogger.addDialog(log);
				
				result.addProperty("returnCode", "OK");
				
				StringBuffer resText = new StringBuffer();
				List<String> list = session.getOutputString();
				for(int i = 0; i < list.size(); i++)
				{
					resText.append(list.get(i));
					if ( i < list.size()-1 )
						resText.append("<br>");
				}
				
				result.addProperty("result", resText.toString());
				
				if ( session.getPostResult() != null )
					result.add("postResult", session.getPostResult());
				
				if ( session.getWarning() != null )
					result.addProperty("warning", session.getWarning());

				if ( debug )
					result.addProperty("debug", session.getDebug());

			}catch(Exception ex) {
				ex.printStackTrace();
				result.addProperty("returnCode", "FAIL");
				result.addProperty("errorString", ex.getMessage());
			}
		}
		
		response.setContentType("text/plain; charset=utf-8");
        response.getWriter().print(result.toString());
	}
}