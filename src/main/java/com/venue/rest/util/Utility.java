package com.venue.rest.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class Utility 
{
	
	public Utility()
	{
		//default constructor
	}
	
	public static void main(String args[])
	{
		//String s= "<![CDATA[&#160; <div style=\"display:none\"> &#8230;</div> <br /> brightcove.createExperiences(); 2nd year veteran, Brooke will be your 2015 Swimsuit Calendar Girl! The Calendar, which will feature the actual photos, can]]>";
		//String data = Utility.stripCDATA(s);
		//System.out.println("data"+data);
		long datevalue= 1406213757541l;
		convertMillSecondsToDate(datevalue);
	}

	public static String stripCDATA(String data) {
	    
		data = data.trim();
		if(data.startsWith("<![CDATA["))
		{	
		    if (data.startsWith("<![CDATA[")) 
		    {
		      data = data.substring(9);
		      int i = data.indexOf("]]>");
		      if (i == -1) {
		        throw new IllegalStateException(
		            "argument starts with <![CDATA[ but cannot find pairing ]]>");
		      }
		      data = data.substring(0, i);
		    }
		    return data;
		}else 
		{
			return data;
		}	
	  }

	/**
	 * return string formatted utc date
	 *
	 * @param datetime utc datetime in string format of 'M/dd/yyyy HH:mm:ss'
	 * @return utc datetime in string format 'yyyy-MM-dd HH:mm:ss'
     */
	public static String getFormattedTimeStamp(String datetime) {

		String requestFormat = "M/dd/yyyy HH:mm:ss";
		String mySqlFormat = "yyyy-MM-dd HH:mm:ss";
		String formattedTimeStamp = "";

		TimeZone istTime = TimeZone.getTimeZone("UTC");
		SimpleDateFormat requestDate = new SimpleDateFormat(requestFormat);
		requestDate.setTimeZone(istTime);
		SimpleDateFormat mysqlDate = new SimpleDateFormat(mySqlFormat);
		mysqlDate.setTimeZone(istTime);
		try {
			formattedTimeStamp = mysqlDate.format(requestDate.parse(datetime));
		} catch (Exception e) {}
		return formattedTimeStamp;
	}
	
	  public static String convertMillSecondsToDate(Long dateValue)
	  {
	       
           String finalDate="";           
           try
           {
           
	       //DateFormat df = new SimpleDateFormat("dd:MM:yyyy:HH:mm:ss");
		    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	       //Converting milliseconds to Date using Calendar
		    if(dateValue!=null && dateValue >0)
		    {
		    	Calendar cal = Calendar.getInstance();
		    	cal.setTimeInMillis(dateValue);
		    	System.out.println("Milliseconds to Date using Calendar:"+ df.format(cal.getTime()));	      
		    	finalDate = df.format(cal.getTime());
		    }
		    
           }catch(Exception e)
           {
        	   e.printStackTrace();
           }
           return finalDate;
	       
	   }

}
