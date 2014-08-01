package com.yaid.stufffinder;

import java.util.HashMap;
import java.util.Queue;

import com.yaid.helpers.DateConversion;
import com.yaid.helpers.ImageProcessing;
import com.yaid.helpers.ItemForPQueue;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.PriorityQueue;

public class SFCursorAdapter extends CursorAdapter{
	
	private LayoutInflater mLayoutInflater;
    private Context mContext;
    String PATH_FOR_PHOTO = "/Stuff Finder";
    private HashMap<String, Bitmap> cache = new HashMap<String, Bitmap>();
    
    private int cacheSize = 70;
    
    private int ivSize = 64;
    private Queue<ItemForPQueue> timeQueue = new PriorityQueue<ItemForPQueue>();
    private long timeReq = 0;
    private String minKey = "";
    private int recordID = 0;

	public SFCursorAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
	}
	
	public void setThumbSize(int newSize){
		ivSize = newSize;
	}
	
	public int getThumbSize(){
		return ivSize;
	}

	@Override
	public void bindView(View v, Context context, Cursor c) {
		String name = c.getString(c.getColumnIndexOrThrow(DBStuffOrganazer.NAME_COL));
		String location = c.getString(c.getColumnIndexOrThrow(DBStuffOrganazer.LOCATION_COL));
		String imagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + PATH_FOR_PHOTO + "/" +  c.getString(c.getColumnIndexOrThrow(DBStuffOrganazer.FILE_NAME_COL));
		String date = DateConversion.getDateFromSeconds(c.getInt(c.getColumnIndexOrThrow(DBStuffOrganazer.DATE_COL)),"yyyy-MM-dd");
		recordID = c.getInt(c.getColumnIndexOrThrow(DBStuffOrganazer.COLUMN_ID));
		timeReq = System.currentTimeMillis();
		
		TextView date_text = (TextView)v.findViewById(R.id.tvTextDate);
		if (date_text != null)
			date_text.setText(date);
		
		TextView name_text = (TextView)v.findViewById(R.id.tvTextName);
		if (name_text != null)
			name_text.setText(name);
		
		TextView location_text = (TextView)v.findViewById(R.id.tvTextLocation);
		if (location_text != null)
			location_text.setText(location);
		Bitmap bm = null;
		
		if (cache.containsKey(imagePath))
		{
			timeQueue.remove(new ItemForPQueue(imagePath, timeReq));
			timeQueue.add(new ItemForPQueue(imagePath, timeReq));
			bm = cache.get(imagePath);
		}
		else
		{
			bm = ImageProcessing.loadAndResizeBitmap(imagePath, ivSize, ivSize, true);
			if(cache.size() == cacheSize)
			{
				minKey = timeQueue.poll().getKey();
				cache.remove(minKey);
			}
			cache.put(imagePath, bm);
			timeQueue.add(new ItemForPQueue(imagePath, timeReq));
		}
		if (!cache.containsKey(imagePath))
		{
			bm = ImageProcessing.loadAndResizeBitmap(imagePath, ivSize, ivSize, true);
			cache.put(imagePath, bm);
		}
		else
		{
			bm = cache.get(imagePath);
		}
		
		ImageView item_image = (ImageView)v.findViewById(R.id.ivThumb);
		//Bitmap bm = ImageProcessing.loadAndResizeBitmap(imagePath, 64, 64, true);
		//Bitmap bm = ImageProcessing.loadAndResizeBitmap(imagePath, ivSize, ivSize, true);
		//int ivHeigth = item_image.getHeight();
	    //int ivWidth = item_image.getWidth();
		item_image.getLayoutParams().height = ivSize;
		item_image.getLayoutParams().width = ivSize;
		//Bitmap bm = ImageProcessing.loadAndResizeBitmap(imagePath, ivHeigth, ivWidth, true);
		
		if (bm != null)
			item_image.setImageBitmap(bm);
		
	}
	
	public int getId(){
		return recordID;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View v = mLayoutInflater.inflate(R.layout.item, parent, false);
		return v;
	}

}
