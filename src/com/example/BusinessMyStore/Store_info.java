package com.example.BusinessMyStore;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.BanBanBusiness.Merchant_main;
import com.example.BanBanBusiness.localStore;
import com.example.BusinessHttp.HttpUtil;
import com.example.banban.R;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
/*
 * 店铺信息界面
 */
import android.widget.TextView;
	
public class Store_info  extends Fragment {
	TextView addressView;
	TextView phoneView;
	TextView descriptionView;
	private String  uri="http://omegaga.net/banban/stores/detail/";
	private GettingHandler handler=new GettingHandler();
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		
		View parentView = inflater.inflate(R.layout.store_info, container, false);
		addressView=(TextView) parentView.findViewById(R.id.real_address);
		phoneView=(TextView) parentView.findViewById(R.id.real_phone);
		descriptionView=(TextView) parentView.findViewById(R.id.real_store_description);
		update();		
		return parentView;
		
	}
	public void update(){
		if(localStore.address!=null){
			addressView.setText(localStore.address);
			phoneView.setText(localStore.phoneString);
			descriptionView.setText(localStore.description);
		}
		else{
			String Furi=uri+localStore.store_id;
			HttpUtil.JsonGetRequest(Furi, handler,  Merchant_main.BBQueue);
		}
	}
	private class GettingHandler extends Handler{
		
		 public void dispatchMessage(Message msg) {
			 switch (msg.what) {
			 case 0x123:
				 JSONObject jsonObj = (JSONObject) msg.obj;
			 	 int code=jsonObj.optInt("ret_code");
			 	switch (code) {
		 		case 0:
		 			try {
		 				String address=jsonObj.getString("address");
						localStore.address=address;
						addressView.setText(address);
						String phoneString=jsonObj.getString("phone");
						localStore.phoneString=phoneString;
						phoneView.setText(phoneString);
						String description=jsonObj.getString("description");
						localStore.description=description;
						descriptionView.setText(description);
		 			}
					catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		 			break;
			 	}
			 	break;
			 }
		 }
	}
}
