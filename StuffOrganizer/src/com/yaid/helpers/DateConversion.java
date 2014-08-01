package com.yaid.helpers;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.text.format.DateFormat;

public class DateConversion {
	
	public static String getDate(long milliSeconds, String dateFormat)
	{
	    // Create a DateFormatter object for displaying date in specified format.
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

	    // Create a calendar object that will convert the date and time value in milliseconds to date. 
	     Calendar calendar = Calendar.getInstance();
	     calendar.setTimeInMillis(milliSeconds);
	     return formatter.format(calendar.getTime());
	}
	
	public static String formateDate(int year, int month, int day, String format)
	{
	    // Create a DateFormatter object for displaying date in specified format.
		SimpleDateFormat formatter = new SimpleDateFormat(format);

	    // Create a calendar object that will convert the date and time value in milliseconds to date. 
	     Calendar calendar = Calendar.getInstance();
	     calendar.set(year, month, day);
	     return formatter.format(calendar.getTime());
	}
	
	public static String formateDate(int year, int month, int day)
	{
	    // Create a DateFormatter object for displaying date in specified format.
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

	    // Create a calendar object that will convert the date and time value in milliseconds to date. 
	     Calendar calendar = Calendar.getInstance();
	     calendar.set(year, month, day);
	     return formatter.format(calendar.getTime());
	}
	
	public static String getDateFromSeconds(int seconds, String dateFormat)
	{
	    // Create a DateFormatter object for displaying date in specified format.
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

		long milliSeconds = seconds * 1000L;
		
	    // Create a calendar object that will convert the date and time value in milliseconds to date. 
	     Calendar calendar = Calendar.getInstance();
	     calendar.setTimeInMillis(milliSeconds);
	     return formatter.format(calendar.getTime());
	}
	
	public static int getSecondsFromDate(int year, int month, int day){
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day);
		
		return (int) (calendar.getTimeInMillis() / 1000);
		
	}

}
