package com.example.banban.other;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.banban.network.BitmapCache;

public class BBApplication extends Application {

	private static Context mContext;
	private static RequestQueue mQueue;
	private static ImageLoader mImageLoader;

	@Override
	public void onCreate() {
		mContext = getApplicationContext();
		mQueue = Volley.newRequestQueue(mContext);
		mImageLoader = new ImageLoader(mQueue, new BitmapCache());
		super.onCreate();
	}

	/*
	 * 设置成私有并只开放get函数，可以防止被误改动
	 */
	public static RequestQueue getQueue() {
		return mQueue;
	}

	public static ImageLoader getImageLoader() {
		return mImageLoader;
	}

}
