package com.yaid.stufffinder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import com.yaid.helpers.ConstContainer;
import com.yaid.helpers.FileUtils;
import com.yaid.helpers.ImageProcessing;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class MainActivity extends Activity{
    
	public static String DB_FILEPATH = "/data/data/com.yaid.stufffinder/databases/stuffOrganazerDB";
	
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mScreenTitles;

    ViewPager pager;
	PagerAdapter pagerAdapter;
	ImageView stuffPhoto;
	Button saveButton;
	Button but2;
	
	EditText etName;
	EditText etLocation;
	EditText etExtras;
	
	ListView lvData;
	
	private static final int CM_DELETE_ID = 1;
	final int TYPE_PHOTO = 1;
	final int REQUEST_CODE_PHOTO = 1;
	final String PATH_FOR_PHOTO = "/Stuff Finder";
	final String TAG = "myLogs";
	private File fotoName;
	private String fileFotoName = "";
	
	//StuffDBHelper dbHelper;
	//SimpleCursorAdapter scAdapter;
	SFCursorAdapter scAdapter;
	Cursor cursor;
	DBStuffOrganazer db;
	    
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
	    
	    but2 = (Button)pages.get(0).findViewById(R.id.button2);
	    but2.setOnClickListener(but2Click);
	    
	    stuffPhoto = (ImageView)pages.get(0).findViewById(R.id.imageViewPhoto);
        stuffPhoto.setClickable(true);
        stuffPhoto.setOnClickListener(imagePhotoClick);
        
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
        
        db = new DBStuffOrganazer(this);
        db.open();
        
        cursor = db.getAllData();
        /*
        scAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
			
			@Override
			public boolean setViewValue(View arg0, Cursor arg1, int arg2) {
				if(arg0.getId() == R.id.ivThumb)
				{
					String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + PATH_FOR_PHOTO + "/" + arg1.getString(arg2);
					//((ImageView)arg0).setImageBitmap(ImageProcessing.loadAndResizeBitmap(fileName, 64, 64, true));
					((ImageView)arg0).setImageURI(Uri.parse(fileName));
					return true;
				}
				return false;
			}
		});
        */
 //       startManagingCursor(cursor);
        
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
        
     // добавляем контекстное меню к списку
	    registerForContextMenu(lvData);
        
        //stuffPhoto.setImageBitmap(ImageProcessing.bitmapResize(mBitmap, ivHeigth, ivWidth, true));
        
    }
    
   OnClickListener saveButClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
						
			db.addRec(etName.getText().toString(), etLocation.getText().toString(), etExtras.getText().toString(), fileFotoName);
			cursor.requery();
									
		}
	};
	
	OnClickListener but2Click = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
						
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
			
	/*	  //Export BackUp 
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
            
		*/							
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

	      } else if (resultCode == RESULT_CANCELED) {
	        Log.d(TAG, "Canceled");
	      }
	    }
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
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
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
  	      db.delRec(acmi.id);
  	      // обновляем курсор
  	      cursor.requery();
  	      return true;
  	    }
  	    return super.onContextItemSelected(item);
  	  }
  	  
  	  protected void onDestroy() {
  	    super.onDestroy();
  	    // закрываем подключение при выходе
  	    db.close();
  	  }
		
    
}