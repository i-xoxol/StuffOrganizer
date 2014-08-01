package com.yaid.stufffinder;

import java.util.Calendar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.widget.DatePicker;

public class DatePickerFragment extends DialogFragment {
    
    public static final String YEAR = "Year";
    public static final String MONTH = "Month";
    public static final String DATE = "Day";
    
    private OnDateSetListener mListener;
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mListener = (OnDateSetListener) activity;
    }
    
    @Override
    public void onDetach() {
        this.mListener = null;
        super.onDetach();
    }
    
    @TargetApi(11)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        
        final Calendar c = Calendar.getInstance();

        int y = getArguments().getInt("year",c.get(Calendar.YEAR));
        int m = getArguments().getInt("month", c.get(Calendar.MONTH));
        int d = getArguments().getInt("day", c.get(Calendar.DAY_OF_MONTH));

        
        // Jelly Bean introduced a bug in DatePickerDialog (and possibly 
        // TimePickerDialog as well), and one of the possible solutions is 
        // to postpone the creation of both the listener and the BUTTON_* .
        // 
        // Passing a null here won't harm because DatePickerDialog checks for a null
        // whenever it reads the listener that was passed here. >>> This seems to be 
        // true down to 1.5 / API 3, up to 4.1.1 / API 16. <<< No worries. For now.
        //
        // See my own question and answer, and details I included for the issue:
        //
        // http://stackoverflow.com/a/11493752/489607
        // http://code.google.com/p/android/issues/detail?id=34833
        //
        // Of course, suggestions welcome.
        
        final DatePickerDialog picker = new DatePickerDialog(getActivity(), 
                getConstructorListener(), y, m, d);
        
        if (hasJellyBeanAndAbove()) {
            picker.setButton(DialogInterface.BUTTON_POSITIVE, 
                    getActivity().getString(android.R.string.ok),
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DatePicker dp = picker.getDatePicker();
                    mListener.onDateSet(dp, 
                            dp.getYear(), dp.getMonth(), dp.getDayOfMonth());
                }
            });
            picker.setButton(DialogInterface.BUTTON_NEGATIVE,
                    getActivity().getString(android.R.string.cancel),
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            });
        }
        return picker;
    }
    
    private static boolean hasJellyBeanAndAbove() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }
    
    private OnDateSetListener getConstructorListener() {
        return hasJellyBeanAndAbove() ? null : mListener;
    }
    

	
}