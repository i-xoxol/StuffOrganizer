package com.yaid.stufffinder;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBStuffOrganazer {
  
  private static final String DB_NAME = "stuffOrganazerDB";
  private static final int DB_VERSION = 1;
  private static final String DB_TABLE = "stufftable";
  
  public static final String COLUMN_ID = "_id";
//  public static final String COLUMN_IMG = "img";
//  public static final String COLUMN_TXT = "txt";
  
  //public static final String tableName = "stufftable";
	//public static final String idCol = "_id";
	public static final String NAME_COL = "namecol";
	public static final String LOCATION_COL = "locationcol";
	public static final String EXTRAS_COL = "extrascol";
	public static final String FILE_NAME_COL = "filenamecol";
  
  private static final String DB_CREATE = 
		  "create table "+DB_TABLE+" ("+COLUMN_ID+" integer primary key autoincrement, " +
				  NAME_COL + " text, " + LOCATION_COL + " text, " + EXTRAS_COL + " text, "+
				  FILE_NAME_COL + " text" + ");";
  
  private final Context mCtx;
  
  
  private DBHelper mDBHelper;
  private SQLiteDatabase mDB;
  
  public DBStuffOrganazer(Context ctx) {
    mCtx = ctx;
  }
  
  // открыть подключение
  public void open() {
    mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
    mDB = mDBHelper.getWritableDatabase();
  }
  
  // закрыть подключение
  public void close() {
    if (mDBHelper!=null) mDBHelper.close();
  }
  
  // получить все данные из таблицы DB_TABLE
  public Cursor getAllData() {
    return mDB.query(DB_TABLE, null, null, null, null, null, null);
  }
  
  // добавить запись в DB_TABLE
  public void addRec(String name, String location,String extras, String fileName) {
    ContentValues cv = new ContentValues();
    cv.put(NAME_COL, name);
    cv.put(LOCATION_COL, location);
    cv.put(EXTRAS_COL, extras);
    cv.put(FILE_NAME_COL, fileName);
    long ttt = mDB.insert(DB_TABLE, null, cv);
  }
  
  // удалить запись из DB_TABLE
  public void delRec(long id) {
    mDB.delete(DB_TABLE, COLUMN_ID + " = " + id, null);
  }
  
  // класс по созданию и управлению БД
  private class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context, String name, CursorFactory factory,
        int version) {
      super(context, name, factory, version);
    }

    // создаем и заполняем БД
    @Override
    public void onCreate(SQLiteDatabase db) {
      db.execSQL(DB_CREATE);
      /*
      ContentValues cv = new ContentValues();
      for (int i = 1; i < 5; i++) {
        cv.put(COLUMN_TXT, "sometext " + i);
        cv.put(COLUMN_IMG, R.drawable.ic_launcher);
        db.insert(DB_TABLE, null, cv);
      }
      */
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
  }
}