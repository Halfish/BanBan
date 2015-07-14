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
import com.halfish.banban.R;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.Toast;
/*
 * 宝贝橱窗界面
 */
public class Store_product extends Fragment {
	private int    images[] = null;
	private String  uri="http://omegaga.net/banban/stores/products/";
	private GettingHandler handler=new GettingHandler();
	ArrayList<HashMap<String, Object>> lstImageItem;
	MyAdapter saImageItems;
	private ImageButton bt1;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		View parentView = inflater.inflate(R.layout.store_product, container, false);
		GridView gridview = (GridView) parentView.findViewById(R.id.gridview);
		lstImageItem = new ArrayList<HashMap<String, Object>>();
		saImageItems=new MyAdapter(getActivity(),
			 lstImageItem,
			 R.layout.myshop_item, 
			 new String[] {"favorites","name","price","numbers" },
			 new int[] { R.id.textView1,R.id.itemTextName,R.id.itemTextP,R.id.itemText3});
	 
	 gridview.setAdapter(saImageItems); 
		return parentView;
		
	}
	public void setUserVisibleHint(boolean isVisibleToUser){
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			Log.v("haha","store_product");
			String Furi=uri+localStore.store_id;
			lstImageItem.clear();
			HttpUtil.JsonGetRequest(Furi, handler, Merchant_main.BBQueue);
			
	    } 
	}
	private class GettingHandler extends Handler{
		
		 public void dispatchMessage(Message msg) {
			 switch (msg.what) {
				case 0x123:
		 		JSONObject jsonobj=(JSONObject) msg.obj;
			try {
				JSONArray  jsonArray=jsonobj.getJSONArray("products");
				for(int i=0;i<jsonArray.length();i++){
					HashMap<String, Object> map = new HashMap<String, Object>(); 
					JSONObject temp=jsonArray.getJSONObject(i);
					int fn =temp.getInt("favorites");
					String name =temp.getString("name");
					String imageString=temp.getString("image");
					String pic_uri="http://omegaga.net/banban"+imageString; 
					int price=temp.getInt("price");
					int numbers1=temp.getInt("amount_spec");
					int numbers2=temp.getInt("amount_random");
					map.put("favorites", fn);   
					map.put("name", name);
					map.put("price", "价格"+price);
					map.put("numbers","数量"+(numbers1+numbers2));	
					map.put("image", pic_uri);
				    lstImageItem.add(map);
					
				}
				saImageItems.notifyDataSetChanged();
				
				
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
		@Override
		public View getView(final int position, View convertView, ViewGroup parent){
			View v = super.getView(position, convertView, parent);
			NetworkImageView networkImageView = (NetworkImageView) v.findViewById(R.id.itemImage3);
			networkImageView.setDefaultImageResId(R.drawable.moren); 
			networkImageView.setErrorImageResId(R.drawable.moren); 
			ImageLoader imageLoader = new ImageLoader(Merchant_main.BBQueue, localStore.storeCache);  
			networkImageView.setImageUrl(lstImageItem.get(position).get("image").toString(),imageLoader);
			return v;
			
		}
	}
}
