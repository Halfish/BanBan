package com.example.BBput;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

public class PhotoM {
	
	/*
	 * 返回相册图片的绝对地址
	 */
	public static String  getpicture(Context context,Uri selectedImage ){
		
		 String[] filePathColumn = { MediaStore.Images.Media.DATA };
		 ContentResolver cr = context.getContentResolver();
		 Cursor cursor =cr.query(selectedImage,
                 filePathColumn, null, null, null);
		 cursor.moveToFirst();
		 int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		 String picturePath = cursor.getString(columnIndex);
		 cursor.close();
		 return picturePath;
		
		
	}
	/*
	 * 将图片转换成BASE64串,进行网络传输
	 */
	public static String imgToBase64(Bitmap bitmap){
		
		/*if (imgPath !=null && imgPath.length() > 0) {  
            bitmap = readBitmap(imgPath);  
        } 
        */ 
        if(bitmap == null){  
            //bitmap not found!!  
        }  
        ByteArrayOutputStream out = null;  
        try {  
            out = new ByteArrayOutputStream();  
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);  
  
            out.flush();  
            out.close();  
  
            byte[] imgBytes = out.toByteArray();  
            return Base64.encodeToString(imgBytes, Base64.DEFAULT);  
        } catch (Exception e) {  
            // TODO Auto-generated catch block  
            return null;  
        } finally {  
            try {  
                out.flush();  
                out.close();  
            } catch (IOException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            }  
        }  
    }  
	public static Bitmap base64ToBitmap(String base64Data) {  
	    byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);  
	    return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);  
	} 
	
    private static Bitmap readBitmap(String imgPath) {  
        try {  
            return BitmapFactory.decodeFile(imgPath);  
        } catch (Exception e) {  
            // TODO Auto-generated catch block  
            return null;  
        }  
  
    }  
}
