package com.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 최의신 (choies@kr.ibm.com)
 * 
 * 지역별 공기질 데이터를 저장한다.
 *
 */
public class AirQuality
{
	public class GradeEntry
	{
		public String city;
		public String grade;
		
		public String toString()
		{
			return city + "::" + grade;
		}
	}
	
	private Map<String, GradeEntry> inform = new HashMap<String, GradeEntry>();
	private String informOverall;
	
	public String getInformOverall() {
		return informOverall;
	}
	public void setInformOverall(String informOverall) {
		this.informOverall = informOverall;
	}
	
	/**
	 * @param city
	 * @param grade
	 */
	public void addGrade(String city, String grade)
	{
		GradeEntry ge = new GradeEntry();
		ge.city = city;
		ge.grade = grade;
		
		inform.put(city, ge);
	}
	
	
	/**
	 * @param city
	 * @return
	 */
	public String getGrade(String city)
	{
		GradeEntry ge = inform.get(city);
		if ( ge != null )
			return "보통".equals(ge.grade) ? "normal" : "나쁨".equals(ge.grade) ? "bad" : "good";
		else
			return "";
	}
	
	public String toString()
	{
		return informOverall + " " + inform;
	}
}
