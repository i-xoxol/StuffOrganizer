package com.yaid.stufffinder;

import java.io.File;

import com.yaid.helpers.ConstContainer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

public class DateTimeActivity extends Activity{
	
	private static final String TIME_HOUR = "TIME_HOUR";
	
	TimePicker timePicker;
	DatePicker datePicker;
	Button butOk;
	private int cHour = 0; 
	
	 @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.date_time_dialog);
	        
	        timePicker = (TimePicker)findViewById(R.id.timePicker1);
	        datePicker = (DatePicker)findViewById(R.id.datePicker1);
	        butOk = (Button)findViewById(R.id.butDateTimeOk);
	        butOk.setOnClickListener(butOkClick);
	        
	        datePicker.setCalendarViewShown(false);
	        	        
	        if (savedInstanceState != null)
	        	cHour = savedInstanceState.getInt(TIME_HOUR, 0);
	        else
	        	cHour = timePicker.getCurrentHour();
	        
	        timePicker.setIs24HourView(true);
	        timePicker.setCurrentHour(cHour);
	 }
	 
	 OnClickListener butOkClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			timePicker.setCurrentHour(14);
			
		}
	};
	 
	 protected void onSaveInstanceState(Bundle outState) {
	        super.onSaveInstanceState(outState);
	        outState.putInt(TIME_HOUR, timePicker.getCurrentHour());
	        
	      }
	    	    	 
}
