package com.example.banban.ui.fragments;

/*
 * @author: BruceZhang
 * @description: 随机抢 fragment，抢到商品后，跳转到ProductInfoActivity，
 * 查看商品的信息
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kankan.wheel.widget.WheelView;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.banban.R;
import com.example.banban.network.HttpUtil;
import com.example.banban.other.BBConfigue;
import com.example.banban.ui.TigerMathine;
import com.example.banban.ui.randombuy.ProductInfoActivity;

import android.app.Activity;
import android.content.Intent;
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
	private List<WheelView> m_wheelViewList;
	private TigerMathine m_tigerMathine;

	private int remainTime = 0;
	private Activity m_activity;
	private RequestQueue m_queue;
	private Handler m_handler;
	private Handler m_randomTimeHandler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		m_activity = getActivity();
		m_queue = Volley.newRequestQueue(m_activity);
		beginTimeDataRequest();
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

		m_randomTimeHandler = new Handler(m_activity.getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case HttpUtil.SUCCESS_CODE:
					JSONObject response = (JSONObject) msg.obj;
					try {
						updateRandomTimeFromServer(response);
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

	private void updateRandomTimeFromServer(JSONObject response)
			throws JSONException {
		int retCode = response.getInt("ret_code");
		if (retCode == 0) {
			int random_times = response.getInt("random_times");
			remainTime = random_times;
			m_chanceTextView.setText("今天还有 " + remainTime + " 次机会");
		}
	}

	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		if (view == null) {
			view = inflater.inflate(R.layout.bb_fragment_random_buy, container,
					false);
			setActionBarCenterTitle(R.string.bb_tab_random_buy);
			initWidgets(view);
			initTigerMathine(view);
		}

		// 缓存的view需要判断是否已经被加过parent，
		// 如果有parent需要从parent删除，要不然会发生这个view已经有parent的错误。
		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null) {
			parent.removeView(view);
		}

		return view;
	}

	private void initWidgets(View view) {
		m_randomBuyBtn = (Button) view.findViewById(R.id.btn_random_buy);
		m_chanceTextView = (TextView) view.findViewById(R.id.tv_chance);
		m_infoTextView = (TextView) view.findViewById(R.id.tv_info);

		remainTime = 0;
		m_chanceTextView.setText("今天还有 " + remainTime + " 次机会");

		m_randomBuyBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if (remainTime > 0) {
					beginDataRequest();
					m_tigerMathine.startAnim();
					m_chanceTextView.setText("今天还有 " + remainTime + " 次机会");
				} else {
					m_chanceTextView.setVisibility(View.INVISIBLE);
					m_infoTextView.setVisibility(View.VISIBLE);
					m_randomBuyBtn.setVisibility(View.INVISIBLE);
				}
			}
		});
	}

	private void initTigerMathine(View view) {
		m_wheelViewList = new ArrayList<WheelView>();
		m_wheelViewList.add((WheelView) view.findViewById(R.id.slot_1));
		m_wheelViewList.add((WheelView) view.findViewById(R.id.slot_2));
		m_wheelViewList.add((WheelView) view.findViewById(R.id.slot_3));
		m_wheelViewList.add((WheelView) view.findViewById(R.id.slot_4));
		m_tigerMathine = new TigerMathine(m_activity, m_wheelViewList);
	}

	private void beginDataRequest() {
		
		HttpUtil.NormalPostRequest(new HashMap<String, String>(),
				BBConfigue.SERVER_HTTP + "/products/generate/random",
				m_handler, m_queue);
		beginTimeDataRequest();
	}

	private void beginTimeDataRequest() {
		HttpUtil.JsonGetRequest(BBConfigue.SERVER_HTTP
				+ "/users/suppports/history/" + BBConfigue.USER_ID,
				m_randomTimeHandler, m_queue);
	}
	
}
