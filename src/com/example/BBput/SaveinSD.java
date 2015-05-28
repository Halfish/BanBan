package com.example.BBput;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.example.banban.other.BBConfigue;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

public class SaveinSD {
	private static String path = "/sdcard/banban/photo/" + BBConfigue.USER_NAME
			+ "/";

	public static boolean savephoto(Bitmap bitmap, String name) {
		Log.v("SaveinSD ", "path is " + path);
		// 判断外部存储是否可用
		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED))
			return false;
		else {
			FileOutputStream b = null;
			File file = new File(path);
			file.mkdirs();
			String fileName = path + name;
			try {
				b = new FileOutputStream(fileName);
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, b);// 把数据写入文件
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					// 关闭流
					b.flush();
					b.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return true;
		}
	}
}
