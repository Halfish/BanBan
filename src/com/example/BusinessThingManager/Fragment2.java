package com.example.BusinessThingManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.view.View.OnClickListener;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.example.BBput.ThingPutting;
import com.example.BanBanBusiness.Merchant_main;
import com.example.BanBanBusiness.localStore;
import com.example.BusinessHttp.HttpUtil;
import com.example.BusinessHttp.BitmapCache;
import com.halfish.banban.R;
import com.example.newuser.LoginActivity;

public class Fragment2 extends Fragment   {
	  private String  uri="http://omegaga.net/banban/stores/products/";
	  private String  deuri="http://omegaga.net/banban/products/delete";
	  private GettingHandler handler=new GettingHandler();
	  private DeleteHandler de_handler=new DeleteHandler();
	  int k_position;
	  GridView gridview;
	  ArrayList<HashMap<String, Object>> lstImageItem;
	  String[] images_uri;
	  MyAdapter saImageItems;
	  ImageView imageView;
	  RequestQueue deleteQueue;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View parentView = inflater.inflate(R.layout.mything, container, false);
		gridview = (GridView) parentView.findViewById(R.id.gridview);
		lstImageItem = new ArrayList<HashMap<String, Object>>();
		deleteQueue=Volley.newRequestQueue(getActivity());
		 //建立GirdView
		saImageItems=new MyAdapter(getActivity(),
				 lstImageItem,
				 R.layout.m2_item_image, 
				 new String[] { "name","price","numbers" },
				 new int[] { R.id.itemText,R.id.itemText2,R.id.textView1});
		 
		 gridview.setAdapter(saImageItems); 

		return parentView;
	}
	//由于要添加GridView中的button，因此要重新定义SimpleAdpter
	public class MyAdapter extends SimpleAdapter{     
		public MyAdapter(Context context, List<? extends Map<String, ?>> data,
				int resource, String[] from, int[] to) {
			super(context, data, resource, from, to);
			// TODO Auto-generated constructor stub
		}
		@Override
		public View getView(final int position, View convertView, ViewGroup parent){
			 View v = super.getView(position, convertView, parent);
			ImageButton btn1=(ImageButton) v.findViewById(R.id.imageButton1);
			ImageButton btn2=(ImageButton) v.findViewById(R.id.imageButton2);
			NetworkImageView networkImageView = (NetworkImageView) v.findViewById(R.id.itemImageT);
			//调用图片加载
			networkImageView.setDefaultImageResId(R.drawable.moren); 
			networkImageView.setErrorImageResId(R.drawable.moren); 
			ImageLoader imageLoader = new ImageLoader(Merchant_main.BBQueue, localStore.storeCache);  
			
			networkImageView.setImageUrl(lstImageItem.get(position).get("image").toString(),imageLoader);
			
			
			btn1.setTag(position);
			btn2.setTag(position);
			btn1.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Builder dialog=new AlertDialog.Builder(getActivity());
					dialog.setMessage("确认删除宝贝？");
					dialog.setPositiveButton("是", new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							//清除宝贝
							String m_position=lstImageItem.get(position).get("product_id").toString();
							k_position=position;
							HashMap<String, String> map = new HashMap<String, String>(); 
							map.put("id", m_position);
							HttpUtil.NormalPostRequest(map, deuri,de_handler,deleteQueue);
						}
						
					});
					dialog.setNegativeButton("否",  new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
						
					});
					dialog.show();
				}	
			});
			btn2.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					localStore.position=position;
					localStore.Itemlist=lstImageItem;
					Intent intent=new Intent(getActivity(),ThingEdit.class);
					startActivity(intent);
				}
			});
		return v;
	  }
	}
	//网络连接 
	 public void onStart() { 
		 Log.v("dodo","onstart");
		 super.onStart();
		 Log.v("dodo","store_product");
		 lstImageItem.clear();
		 String Furi=uri+localStore.store_id;
		 HttpUtil.JsonGetRequest(Furi, handler,  Merchant_main.BBQueue);
	 }
	 
	 
	private class GettingHandler extends Handler{
		
		 public void dispatchMessage(Message msg) {
			 switch (msg.what) {
			 	case 0x123:
			 		JSONObject jsonobj=(JSONObject) msg.obj;
				try {
					JSONArray  jsonArray=jsonobj.getJSONArray("products");
					int code=jsonobj.getInt("ret_code");
						if(code==0){
						for(int i=0;i<jsonArray.length();i++){
							HashMap<String, Object> map = new HashMap<String, Object>(); 
							JSONObject temp=jsonArray.getJSONObject(i);
							int id =temp.getInt("product_id");
							String name =temp.getString("name");
							String imageString=temp.getString("image");
							String pic_uri="http://omegaga.net/banban"+imageString; 
							double o_price=temp.getDouble("original_price");
							double price=temp.getDouble("price");
							double total=temp.getDouble("donate");
							int numbers1=temp.getInt("amount_spec");	
							int numbers2=temp.getInt("amount_random");
							int numbers3=temp.getInt("category_id");
							String description=temp.getString("description");
							map.put("product_id", id);   
							map.put("name", name);
							map.put("description", description);
							map.put("o_price", o_price);
							map.put("price", "￥"+price);
							map.put("price_h", price);
							map.put("donate", total);
							map.put("numbers","剩 "+(numbers1+numbers2)+" 件");
							map.put("amount_spec", numbers1);
							map.put("amount_random", numbers2);
							map.put("image", pic_uri);
							map.put("category_id",numbers3);
							lstImageItem.add(map);
							saImageItems.notifyDataSetChanged();
							
						}
					}
					else {
						Toast.makeText(getActivity(),"加载失败",2000).show();
						
					}
				
					
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
	private class DeleteHandler extends Handler{
		 public void dispatchMessage(Message msg) {
			 switch (msg.what) {
			 	case 0x123:
			 		JSONObject jsonObj = (JSONObject) msg.obj;
			 		int code=jsonObj.optInt("ret_code");
			 		Log.v("code",""+code);
			 		switch (code){
			 			case 0:
			 				Toast.makeText(getActivity(),"删除成功",2000).show();
			 				lstImageItem.remove(k_position);
			 				saImageItems.notifyDataSetChanged();
			 				break;
			 			default:
			 				Toast.makeText(getActivity(),"删除失败",2000).show();	
			 		}
			 		break;
			 	case 0x124:
			 		Toast.makeText(getActivity(),"服务器无响应",2000).show();
			 }
		 }
	}
	public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
		 inflater.inflate(R.menu.main, menu);
		 super.onCreateOptionsMenu(menu,inflater);
	}
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		Builder dialog;
		switch (id) {
		case R.id.action_puttings:
			Intent intent=new Intent(getActivity(),ThingPutting.class);
			startActivity(intent);
			break;
		}
		return false;
	}
}
