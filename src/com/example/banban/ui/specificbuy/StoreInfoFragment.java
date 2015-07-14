package com.example.banban.ui.specificbuy;

/*
 * @author: BruceZhang
 * @description: 商家页面 第二个Tab选项 该商家的信息
 */

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.halfish.banban.R;
import com.example.banban.network.HttpUtil;
import com.example.banban.other.BBApplication;
import com.example.banban.other.BBConfigue;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class StoreInfoFragment extends Fragment {

	protected static final String LOG_TAG = "StoreInfoFragment";
	private TextView m_addrTextView;
	private TextView m_phoneTextView;
	private TextView m_storeInfoTV;
	private Handler m_handler;
	private RequestQueue m_queue;
	private int m_storeId;
	private Activity m_activity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_activity = getActivity();
		m_storeId = m_activity.getIntent().getIntExtra("store_id", -1);
		m_queue = BBApplication.getQueue();
		initHandler();

	}

	private void initHandler() {
		m_handler = new Handler(getActivity().getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case HttpUtil.SUCCESS_CODE:
					JSONObject response = (JSONObject) msg.obj;
					try {
						updataDataFromServer(response);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					Log.v(LOG_TAG, response.toString());
					break;

				default:
					break;
				}
				super.handleMessage(msg);
			}
		};
	}

	private void updataDataFromServer(JSONObject response) throws JSONException {
		int ret_code = response.getInt("ret_code");
		if (ret_code == 1) {
			Toast.makeText(m_activity, "Store not exist", Toast.LENGTH_SHORT)
					.show();
			return;
		}

		// else ret_code == 0
		String address = response.getString("address");
		String phone = response.getString("phone");
		String description = response.getString("description");

		if (address != null) {
			m_addrTextView.setText(address);
		}

		if (description != null) {
			m_storeInfoTV.setText(description);
		}
		m_phoneTextView.setText(phone);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.bb_fragment_specificbuy_storeinfo, container, false);
		m_storeInfoTV = (TextView) rootView
				.findViewById(R.id.tv_store_detail_zhishan);
		m_storeInfoTV.setMovementMethod(ScrollingMovementMethod.getInstance());

		m_addrTextView = (TextView) rootView
				.findViewById(R.id.tv_address_detail);
		m_phoneTextView = (TextView) rootView.findViewById(R.id.tv_phone_num);

		beginDataRequest();

		return rootView;
	}

	private void beginDataRequest() {
		
		HttpUtil.JsonGetRequest(BBConfigue.SERVER_HTTP + "/stores/detail/"
				+ m_storeId, m_handler, m_queue);
	}

}
