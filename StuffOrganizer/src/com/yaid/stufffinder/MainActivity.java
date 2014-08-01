package com.yaid.stufffinder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.yaid.helpers.ConstContainer;
import com.yaid.helpers.DateConversion;
import com.yaid.helpers.ImageProcessing;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.app.DatePickerDialog.OnDateSetListener;
import android.widget.SearchView;

//import com.yaid.stufffinder.DatePickerFragment.DatePickerDialogListener;

public class MainActivity extends Activity implements OnDateSetListener, OnQueryTextListener{
    
	public static String DB_FILEPATH = "/data/data/com.yaid.stufffinder/databases/stuffOrganazerDB";
	
	private SearchView mSearchView;
	
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mScreenTitles;
    
    private String mSearchViewText;

    ViewPager pager;
	PagerAdapter pagerAdapter;
	ImageView stuffPhoto;
	ImageButton stuffFromPhoto;
	Button saveButton;
	Button dateTimeButton;
	Button but2;
	
	EditText etName;
	EditText etLocation;
	EditText etExtras;
	
	ListView lvData;
	
	private static final int CM_DELETE_ID = 1;
	final int TYPE_PHOTO = 1;
	final int REQUEST_CODE_PHOTO = 1;
	final int REQUEST_CODESELECT_PICTURE = 2;
	public final static String PATH_FOR_PHOTO = "/Stuff Finder";
	final String TAG = "myLogs";
	private File fotoName;
	private String fileFotoName = "";
	
	//StuffDBHelper dbHelper;
	//SimpleCursorAdapter scAdapter;
	SFCursorAdapter scAdapter;
	static Cursor cursor = null;
	
	//private static boolean dataDownloaded = false;
	//private static Cursor oldCursor;
	
	DBStuffOrganazer db;
	
	int DIALOG_DATE = 1;
	int myYear = 2011;
	int myMonth = 02;
	int myDay = 03;
	
	int dateInSeconds;
	
	String packName = "com.yaid.stufffinder";
	    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
        File folder = new File(Environment.getExternalStorageDirectory() + PATH_FOR_PHOTO);
        if (!folder.exists()) 
        	folder.mkdir(); 
                                
        pager = (ViewPager) findViewById(R.id.pager);

		LayoutInflater inflater = LayoutInflater.from(this);
	    List<View> pages = new ArrayList<View>();

	    pages.add(inflater.inflate(R.layout.add_stuff_screen, null));
	    pages.add(inflater.inflate(R.layout.list_stuff_screen, null));
	    
	    StuffFindPagerAdapter pagerAdapter = new StuffFindPagerAdapter(pages);
	    
	    pager.setAdapter(pagerAdapter);
	    
	    saveButton = (Button)pages.get(0).findViewById(R.id.butSave);
	    saveButton.setOnClickListener(saveButClick);
	    
	    dateTimeButton = (Button)pages.get(0).findViewById(R.id.dateTimeBut);
	    dateTimeButton.setOnClickListener(dateTimeButClick);
	    
	    but2 = (Button)pages.get(0).findViewById(R.id.button2);
	    but2.setOnClickListener(but2Click);
	    
	    stuffPhoto = (ImageView)pages.get(0).findViewById(R.id.imageViewPhoto);
        stuffPhoto.setClickable(true);
        stuffPhoto.setOnClickListener(imagePhotoClick);
        
        stuffFromPhoto = (ImageButton)pages.get(0).findViewById(R.id.imageViewGal);
        stuffFromPhoto.setClickable(true);
        stuffFromPhoto.setOnClickListener(stuffFromPhotoClick);
        
        
        etName = (EditText)pages.get(0).findViewById(R.id.etName);
        etLocation = (EditText)pages.get(0).findViewById(R.id.etLocation);
        etExtras = (EditText)pages.get(0).findViewById(R.id.etExtras);
        
        lvData = (ListView)pages.get(1).findViewById(R.id.listView1);
                
	    pager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				mDrawerList.setItemChecked(pager.getCurrentItem(), true);
                setTitle(mScreenTitles[pager.getCurrentItem()]);
			}
		});

        mTitle = mDrawerTitle = getTitle();
        mScreenTitles = getResources().getStringArray(R.array.screens_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mScreenTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
       

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                           
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }
        
        //db = new DBStuffOrganazer(this);
        db = DBStuffOrganazer.getInstance(this);
        db.open();
        
        if (cursor == null)
        	cursor = db.getAllData();
                
        //String[] from = new String[]{DBStuffOrganazer.NAME_COL, DBStuffOrganazer.LOCATION_COL, DBStuffOrganazer.FILE_NAME_COL};
        String[] from = new String[]{DBStuffOrganazer.NAME_COL, DBStuffOrganazer.LOCATION_COL};
        //int[] to = new int[] {R.id.tvTextName, R.id.tvTextLocation, R.id.ivThumb};
        int[] to = new int[] {R.id.tvTextName, R.id.tvTextLocation};
        
        //scAdapter = new SimpleCursorAdapter(this, R.layout.item, cursor, from, to);
        scAdapter = new SFCursorAdapter(this, cursor, true);
        
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screen_width = size.x;
        
        scAdapter.setThumbSize(screen_width/5);
        
        lvData.setAdapter(scAdapter);
        lvData.setOnItemClickListener(lvDataItemClick);
        
     // добавляем контекстное меню к списку
	    registerForContextMenu(lvData);
        
	    Calendar c = Calendar.getInstance();

        myYear = c.get(Calendar.YEAR);
        myMonth = c.get(Calendar.MONTH);
        myDay = c.get(Calendar.DAY_OF_MONTH);
	    
        //stuffPhoto.setImageBitmap(ImageProcessing.bitmapResize(mBitmap, ivHeigth, ivWidth, true));
        /*
        if (mSearchViewText != null && mSearchViewText.length()>0)
        	//mSearchView.setQuery(mSearchViewText, false);
        	Log.d("myLogs", mSearchViewText);
        */
    }
    
    OnItemClickListener lvDataItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parrent, View view, int position,
				long id) {
						
			//Cursor mC = db.getRowById((int)id);
			//mC.moveToFirst();
			
		   //String imagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + PATH_FOR_PHOTO + "/" +  mC.getString(mC.getColumnIndexOrThrow(DBStuffOrganazer.FILE_NAME_COL));
		   //Log.d(TAG, imagePath);
			//Log.d(TAG, Integer.toString(curID));
		   //Log.d(TAG,Long.toString(id) + " - " + cursor.getCount() + " - " + position);
			
			//Intent intent = new Intent(MainActivity.this, ItemShowActivity.class);
			//Log.d(TAG, "selected = " + position);
			long[] itemsID = new long[lvData.getLastVisiblePosition()+1];
			//Log.d(TAG, "Visible = " + lvData.getLastVisiblePosition());
			for (int i=0; i<lvData.getLastVisiblePosition()+1; i++){
				itemsID[i] = lvData.getItemIdAtPosition(i);
				Log.d(TAG, "itemsID = " + itemsID[i]);
			}
			
			Intent intent = new Intent(MainActivity.this, ItemShowWithFragmentsActivity.class);
			intent.putExtra(DBStuffOrganazer.COLUMN_ID, id);
			intent.putExtra("items_IDS", itemsID);
			startActivity(intent);
			
		}
	};
    
    android.view.View.OnClickListener dateTimeButClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			//Intent intent = new Intent(this, DateTimeActivity.class);
			//Intent intent = new Intent(MainActivity.this, DateTimeActivity.class);
			//startActivity(intent);
			
			//showDialog(DIALOG_DATE);
			
	        Bundle args = new Bundle();
	        args.putInt("year", myYear);
	        args.putInt("month", myMonth);
	        args.putInt("day", myDay);
			/*
			DatePickerFragment newFragment = new DatePickerFragment();
			newFragment.setArguments(args);
		    newFragment.show(getFragmentManager(), "datePicker");*/
	        
	        DatePickerFragment picker = new DatePickerFragment();
	        picker.setArguments(args);
	        //picker.show((FragmentManager)((Activity)MainActivity.this).getFragmentManager(), "frag_date_picker");
	        FragmentManager fm = ((Activity)MainActivity.this).getFragmentManager();
	        picker.show(fm, "frag_date_picker");
			
		}
	};
    		
   OnClickListener saveButClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			
			//SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMMM d, yyyy HH:mm");
			//String dateString = formatter.format(new Date(seconds * 1000L));
						
			db.addRec(etName.getText().toString(), etLocation.getText().toString(), etExtras.getText().toString(), fileFotoName, DateConversion.getSecondsFromDate(myYear, myMonth, myDay));
			//cursor.requery();
			scAdapter.swapCursor(db.getAllData());
									
		}
	};
	
	OnClickListener but2Click = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
		/*				
			// Close the SQLiteOpenHelper so it will commit the created empty
		    // database to internal storage.
			db.close();
			String packName = getApplicationContext().getPackageName();
	//Import backUp		
			try {
                File sd = Environment.getExternalStorageDirectory();
                File data  = Environment.getDataDirectory();

                if (sd.canWrite()) {
                    String  currentDBPath= "//data//" + packName
                            + "//databases//" + "stuffOrganazerDB";
                    String backupDBPath  = PATH_FOR_PHOTO + "/DatabaseName";
                    File  backupDB= new File(data, currentDBPath);
                    File currentDB  = new File(sd, backupDBPath);

                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    Toast.makeText(getBaseContext(), backupDB.toString(),
                            Toast.LENGTH_LONG).show();

                }
            } catch (Exception e) {

                Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG)
                        .show();

            }
			*/
		  //Export BackUp 
			try {
                File sd = Environment.getExternalStorageDirectory();
                File data = Environment.getDataDirectory();

                if (sd.canWrite()) {
                    String  currentDBPath= "//data//" + packName
                            + "//databases//" + "stuffOrganazerDB";
                    String backupDBPath  = PATH_FOR_PHOTO + "/DatabaseName";
                    File currentDB = new File(data, currentDBPath);
                    File backupDB = new File(sd, backupDBPath);
                    
                    if (!backupDB.exists())
                    	backupDB.createNewFile();

                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    Toast.makeText(getBaseContext(), backupDB.toString(),
                            Toast.LENGTH_LONG).show();

                }
            } catch (Exception e) {

                Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG)
                        .show();
            }
            
							
		/*	
			//cursor.move(2);
			cursor.moveToFirst();
	  	      //int colIndex = cursor.getColumnIndex(DBStuffOrganazer.FILE_NAME_COL);
			
			String fileName = cursor.getString(4);
			Log.d(TAG, Integer.toString(cursor.getPosition()));
			Log.d(TAG, fileName);
	  	      
			while (cursor.moveToNext()){
				fileName = cursor.getString(4);
				Log.d(TAG, Integer.toString(cursor.getPosition()));
				Log.d(TAG, fileName);
			}
			
			for (int i=0; i<3; i++)
			{
				cursor.moveToPosition(i);
				fileName = cursor.getString(4);
				Log.d(TAG, Integer.toString(cursor.getPosition()));
				Log.d(TAG, fileName);
			}
			
			*/
			mSearchView.setIconified(false);
			//mSearchView.setIconifiedByDefault(false);
		    //mSearchView.requestFocusFromTouch();
			mSearchView.setQuery("bkabla", false);
			//mSearchView.setIconified(false);
		    //mSearchView.requestFocusFromTouch();
			Log.d("myLogs","but 2 pressed");
		}
	};
    
      
    OnClickListener imagePhotoClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			fileFotoName =  "photo_" + System.currentTimeMillis() + ".jpg";
	        fotoName = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + PATH_FOR_PHOTO + "/" + fileFotoName);
	    	
	    	//String sdCardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
	        
	        // stt
			
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fotoName));
		    startActivityForResult(intent, REQUEST_CODE_PHOTO);			
		}
	};
	
	OnClickListener stuffFromPhotoClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			fileFotoName =  "photo_" + System.currentTimeMillis() + ".jpg";
	        fotoName = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + PATH_FOR_PHOTO + "/" + fileFotoName);
	    	
	        Intent intent = new Intent();
	        intent.setType("image/*");
	        intent.setAction(Intent.ACTION_GET_CONTENT);

	        startActivityForResult(Intent.createChooser(intent,getString(R.string.select_picture_label)), REQUEST_CODESELECT_PICTURE);
		}
	};
	
	@Override
	  protected void onActivityResult(int requestCode, int resultCode,
	      Intent intent) {
	    if (requestCode == REQUEST_CODE_PHOTO) {
	      if (resultCode == RESULT_OK) {
	    	  //File out = new File(getFilesDir(), "newImage.jpg");
	    	  
	    	     if(!fotoName.exists()) {

	    	    Toast.makeText(getBaseContext(),

	    	      "Error while capturing image", Toast.LENGTH_LONG)

	    	      .show();

	    	    return;

	    	   }
	    	     
	    	      ImageProcessing.resizeImage(fotoName, 640, 480, true);
	    	      Bitmap mBitmap = BitmapFactory.decodeFile(fotoName.getAbsolutePath());
	    	      int ivHeigth = stuffPhoto.getHeight();
	    	      int ivWidth = stuffPhoto.getWidth();
	    	      stuffPhoto.setImageBitmap(ImageProcessing.bitmapResize(mBitmap, ivHeigth, ivWidth, true));
	    	      //stuffPhoto.setImageBitmap(mBitmap);
	    	      
	    	      setData();
	    	      

	      } else if (resultCode == RESULT_CANCELED) {
	        Log.d(TAG, "Canceled");
	      }
	    } else if (requestCode == REQUEST_CODESELECT_PICTURE)
	    {
	    	
	    	if(resultCode == Activity.RESULT_OK){
	    		try {
	    			Bitmap bitmap = null;
	        /*        // We need to recyle unused bitmaps
	                if (bitmap != null) {
	                    bitmap.recycle();
	                }
	                */
	                InputStream stream = getContentResolver().openInputStream(
	                        intent.getData());
	                bitmap = BitmapFactory.decodeStream(stream);
	                stream.close();
	                
	                int ivHeigth = stuffPhoto.getHeight();
		    	    int ivWidth = stuffPhoto.getWidth();
		    	    
		    	    fileFotoName =  "photo_" + System.currentTimeMillis() + ".jpg";
		    	    fotoName = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + PATH_FOR_PHOTO + "/" + fileFotoName);
		    	    ImageProcessing.resizeBitmapAndSave(fotoName, bitmap, 768, 1024, true);
		    	    stuffPhoto.setImageBitmap(ImageProcessing.bitmapResize(bitmap, ivHeigth, ivWidth, true));
		    	    
	                //stuffPhoto.setImageBitmap(bitmap);
	                
	                setData();
	                
	            } catch (FileNotFoundException e) {
	                e.printStackTrace();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }            
	    	}
	    }
	}

	private void setData() {
		final Calendar c = Calendar.getInstance();
		  int myYear = c.get(Calendar.YEAR);
		  int myMonth = c.get(Calendar.MONTH);
		  int myDay = c.get(Calendar.DAY_OF_MONTH);
		  
		  String dateString = DateConversion.formateDate(myYear, myMonth, myDay);
		  
		  dateTimeButton.setText(dateString);
	}
	
	public String getPath(Uri uri) 
    {
		String[] filePathColumn = { MediaStore.Images.Media.DATA };
		 
        Cursor cursor = getContentResolver().query(uri,
                filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return picturePath;
    }
	
	public String getRealPathFromURI(Uri contentUri)
    {
        try
        {
            String[] proj = {MediaStore.Images.Media.DATA};
            //Cursor cursor = managedQuery(contentUri, proj, null, null, null);
            Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        catch (Exception e)
        {
            return contentUri.getPath();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        
        mSearchView = (SearchView) menu.findItem(R.id.action_websearch).getActionView();
        //if (mSearchViewText != null && mSearchViewText.length()>0)
        //	mSearchView.setQuery(mSearchViewText, false);
        //mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        
        return super.onCreateOptionsMenu(menu);
    }
    
    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        //menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
        case R.id.action_websearch:
        	/*
            // create intent to perform web search for this planet
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
            // catch event that there's no activity to handle intent
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
            }
            return true;
            */
        //case R.id.action_settings:
        	//registerForContextMenu();
        	//openContextMenu(getActionBar().getCustomView());
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
    	if(position<2)
    		pager.setCurrentItem(position);
    	
        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mScreenTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Bitmap bmp = ((BitmapDrawable)stuffPhoto.getDrawable()).getBitmap();
        outState.putParcelable(ConstContainer.PHOTO_IMG, ((BitmapDrawable)stuffPhoto.getDrawable()).getBitmap());
        outState.putSerializable(ConstContainer.PHOTO_FILE_NAME, fotoName);
        outState.putString(ConstContainer.EDITTEXT_NAME, etName.getText().toString());
        outState.putString(ConstContainer.EDITTEXT_LOCATION, etLocation.getText().toString());
        outState.putString(ConstContainer.EDITTEXT_EXTRAS, etExtras.getText().toString());
        outState.putString(ConstContainer.SEARCHVIEW_TEXT, mSearchView.getQuery().toString());
        Log.d("myLogs", mSearchView.getQuery().toString());
        //Log.d(LOG_TAG, "onSaveInstanceState");
      }
    
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //cnt = savedInstanceState.getInt("count");
        stuffPhoto.setImageBitmap((Bitmap)savedInstanceState.getParcelable(ConstContainer.PHOTO_IMG));
        fotoName = (File)savedInstanceState.getSerializable(ConstContainer.PHOTO_FILE_NAME);
        etName.setText((String)savedInstanceState.getString(ConstContainer.EDITTEXT_NAME));
        etLocation.setText((String)savedInstanceState.getString(ConstContainer.EDITTEXT_LOCATION));
        etExtras.setText((String)savedInstanceState.getString(ConstContainer.EDITTEXT_EXTRAS));
        //mSearchView.setQuery((String)savedInstanceState.getString(ConstContainer.SEARCHVIEW_TEXT), false);
        mSearchViewText = (String)savedInstanceState.getString(ConstContainer.SEARCHVIEW_TEXT);
        //Log.d(LOG_TAG, "onRestoreInstanceState");
      }
    
    public void onCreateContextMenu(ContextMenu menu, View v,
  	      ContextMenuInfo menuInfo) {
  	    super.onCreateContextMenu(menu, v, menuInfo);
  	    menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
  	  }
  	  
  	  public boolean onContextItemSelected(MenuItem item) {
  	    if (item.getItemId() == CM_DELETE_ID) {
  	      // получаем из пункта контекстного меню данные по пункту списка 
  	      AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item.getMenuInfo();
  	      // извлекаем id записи и удаляем соответствующую запись в БД
  	      
  	      Cursor mC = db.getRowById((int)acmi.id);
  	      mC.moveToFirst();
		  
  	      String imagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + PATH_FOR_PHOTO + "/" +  mC.getString(mC.getColumnIndexOrThrow(DBStuffOrganazer.FILE_NAME_COL));
  	      ImageProcessing.deleteImageFile(imagePath);
  	      db.delRec(acmi.id);
  	      
  	      Log.d(TAG, imagePath);
  	      //Log.d(TAG, Long.toString(acmi.id));
  	      // обновляем курсор
  	      //cursor.requery();
  	      //mSearchView.setQuery("", false);
  	      //mSearchView.clearFocus();
  	      if (mSearchView.getQuery().toString().length() == 0) scAdapter.swapCursor(db.getAllData());
  	      else scAdapter.swapCursor(db.searchName(mSearchView.getQuery().toString()));
  	      return true;
  	    }
  	    return super.onContextItemSelected(item);
  	  }
  	  
  	  protected void onDestroy() {
  	    super.onDestroy();
  	    // закрываем подключение при выходе
  	    db.close();
  	  }
/*
	@Override
	public void onDatePicked(DialogFragment dialog, Calendar c,
			boolean isFromDate) {
		if (!isFromDate) return;
		//dateTimeButton.setText(year + " - " + month + " - " + day);
		dateTimeButton.setText(c.get(Calendar.YEAR) + " - " + (c.get(Calendar.MONTH) + 1) + " - " + c.get(Calendar.DAY_OF_MONTH));
		myYear = c.get(Calendar.YEAR);
		myMonth = c.get(Calendar.MONTH) + 1;
		myDay = c.get(Calendar.DAY_OF_MONTH);
		
	}
*/
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		
		//dateTimeButton.setText(year + " - " + (monthOfYear + 1) + " - " + dayOfMonth);
		dateTimeButton.setText(DateConversion.formateDate(year, monthOfYear, dayOfMonth));
		myYear = year;
		myMonth = monthOfYear + 1;
		myDay = dayOfMonth;
		
	}

@Override
public boolean onQueryTextChange(String arg0) {
	//etName.setText(arg0);
	if (arg0.length() == 0)
	{
		cursor = db.getAllData();
		//scAdapter.swapCursor(db.getAllData());
		scAdapter.swapCursor(cursor);
	}
	return false;
}

@Override
public boolean onQueryTextSubmit(String arg0) {
	//etName.setText(arg0+" SUBMIT");
	//cursor = db.searchName("ggg");
	//cursor.requery();
	//scAdapter.notifyDataSetChanged();
	cursor = db.searchName(arg0);
	//scAdapter.swapCursor(db.searchName(arg0));
	scAdapter.swapCursor(cursor);
	
	pager.setCurrentItem(1);
	//mSearchView.clearFocus();
	
	Handler handler = new Handler();
	handler.postDelayed(new Runnable() {

	    public void run() {
	    	mSearchView.clearFocus();
	    }

	},150); // 5000ms delay
	
	return true;
}

}