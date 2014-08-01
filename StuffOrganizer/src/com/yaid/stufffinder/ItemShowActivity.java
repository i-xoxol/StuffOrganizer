package com.yaid.stufffinder;

import com.yaid.helpers.ImageProcessing;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemShowActivity extends Activity {
	
	ImageView itemImage;
	String imagePath;
	MenuItem menuEdit, menuDelete, menuApply;	
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.showitem);
			
		long rowID = getIntent().getLongExtra(DBStuffOrganazer.COLUMN_ID, 0);
		TextView tv = (TextView)findViewById(R.id.showItemTV);
		itemImage = (ImageView)findViewById(R.id.showItemIV);
		if (!DBStuffOrganazer.getInstance(this).isOpen())
			DBStuffOrganazer.getInstance(this).open();
		
		Cursor mC = DBStuffOrganazer.getInstance(this).getRowById((int)rowID);
		mC.moveToFirst();
		imagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + MainActivity.PATH_FOR_PHOTO + "/" +  mC.getString(mC.getColumnIndexOrThrow(DBStuffOrganazer.FILE_NAME_COL));
		itemImage.setImageBitmap(ImageProcessing.loadAndResizeBitmap(imagePath, 5000, getScreenWidth(), true));
		tv.setText(imagePath);
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
	
	private int getScreenWidth(){
		Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;        
	}
}
