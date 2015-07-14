package com.example.BusinessBuyManager;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.BanBanBusiness.Merchant_main;
import com.example.BusinessHttp.HttpUtil;
import com.halfish.banban.R;

public class Fragment3 extends Fragment   {
	Button bt;
	private  AlertDialog mDialog;
	private String  uri="http://omegaga.net/banban/products/purchases/verify";
	private EditText editText;
	private PuttingHandler handler=new PuttingHandler();
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View parentView = inflater.inflate(R.layout.mydeal, container, false);
		bt=(Button)parentView.findViewById(R.id.button1);
		editText=(EditText)parentView.findViewById(R.id.editText1);
		bt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 showRoundProcessDialog(getActivity(), R.layout.loading_process);
			//将数据发送过去
				 Map<String,String> map=new HashMap<String, String>();
				 map.put("purchase_code", editText.getText().toString());
				 HttpUtil.NormalPostRequest(map,uri, handler, 
						 Merchant_main.BBQueue);
			}
		});
		return parentView;
	}
	 public class PuttingHandler extends Handler{
		 public void dispatchMessage(Message msg) {
			 switch (msg.what) {
			 	case 0x123:
			 		JSONObject jsonObj = (JSONObject) msg.obj;
			 		int code=jsonObj.optInt("ret_code");
			 		switch (code) {
					case 0:
						Toast.makeText(getActivity(),"确认成功",2000).show();
						mDialog.dismiss();
						editText.setText("");
						break;
					case 5:
						Toast.makeText(getActivity(),"消费已经使用",2000).show();
						mDialog.dismiss();
						editText.setText("");
						break;
					default:
						Toast.makeText(getActivity(),"确认失败",2000).show();
						mDialog.dismiss();
						editText.setText("");
						break;
					}
			 		
			 		break;
			 	case 0x124:
			 		Toast.makeText(getActivity(),"确认失败",2000).show();
			 		mDialog.dismiss();
			 		break;
			 }
		 }
		 
	 }
	  public void showRoundProcessDialog(Context mContext, int layout)
	    {
	        OnKeyListener keyListener = new OnKeyListener()
	        {
	            @Override
	            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
	            {
	                if (keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_SEARCH)
	                {
	                    return true;
	                }
	                return false;
	            }
	        };

	        mDialog = new AlertDialog.Builder(mContext).create();
	        mDialog.setOnKeyListener(keyListener);
	        mDialog.show();
	        // 注意此处要放在show之后 否则会报异常
	        mDialog.setContentView(layout);
	    }
	}