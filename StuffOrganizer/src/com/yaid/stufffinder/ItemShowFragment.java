package com.yaid.stufffinder;

import java.util.Random;

import com.yaid.helpers.ImageProcessing;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemShowFragment extends Fragment{

	static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
	static final String IMAGE_PATH = "IMAGE_PATH";
	static final String ITEM_NAME = "ITEM_NAME";
	  
	  int pageNumber;
	  int backColor;
	  String imagePath;
	  String itemName;
	  
	  static ItemShowFragment newInstance(String imagePath, String itemName) {
		ItemShowFragment pageFragment = new ItemShowFragment();
	    Bundle arguments = new Bundle();
	    //arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
	    arguments.putString(IMAGE_PATH, imagePath);
	    arguments.putString(ITEM_NAME, itemName);
	    pageFragment.setArguments(arguments);
	    return pageFragment;
	  }
	  
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    //pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
	    imagePath = getArguments().getString(IMAGE_PATH, "");
	    itemName = getArguments().getString(ITEM_NAME, "");
	    Random rnd = new Random();
	    backColor = Color.argb(40, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
	  }
	  
	  @Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	      Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.showitem, null);
	    
	    TextView tvPage = (TextView) view.findViewById(R.id.showItemTV);
	    //tvPage.setText("Page " + pageNumber);
	    tvPage.setText(itemName);
	    //tvPage.setBackgroundColor(backColor);
	    
	    ImageView itemImage = (ImageView)view.findViewById(R.id.showItemIV);
	    itemImage.setImageBitmap(ImageProcessing.loadAndResizeBitmap(imagePath, 5000, getScreenWidth(), true));
	    return view;
	  }
	  
	  private int getScreenWidth(){
			//Display display = getWindowManager().getDefaultDisplay();
		  	DisplayMetrics displaymetrics = new DisplayMetrics();
		  	getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
	        //Point size = new Point();
	        //display.getSize(size);
		  	return displaymetrics.widthPixels;
	        //return size.x;        
		}
}
