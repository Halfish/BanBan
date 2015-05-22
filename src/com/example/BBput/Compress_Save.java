package com.example.BBput;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
	    final BitmapFactory.Options options = new BitmapFactory.Options();  
	    options.inJustDecodeBounds = true;  
	    BitmapFactory.decodeFile(filePath, options);  
	    options.inSampleSize = calculateInSampleSize(options, reqWidth,  
	            reqHeight);  
	    options.inJustDecodeBounds = false;  
	    options.inPreferredConfig = Bitmap.Config.RGB_565;
	    options.inPurgeable = true;
	    options.inInputShareable = true;
	    return BitmapFactory.decodeFile(filePath, options);  
	}  

	
}
