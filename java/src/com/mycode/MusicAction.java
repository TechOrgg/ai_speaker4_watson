package com.mycode;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.ibm.watson.developer_cloud.conversation.v1.model.RuntimeEntity;
import com.klab.ctx.ConversationSession;
import com.klab.svc.AppsPropertiy;
import com.klab.svc.CommChannel;
import com.model.MusicData;
import com.svc.Message;
import com.svc.MessageResource;
import com.utils.SqlSessionManager;

/**
 * @author 최의신 (choies@kr.ibm.com)
 * 
 * 음악 재생 요청을 처리한다.
 * 가수 또는 노래 제목을 이용하여 검색하거나 또는 임의의 노래를 검색하여 재생 정보를 전달한다.
 *
 */
@SuppressWarnings("rawtypes")
public class MusicAction extends MyBaseAction
{
	/**
	 * @param artist
	 * @param title
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private int findSong(String artist, String title) throws Exception
	{
		int id = -1;
		
		if ( title != null )
			title = title.replaceAll("\\s","");
		
		if ( artist != null )
			artist = artist.replaceAll("\\s","");
		
		List songList = null;
		Map parm = new HashMap();
		
		if ( artist != null && title != null )
		{
			parm.put("artist", artist);
			parm.put("title", title);
			songList = SqlSessionManager.getSqlMapClient().queryForList("MYHOME.selectSong", parm);
		}
		else {
			parm.put("artist", artist);
			songList = SqlSessionManager.getSqlMapClient().queryForList("MYHOME.selectSong", parm);
			if ( songList.size() == 0 ) {
				parm.clear();
				
				parm.put("title", artist);
				songList = SqlSessionManager.getSqlMapClient().queryForList("MYHOME.selectSong", parm);
			}
		}
		
		if ( songList != null && songList.size() > 0 )
		{
			Random r = new Random();
			Map info = (Map)songList.get(r.nextInt(songList.size()));
			
			id = Integer.parseInt(info.get("id").toString());
		}
		
		return id;
	}

	
	/* (non-Javadoc)
	 * @see com.klab.svc.BaseAction#execute(java.lang.String, java.util.Map)
	 */
	@Override
	protected Object executeAction(String actionId, Map params, ConversationSession session, CommChannel channel)
	{
		Object exeResult = null;
		
		try
		{
			String server = AppsPropertiy.getInstance().getProperty("songs.server");
			String port = AppsPropertiy.getInstance().getProperty("songs.port");
			
            // encrypt Authdata
			String username = AppsPropertiy.getInstance().getProperty("songs.username");
			String password = AppsPropertiy.getInstance().getProperty("songs.password");
            byte[] toEncrypt = (username + ":" + password).getBytes();
            String encoded = Base64.getEncoder().encodeToString(toEncrypt);
            
			int songId = -1;
			String inputStr = session.getInputString();
			RuntimeEntity target = findEntity(session, "target");
			RuntimeEntity action = findEntity(session, "action");
			
			//if ( target != null && action != null && "music".equals(target.getValue()) && "play_music".equals(action.getValue()) )
			if ( action != null && "play".equals(action.getValue()) )
			{
				if ( target != null && "music".equals(target.getValue()) )
				{
					String info = inputStr.substring(0, target.getLocation().get(0).intValue()).trim();
					int ix = info.indexOf(" ");
					if ( ix != -1 ) {
						String artist = info.substring(0, ix);
						String title = info.substring(ix+1);
						songId = findSong(artist, title);
					}
					else {
						String artist = info;
						songId = findSong(artist, null);
					}
				}
				else {
					String info = inputStr.substring(0, action.getLocation().get(0).intValue()).trim();
					int ix = info.indexOf(" ");
					if ( ix != -1 ) {
						String artist = info.substring(0, ix);
						String title = info.substring(ix+1);
						songId = findSong(artist, title);
					}
					else {
						String artist = info;
						songId = findSong(artist, null);
					}
				}
			}
			
			final Map<String, String> result = new HashMap<String, String>();

			if ( songId != -1 ) {
				MusicData md = new MusicData();
				md.setSongUrl("http://" + server + ":" + port + "/rsp/stream/" + songId);
				md.setAuthToken(encoded);
				
				sendMessage(channel, Message.CHANNEL_SOUND, Message.MESSAGE_PLAY_MUSIC, md);
				result.put("MESSAGE", "");
			}
			else {
				result.put("MESSAGE", MessageResource.getString("no.song")); // "원하시는 음악이 없습니다"
			}
			
			exeResult = result;				
		}catch(Exception ex) {
			ex.printStackTrace();
		}

		return exeResult;
	}
	
}
