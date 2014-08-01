package com.yaid.stufffinder;

import com.yaid.helpers.ImageProcessing;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.database.Cursor;
import android.graphics.Point;
//import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Environment;
import android.support.v13.app.FragmentPagerAdapter;
//import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemShowWithFragmentsActivity extends Activity{
	
	//static final String TAG = "myLogs";
	//static final int PAGE_COUNT = 10;
	
	private int pageCount;
	
	ViewPager pager;
	PagerAdapter pagerAdapter;
	
	//ImageView itemImage;
	String imagePath;
	MenuItem menuEdit, menuDelete, menuApply;
	
	long rowID;
	Cursor mC;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showitem_with_fragment);
        
        long[] itemsIds = getIntent().getLongArrayExtra("items_IDS");
        pageCount = itemsIds.length;
        
        pager = (ViewPager) findViewById(R.id.pager_show_item);
        pagerAdapter = new MyFragmentPagerAdapter(getFragmentManager());
        pager.setAdapter(pagerAdapter);

        pager.setOnPageChangeListener(new OnPageChangeListener() {

          @Override
          public void onPageSelected(int position) {
            //Log.d(TAG, "onPageSelected, position = " + position);
          }

          @Override
          public void onPageScrolled(int position, float positionOffset,
              int positionOffsetPixels) {
          }

          @Override
          public void onPageScrollStateChanged(int state) {
          }
        });
        
        //long rowID = getIntent().getLongExtra(DBStuffOrganazer.COLUMN_ID, 0);
        long rowID = getIntent().getLongExtra(DBStuffOrganazer.COLUMN_ID, 0);
        
		//TextView tv = (TextView)findViewById(R.id.showItemTV);
		//itemImage = (ImageView)findViewById(R.id.showItemIV);
		if (!DBStuffOrganazer.getInstance(this).isOpen())
			DBStuffOrganazer.getInstance(this).open();
		
		//Cursor mC = DBStuffOrganazer.getInstance(this).getRowById((int)rowID);
		mC = DBStuffOrganazer.getInstance(this).getRowsById(itemsIds);
		//mC.moveToFirst();
		//imagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + MainActivity.PATH_FOR_PHOTO + "/" +  mC.getString(mC.getColumnIndexOrThrow(DBStuffOrganazer.FILE_NAME_COL));
		//itemImage.setImageBitmap(ImageProcessing.loadAndResizeBitmap(imagePath, 5000, getScreenWidth(), true));
		//tv.setText(imagePath);
        int pos = 0;
        //long rowid = 0;
        mC.moveToFirst();
        for (int i=0; i<mC.getCount(); i++)
        {
        	if ((int)rowID == mC.getInt(mC.getColumnIndexOrThrow(DBStuffOrganazer.COLUMN_ID)))
        	{pos = i; break;}
        	mC.moveToNext();
        	Log.d("myLogs", "i = " + i);
        }
        pager.setCurrentItem(pos);
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.show_item_ab_menu, menu);
	    menuEdit = menu.findItem(R.id.action_edit);
	    menuApply = menu.findItem(R.id.action_apply);
	    menuDelete = menu.findItem(R.id.action_delete);
	    menuApply.setVisible(false);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_edit:
	            //openSearch();
	        	//MenuItem item = menu.findItem(R.id.addAction);
	        	menuApply.setVisible(true);
	        	menuDelete.setVisible(false);
	        	menuEdit.setVisible(false);
	            return true;
	        case R.id.action_apply:
	        	menuApply.setVisible(false);
	        	menuDelete.setVisible(true);
	        	menuEdit.setVisible(true);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	
	
	private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

	    public MyFragmentPagerAdapter(FragmentManager fm) {
	      super(fm);
	    }

	    @Override
	    public Fragment getItem(int position) {
	      //return ItemShowFragment.newInstance(position);
	    	
	    	//long rrowID = 5;
	    	//Cursor mC = DBStuffOrganazer.getInstance(ItemShowWithFragmentsActivity.this).getRowById((int)rrowID);
			//mC.moveToFirst();
	    	mC.moveToPosition(position);
			imagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + MainActivity.PATH_FOR_PHOTO + "/" +  mC.getString(mC.getColumnIndexOrThrow(DBStuffOrganazer.FILE_NAME_COL));
	    	
	    	return ItemShowFragment.newInstance(imagePath, imagePath);
	    }

	    @Override
	    public int getCount() {
	      //return PAGE_COUNT;
	    	return pageCount;
	    }

	  }


}
