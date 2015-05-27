package com.example.BusinessMyStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.BanBanBusiness.Merchant_main;
import com.example.BanBanBusiness.localStore;
import com.example.BusinessHttp.BitmapCache;
import com.example.BusinessHttp.HttpUtil;
import com.example.banban.R;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;


public class Store_reviews extends Fragment {
	private ListView listView;
	private String  uri="http://omegaga.net/banban/stores/reviews/";
	private GettingHandler handler=new GettingHandler();
	MyAdapter adapter;
	ArrayList<HashMap<String, Object>> lstViewItem;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		View parentView = inflater.inflate(R.layout.store_reviews, container, false);
		lstViewItem = new ArrayList<HashMap<String, Object>>();
		listView = (ListView) parentView.findViewById(R.id.listview);

		adapter = new MyAdapter(getActivity(), lstViewItem,
				R.layout.reviews_item, 
				new String[] {"name",
			"time","content"},
				new int[] {  R.id.textViewN,
			R.id.textViewT,R.id.textViewC});
		listView.setAdapter(adapter);
		return parentView;
	}
	public void setUserVisibleHint(boolean isVisibleToUser){
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			Log.v("haha","store_review");
			String Furi=uri+localStore.store_id;
			if(lstViewItem !=null)
				lstViewItem.clear();
			HttpUtil.JsonGetRequest(Furi, handler, Merchant_main.BBQueue);
			
	    } 
	}
	private class GettingHandler extends Handler{
		
		 public void dispatchMessage(Message msg) {
			 switch (msg.what) {
			 	case 0x123:
				 	JSONObject jsonobj=(JSONObject) msg.obj;
				try {
					JSONArray  jsonArray=jsonobj.getJSONArray("reviews");
					int code=jsonobj.getInt("ret_code");
					for(int i=0;i<jsonArray.length();i++){
						HashMap<String, Object> map = new HashMap<String, Object>(); 
						JSONObject temp=jsonArray.getJSONObject(i);
						String name =temp.getString("username");
						String content =temp.getString("content");
						float rating =temp.getInt("rating");
						String time =temp.getString("time");
						String imageString=temp.getString("user_image");
						String pic_uri="http://omegaga.net/banban"+imageString; 
						map.put("name",name);
						map.put("content", content);
						map.put("rating", rating);
						map.put("time", time);
						map.put("image", pic_uri);
						lstViewItem.add(map);
					}
					adapter.notifyDataSetChanged();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 		break;
			 	case 0x124:
			 		Toast.makeText(getActivity(),"无法连接到服务器",2000).show();
			 		break;
			 }
		 }
	}
	class MyAdapter extends SimpleAdapter{

		public MyAdapter(Context context, List<? extends Map<String, ?>> data,
				int resource, String[] from, int[] to) {
			super(context, data, resource, from, to);
			// TODO Auto-generated constructor stub
		}
		public View getView(final int position, View convertView, ViewGroup parent){
			View v = super.getView(position, convertView, parent);
			RatingBar  ratingBar=(RatingBar) v.findViewById(R.id.ratingBar1);
			ratingBar.setRating((Float) lstViewItem.get(position).get("rating"));
			NetworkImageView networkImageView = (NetworkImageView) v.findViewById(R.id.imageViewW);
			networkImageView.setDefaultImageResId(R.drawable.touxiang); 
			networkImageView.setErrorImageResId(R.drawable.touxiang);
			ImageLoader imageLoader = new ImageLoader(Merchant_main.BBQueue,localStore.storeCache);  
			networkImageView.setImageUrl(lstViewItem.get(position).get("image").toString(),imageLoader);
			
			return v;
		}
	}
}
