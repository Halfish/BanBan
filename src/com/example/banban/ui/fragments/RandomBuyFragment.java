package com.example.banban.ui.fragments;

/*
 * @author: BruceZhang
 * @description: 随机抢 fragment，抢到商品后，跳转到ProductInfoActivity，
 * 查看商品的信息
 */

import java.util.Calendar;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.banban.R;
import com.example.banban.network.HttpUtil;
import com.example.banban.other.BBConfigue;
import com.example.banban.ui.ProductInfoActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class RandomBuyFragment extends BaseActionBarFragment {
	private static final String LOG_TAG = RandomBuyFragment.class.getName();

	private Button m_randomBuyBtn;
	private TextView m_chanceTextView;
	private TextView m_infoTextView;
	private int remainTime = 100;
	private Activity m_activity;
	private RequestQueue m_queue;
	private Handler m_handler;

	private SharedPreferences m_pref;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		m_activity = getActivity();
		Log.v(LOG_TAG, "onCreate called");
		initHandler();
	}
	
	@Override
	public void onStart() {
		Log.v(LOG_TAG, "onStart called");
		super.onStart();
	}

	@Override
	public void onResume() {
		Log.v(LOG_TAG, "onResume called");
		super.onResume();
	}

	private void initHandler() {
		m_handler = new Handler(m_activity.getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case HttpUtil.SUCCESS_CODE:
					JSONObject response = (JSONObject) msg.obj;
					try {
						updateDataFromServer(response);
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

	private void updateDataFromServer(JSONObject jsonObject)
			throws JSONException {
		int product_id = jsonObject.getInt("product_id");
		if (product_id == -1) {
			// 没抢到 TODO 看还有几次机会
			Toast.makeText(m_activity, "没抢到，再试一次吧！", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(m_activity, "不错呦，抢到了一个！", Toast.LENGTH_LONG).show();
			Intent intent = new Intent(m_activity, ProductInfoActivity.class);
			startActivity(intent);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.bb_fragment_random_buy,
				container, false);
		setActionBarCenterTitle(R.string.bb_tab_random_buy);
		initWidgets(view);
		return view;
	}

	private void initWidgets(View view) {
		m_randomBuyBtn = (Button) view.findViewById(R.id.btn_random_buy);
		m_chanceTextView = (TextView) view.findViewById(R.id.tv_chance);
		m_infoTextView = (TextView) view.findViewById(R.id.tv_info);

		remainTime = getRemainingTime();
		m_chanceTextView.setText("今天还有 " + remainTime + " 次机会");

		m_randomBuyBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				remainTime = m_pref.getInt("remainTime", 100);
				if (remainTime > 0) {
					beginDataRequest();
					remainTime--;
					m_pref.edit().putInt("remainTime", remainTime).commit();
					m_chanceTextView.setText("今天还有 " + remainTime + " 次机会");
				} else {
					m_chanceTextView.setVisibility(View.INVISIBLE);
					m_infoTextView.setVisibility(View.VISIBLE);
					m_randomBuyBtn.setVisibility(View.INVISIBLE);
				}
			}
		});
	}

	private int getRemainingTime() {
		m_pref = m_activity.getSharedPreferences("remainTime",
				Context.MODE_PRIVATE);
		String date = m_pref.getString("date", "");

		Calendar calendar = Calendar.getInstance();
		String today = calendar.get(Calendar.YEAR) + ""
				+ (calendar.get(Calendar.MONTH) + 1)
				+ calendar.get(Calendar.DAY_OF_MONTH);

		if (date.equals(today)) {
			return m_pref.getInt("remainTime", 100);
		} else {
			m_pref.edit().putString("date", today).commit();
			m_pref.edit().putInt("remainTime", 100).commit();
		}

		return 100;
	}

	private void beginDataRequest() {
		m_queue = Volley.newRequestQueue(m_activity);
		HttpUtil.NormalPostRequest(new HashMap<String, String>(),
				BBConfigue.SERVER_HTTP + "/products/generate/random",
				m_handler, m_queue);
	}

}
