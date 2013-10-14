package com.yaid.stufffinder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class ImageProcessing {
	
	final public static int RES_OK = 0;
	final public static int FILE_DOESNT_EXIST = 1;
	final public static int IO_ERROR = 2;
	final public static int SIZE_ERROR = 3;
	
	static public int resizeImage(String path, int heigth, int width, boolean proportionLock){
		
		File imageName = new File(path);
		return resizeImage(imageName, heigth, width, proportionLock);		
	}
	
	static public int resizeImage(File imageName, int heigth, int width, boolean proportionLock){
		
		//File imageName = new File(path);
		if(!imageName.exists())
			return FILE_DOESNT_EXIST;
		
		if (heigth == 0 || width == 0)
			return SIZE_ERROR;
		
		Bitmap mBitmap = BitmapFactory.decodeFile(imageName.getAbsolutePath());
		
	    Bitmap resizedBitmap = bitmapResize(mBitmap, heigth, width, proportionLock);
	    
		try {
			FileOutputStream out = null;
			out = new FileOutputStream(imageName.getAbsolutePath());
			if (out != null)
				{
				resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
					return IO_ERROR;
					}
				}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return FILE_DOESNT_EXIST;
		}
		
		return RES_OK;
	}
	
	static public Bitmap bitmapResize(Bitmap bmpIn, int heigth, int width, boolean proportionLock)
	{
		if (bmpIn == null) return null;
		if (heigth == 0 || width == 0) return bmpIn;
		int oldWidth = bmpIn.getWidth();
	    int oldHeight = bmpIn.getHeight();
	    float scaleWidth = ((float) width) / oldWidth;;
	    float scaleHeight = ((float) heigth) / oldHeight;
		if (proportionLock)
		{
			if (scaleWidth>scaleHeight)
				scaleWidth = scaleHeight;
			else
				scaleHeight = scaleWidth;
		}
		
		Matrix matrix = new Matrix();
	    // RESIZE THE BIT MAP
	    matrix.postScale(scaleWidth, scaleHeight);

	    // "RECREATE" THE NEW BITMAP
	    return Bitmap.createBitmap(bmpIn, 0, 0, oldWidth, oldHeight, matrix, false);		
	}
	
	static public Bitmap loadAndResizeBitmap(String path, int heigth, int width, boolean proportionLock)
	{
		File imageName = new File(path);
		if(!imageName.exists())
			return null;
		
		if (heigth == 0 || width == 0)
			return null;
		
		Bitmap mBitmap = BitmapFactory.decodeFile(imageName.getAbsolutePath());
		
	    return bitmapResize(mBitmap, heigth, width, proportionLock);
		
		
	}

}
