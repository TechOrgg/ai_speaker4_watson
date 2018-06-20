package com.model;

import com.utils.Utils;

/**
 * @author 최의신 (choies@kr.ibm.com)
 * 
 * 시간대별 날씨 데이터를 저장한다.
 *
 */
public class WeatherData
{
	private int hour;
	private int day;
	private float temp;
	private String wfKor;
	
	public int getHour() {
		return hour;
	}
	public void setHour(int hour) {
		this.hour = hour;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public float getTemp() {
		return temp;
	}
	public void setTemp(float temp) {
		this.temp = temp;
	}
	public String getWfKor() {
		return wfKor;
	}
	public void setWfKor(String wfKor) {
		this.wfKor = wfKor;
	}
	
	/**
	 * @param id
	 * @param value
	 */
	public void putData(String id, String value)
	{
		if ( "hour".equals(id) ) {
			hour = Utils.toInt(value);
		}
		else if ( "day".equals(id) ) {
			day = Utils.toInt(value);
		}
		else if ( "temp".equals(id) ) {
			temp = Utils.toFloat(value);
		}
		else if ( "wfKor".equals(id) ) {
			wfKor = value;
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return hour + ":" + day + ":" + temp + ":" + wfKor;
	}

}
