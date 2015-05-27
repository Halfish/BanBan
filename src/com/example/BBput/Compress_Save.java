package com.example.BBput;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

public class Compress_Save {
	
		/*
		 * 对图片进行压缩
		 */
	public static int calculateInSampleSize(BitmapFactory.Options options,  
	        int reqWidth, int reqHeight) {  
	    final int height = options.outHeight;  
	    final int width = options.outWidth;  
	    int inSampleSize = 1;  
	    if (height > reqHeight || width > reqWidth) {  
	        final int heightRatio = Math.round((float) height  
	                / (float) reqHeight);  
	        final int widthRatio = Math.round((float) width / (float) reqWidth);  
	        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;  
	    }  
	    Log.v("a",""+inSampleSize);
	    return inSampleSize;  
	}  
	
	@SuppressWarnings("deprecation")
	public static Bitmap getSmallBitmap(String filePath, int reqWidth,  
	        int reqHeight) {  
		Log.v("haha",reqHeight+"  "+reqWidth);
	    final BitmapFactory.Options options = new BitmapFactory.Options();  
	    options.inJustDecodeBounds = true;  
	    BitmapFactory.decodeFile(filePath, options);  
	    options.inSampleSize = calculateInSampleSize(options,420,  
	            420);  
	    options.inJustDecodeBounds = false;  
	    options.inPreferredConfig = Bitmap.Config.RGB_565;
	    options.inPurgeable = true;
	    options.inInputShareable = true;
	    Bitmap bm= BitmapFactory.decodeFile(filePath, options);  
	    int degree = readPictureDegree(filePath);  
        bm = rotateBitmap(bm,degree) ;  
        return bm ;  
  
    }  
	private static Bitmap rotateBitmap(Bitmap bitmap, int rotate){  
        if(bitmap == null)  
            return null ;  
          
        int w = bitmap.getWidth();  
        int h = bitmap.getHeight();  
  
        // Setting post rotate to 90  
        Matrix mtx = new Matrix();  
        mtx.postRotate(rotate);  
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);  
    }
	private static int readPictureDegree(String path) {    
        int degree  = 0;    
        try {    
                ExifInterface exifInterface = new ExifInterface(path);    
                int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);    
                switch (orientation) {    
                case ExifInterface.ORIENTATION_ROTATE_90:    
                        degree = 90;    
                        break;    
                case ExifInterface.ORIENTATION_ROTATE_180:    
                        degree = 180;    
                        break;    
                case ExifInterface.ORIENTATION_ROTATE_270:    
                        degree = 270;    
                        break;    
                }    
        } catch (IOException e) {    
                e.printStackTrace();    
        }    
        return degree;    
    }   
}
