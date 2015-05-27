package com.example.banban.ui.fragments;

/*
 * @author: BruceZhang
 * @description: 随机抢 fragment，抢到商品后，跳转到ProductInfoActivity，
 * 查看商品的信息
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kankan.wheel.widget.WheelView;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.banban.R;
import com.example.banban.network.HttpUtil;
import com.example.banban.other.BBConfigue;
import com.example.banban.ui.TigerMathine;
import com.example.banban.ui.TigerMathine.TigerAnimFinishedCallBack;
import com.example.banban.ui.randombuy.ChooseCategoryActivity;
import com.example.banban.ui.randombuy.ProductInfoActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class RandomBuyFragment extends BaseActionBarFragment {
	private static final String LOG_TAG = RandomBuyFragment.class.getName();

	private Button m_randomBuyBtn;
	private TextView m_chanceTextView;
	private TextView m_infoTextView;
	private List<WheelView> m_wheelViewList;
	private TigerMathine m_tigerMathine;

	private Activity m_activity;
	private RequestQueue m_queue;
	private Handler m_handler;
	private Handler m_randomTimeHandler;
	private int m_randomTimes = 0;
	private boolean m_lucky = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		m_activity = getActivity();
		m_queue = Volley.newRequestQueue(m_activity);
		initHandler();
		beginTimeDataRequest();
		Log.v(LOG_TAG, "onCreate called");
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
					beginTimeDataRequest();
					JSONObject response = (JSONObject) msg.obj;
					try {
						updateDataFromServer(response);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					Log.v(LOG_TAG,
							"random generate products: " + response.toString());
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
			m_lucky = false;
			m_tigerMathine.startAnim(false);
		} else {
			m_lucky = true;
			m_tigerMathine.startAnim(true);
		}
	}

	private void updateRandomTimeFromServer(JSONObject response)
			throws JSONException {
		int retCode = response.getInt("ret_code");
		if (retCode == 0) {
			m_randomTimes = response.getInt("random_times");
			Log.v(LOG_TAG, "random_times is " + m_randomTimes);
			updateRandomTimes();
		}
	}

	private void updateRandomTimes() {
		if (m_randomTimes == 0) {
			m_chanceTextView.setVisibility(View.GONE);
			m_randomBuyBtn.setVisibility(View.GONE);
			m_infoTextView.setVisibility(View.VISIBLE);
		} else {
			m_chanceTextView.setText("今天还有 " + m_randomTimes + " 次机会");
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

		m_randomBuyBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (BBConfigue.IS_VISITOR) {
					ProgressDialog dialog = new ProgressDialog(m_activity);
					dialog.setMessage("游客用户无权限，请登录或注册");
					dialog.show();
					return;
				}

				m_lucky = false;
				beginDataRequest();
				m_randomTimes--;
				updateRandomTimes();
			}
		});
	}

	private void initTigerMathine(View view) {
		m_wheelViewList = new ArrayList<WheelView>();
		m_wheelViewList.add((WheelView) view.findViewById(R.id.slot_1));
		m_wheelViewList.add((WheelView) view.findViewById(R.id.slot_2));
		m_wheelViewList.add((WheelView) view.findViewById(R.id.slot_3));
		m_wheelViewList.add((WheelView) view.findViewById(R.id.slot_4));
		m_tigerMathine = new TigerMathine(m_activity, m_wheelViewList,
				new TigerAnimFinishedCallBack() {
					public void onTigerAnimFinished() {
						Log.v(LOG_TAG, "anim finished");
						if (m_lucky) {
							Intent intent = new Intent(m_activity,
									ProductInfoActivity.class);
							startActivity(intent);
						}
					}
				});
	}

	private void beginDataRequest() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("city", BBConfigue.CURRENT_CITY);
		if (BBConfigue.CATEGORY != 0) {
			map.put("category_id", BBConfigue.CATEGORY + "");
		}
		
		HttpUtil.NormalPostRequest(map, BBConfigue.SERVER_HTTP
				+ "/products/generate/random", m_handler, m_queue);
		Log.v(LOG_TAG, "generate random and CITY is " + BBConfigue.CURRENT_CITY);
		Log.v(LOG_TAG, "generate random and CATEGORY is " + BBConfigue.CATEGORY);
	}

	private void beginTimeDataRequest() {
		HttpUtil.JsonGetRequest(BBConfigue.SERVER_HTTP + "/users/random_times",
				m_randomTimeHandler, m_queue);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.bb_menu_fragment_random_buy, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_category:
			Intent intent = new Intent(m_activity, ChooseCategoryActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
		return false;
	}

}
