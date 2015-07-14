package com.example.BusinessMyStore;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.BBput.ThingPutting;
import com.example.BanBanBusiness.Merchant_main;
import com.example.BanBanBusiness.localStore;
import com.example.BusinessHttp.HttpUtil;
import com.halfish.banban.R;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.location.GpsStatus.Listener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
/*
 * 店铺信息界面
 */
import android.widget.TextView;
	
public class Store_info  extends Fragment {
	TextView addressView;
	TextView phoneView;
	TextView descriptionView;
	View view1,view2,view3;
	private String  uri="http://omegaga.net/banban/stores/detail/";
	private String  uri2="http://omegaga.net/banban/stores/update";
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
		view1=(View) parentView.findViewById(R.id.r1);
		view2=(View) parentView.findViewById(R.id.r2);
		view3=(View) parentView.findViewById(R.id.r4);
		view1.setOnClickListener(listener);
		view2.setOnClickListener(listener);
		view3.setOnClickListener(listener);
		update();		
		return parentView;
		
	}
	private OnClickListener listener= new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Builder dialog = new AlertDialog.Builder(getActivity());
			LayoutInflater factory = LayoutInflater.from(getActivity());
			final View textEntryView = factory.inflate(
					R.layout.dialog_item, null);
			final EditText addressEditText = (EditText) textEntryView
					.findViewById(R.id.editText1);
			dialog.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							dialog.dismiss();
						}
					});
			switch (v.getId()) {
			case R.id.r1:
				dialog.setTitle("请输入地址：");
				dialog.setView(textEntryView);
				dialog.setPositiveButton("确定",new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						addressView.setText(addressEditText.getText()
								.toString());
						if(addressView!=null){
							HashMap<String, String> map = new HashMap<String, String>(); 
							map.put("address",addressView.getText().toString());
							HttpUtil.NormalPostRequest(map, uri2, handler, Merchant_main.BBQueue);
						}
					}
					
				});
				dialog.show();
				break;
			case R.id.r2:

				dialog.setTitle("请输入电话");
				dialog.setView(textEntryView);	
				addressEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
				dialog.setPositiveButton("确定",new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
						phoneView.setText(addressEditText.getText()
								.toString());
						if(phoneView!=null){
							HashMap<String, String> map = new HashMap<String, String>(); 
							map.put("phone",phoneView.getText().toString());
							HttpUtil.NormalPostRequest(map, uri2, handler, Merchant_main.BBQueue);
						}
					}
					
				});
				dialog.show();
				break;
			case R.id.r4:
				dialog.setTitle("请输入商家详情");
				dialog.setView(textEntryView);	
				addressEditText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE|InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
				dialog.setPositiveButton("确定",new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
						descriptionView.setText(addressEditText.getText()
								.toString());
						if(descriptionView!=null){
							HashMap<String, String> map = new HashMap<String, String>(); 
							map.put("description",descriptionView.getText().toString());
							HttpUtil.NormalPostRequest(map, uri2, handler, Merchant_main.BBQueue);
						}
					}
					
				});
				dialog.show();
				break;		
			default:
				break;
			}
		}
	
	};
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
