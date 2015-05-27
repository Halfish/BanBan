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
import android.widget.SimpleAdapter;
import android.widget.Toast;
/*
 * 交易记录
 */
public class Store_records extends Fragment {
	private ListView listView;
	private String  uri="http://omegaga.net/banban/stores/purchases/history";
	private GettingHandler handler=new GettingHandler();
	MyAdapter adapter;
	ArrayList<HashMap<String, Object>> lstViewItem;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		 View parentView = inflater.inflate(R.layout.store_records, container, false);
		 lstViewItem = new ArrayList<HashMap<String, Object>>();
		 ListView listview = (ListView) parentView.findViewById(R.id.listview);
		 
		 adapter = new MyAdapter(getActivity(),
				 lstViewItem,
				 R.layout.record_item,
				 new String[] { "product_name", "price",
			"buyer_name","date_time" },
				 new int[] { R.id.textView1,R.id.textView3,R.id.textView4,
			R.id.textView5}
		);
		listview.setAdapter(adapter); 
		return parentView;
		
	}
	public void setUserVisibleHint(boolean isVisibleToUser){
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			Log.v("haha","store_history");
			if(lstViewItem!=null)
				lstViewItem.clear();
			HttpUtil.JsonGetRequest(uri, handler, Merchant_main.BBQueue);
			
	    } 
	}
	private class GettingHandler extends Handler{
		
		 public void dispatchMessage(Message msg) {
			 switch (msg.what) {
			 	case 0x123:
			 		JSONObject jsonobj=(JSONObject) msg.obj;
				try {
					JSONArray  jsonArray=jsonobj.getJSONArray("history");
					int code=jsonobj.getInt("ret_code");
					for(int i=0;i<jsonArray.length();i++){
						HashMap<String, Object> map = new HashMap<String, Object>(); 
						JSONObject temp=jsonArray.getJSONObject(i);
						String product_name =temp.getString("product_name");
						String price=temp.getString("price");
						String buyer_name =temp.getString("purchaser_name");
						String purchase_time=temp.getString("purchase_time");
						String imageString=temp.getString("purchaser_image");
						String pic_uri="http://omegaga.net/banban"+imageString; 
						map.put("product_name",product_name);
						map.put("price", price);
						map.put("buyer_name", buyer_name);
						map.put("date_time", purchase_time);
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
			NetworkImageView networkImageView = (NetworkImageView) v.findViewById(R.id.imageView1);
			networkImageView.setDefaultImageResId(R.drawable.touxiang); 
			networkImageView.setErrorImageResId(R.drawable.touxiang);
			ImageLoader imageLoader = new ImageLoader(Merchant_main.BBQueue, localStore.storeCache);  
			networkImageView.setImageUrl(lstViewItem.get(position).get("image").toString(),imageLoader);
			return v;
			
		}
	}
}
