package com.utils;

import java.awt.Dimension;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;

/**
 * @author 최의신 (choies@kr.ibm.com)
 *
 * 유용한 기능을 모아둔 클래스이다.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class Utils
{	
	/**
	 * @param src
	 * @param cnt
	 * @return
	 */
	public static List slice(List src, int cnt)
	{
		List list = new ArrayList();
		
		if ( src.size() >= cnt )
		{
			for(int i = 0; i < cnt; i++)
				list.add(src.remove(0));
		}
		else {
			list.addAll(src);
		}
		
		return list;
	}

    /**
     * DB Time Format을 일반 Time Format으로 변경한다. (yyyy/mm/dd hh:mi:ss)
     * @param time
     * @return 포맷된 문자열
     */
     public static String normalTimeFormat(String time)
     {
    	 if ( time == null )
    		 return "";
    	 
         String ret = null;
         if (time.length() >= 14){
             ret = time.substring(0, 4) + "/" + time.substring(4, 6) + "/" +
               time.substring(6, 8) + " " + time.substring(8, 10) + ":" +
               time.substring(10, 12) + ":" + time.substring(12, 14);
         }else if(time.length() == 8){
             ret = time.substring(0, 4) + "/" + time.substring(4, 6) + "/" + time.substring(6, 8);
         }else{
             return time;
         }

         return ret;
     }
     
     /**
 	 * @return
 	 */
 	public static String getDateString(int day)
     {
     	Calendar c = Calendar.getInstance();
     	c.add(Calendar.DAY_OF_MONTH, day);
     	
     	StringBuffer str = new StringBuffer();
     	
     	int v = c.get(Calendar.YEAR);
     	str.append(v);
     	
     	v = c.get(Calendar.MONTH) + 1;
     	if ( v < 10 ) str.append("0");
 		str.append(v);
 		
 		v = c.get(Calendar.DAY_OF_MONTH);
 		if ( v < 10 ) str.append("0");
 		str.append(v);
 		
 		v = c.get(Calendar.HOUR_OF_DAY);
 		if ( v < 10 ) str.append("0");
 		str.append(v);
 		
     	return normalTimeFormat(str.toString());
     }
     

    /**
	 * @return
	 */
	public static String currentTime()
    {
		return currentTime(null, null, true, false);
    }
	
	/**
	 * @return
	 */
	public static String currentTime4()
	{
		return currentTime("/", ":", true, false);
	}
	
	/**
	 * @param ds
	 * @param ts
	 * @return
	 */
	public static String currentTime(String ds, String ts, boolean time, boolean ms)
	{
    	Calendar c = Calendar.getInstance();
    	
    	StringBuffer str = new StringBuffer();
    	
    	int v = c.get(Calendar.YEAR);
    	str.append(v);
    	if (ds != null) str.append(ds);
    	
    	v = c.get(Calendar.MONTH) + 1;
    	if ( v < 10 ) str.append("0");
		str.append(v);
		if (ds != null) str.append(ds);
		
		v = c.get(Calendar.DAY_OF_MONTH);
		if ( v < 10 ) str.append("0");
		str.append(v);
		
		if ( time ) {
			if (ds != null) str.append(" ");

			v = c.get(Calendar.HOUR_OF_DAY);
			if ( v < 10 ) str.append("0");
			str.append(v);
			if (ts != null) str.append(ts);
			
			v = c.get(Calendar.MINUTE);
			if ( v < 10 ) str.append("0");
			str.append(v);
			if (ts != null) str.append(ts);
	    	
			v = c.get(Calendar.SECOND);
			if ( v < 10 ) str.append("0");
			str.append(v);
			
			if ( ms )
				str.append(Calendar.MILLISECOND);
		}

    	return str.toString();		
	}	
	
    /**
     * @param target
     */
    @SuppressWarnings("deprecation")
	public static void showCenter(Window target)
    {
        Dimension size = target.getSize();
        Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screen.width - size.width) / 2;
        int y = (screen.height - size.height) / 2;

        target.setLocation(x, y);
        target.show();
    }
    
    
    /**
     * @param file
     * @return
     */
    public static BufferedImage loadImage(String file)
    {
    	BufferedImage img = null;
    	try
    	{
    		URL u = Utils.class.getResource("/" + file);
    		if ( u != null )
    			img = ImageIO.read(u);
    	}catch(Exception ex) {
    		ex.printStackTrace();
    	}

    	return img;
    }

	/**
	 * @param file
	 * @return
	 */
	public static byte [] loadCertDocument(String file)
	{
		byte [] buffer = null;
		InputStream in = null;
		try {
    		URL u = Utils.class.getResource("/" + file);
    		if ( u != null ) {
    			in = u.openStream();
    			buffer = IOUtils.toByteArray(in);
    		}
		}catch(Exception e) {
			e.printStackTrace();
		} finally {
			if ( in != null )
				IOUtils.closeQuietly(in);
		}		

		return buffer;
	}
	
	/**
	 * @param buffer
	 * @return
	 */
	public static String MD5(byte [] buffer)
	{
		StringBuffer hashcode = new StringBuffer();
		
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(buffer);
			byte[] mdbytes = md.digest();
			 
			for (int i=0; i < mdbytes.length; i++)
			{
				String hex=Integer.toHexString(0xff & mdbytes[i]);
		   	    if(hex.length()==1)
		   	    	hashcode.append('0');
		   	    hashcode.append(hex);
		    }
		}catch(Exception ig) {
		}
		
		return hashcode.toString();
	}
	
    /**
     * 문자열을 int 기본타입으로 변환한다.
     * @param val
     * @return int 값
     */
    public static int toInt(String val) {
        int v = 0;

        try {
            v = Integer.parseInt(val);
        } catch (Exception e) {
        }

        return v;
    }
    
    /**
     * 문자열을 float 기본타입으로 변환한다.
     * @param val
     * @return float 값
     */
    public static float toFloat(String val) {
        float v = 0;

        try {
            v = Float.parseFloat(val);
        } catch (Exception e) {
        }

        return v;
    }  	
}
