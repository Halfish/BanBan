package com.example.banban.ui.fragments;

/*
 * @author: BruceZhang
 * @description: 我的账户 fragment
 */

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.banban.R;
import com.example.banban.network.HttpUtil;
import com.example.banban.other.BBConfigue;
import com.example.banban.ui.myaccount.ProjectFragment;
import com.example.banban.ui.myaccount.ShareProductFragment;
import com.example.banban.ui.myaccount.ShoppingCarFragment;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MyAccountFragment extends BaseActionBarFragment {

	protected static final String LOG_TAG = MyAccountFragment.class.getName();
	private ViewPager mPager;
	private ArrayList<Fragment> fragmentList;
	private ImageView image;
	private TextView view1, view2, view3;
	private int currIndex;// 当前页卡编号
	private int bmpW;// 横线图片宽度
	private int offset;// 图片移动的偏移量

	private Activity m_activity;
	private TextView m_donateTextView;
	private TextView m_balanceTextView;
	private TextView m_nickName;

	private Handler m_handler;
	private Handler m_handler2;
	private RequestQueue m_queue;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_activity = getActivity();
		m_actionBar = m_activity.getActionBar();
		initHandler();
	}

	private void initHandler() {
		m_handler = new Handler(m_activity.getMainLooper()) {
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
		
		m_handler2 = new Handler(m_activity.getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case HttpUtil.SUCCESS_CODE:
					JSONObject response = (JSONObject) msg.obj;
					try {
						updateUserDetail(response);
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

	private void beginDataRequest() {
		m_queue = Volley.newRequestQueue(m_activity);
		HttpUtil.JsonGetRequest(BBConfigue.SERVER_HTTP + "/users/balance",
				m_handler, m_queue);
		HttpUtil.JsonGetRequest(BBConfigue.SERVER_HTTP + "/users/"
				+ BBConfigue.USER_ID, m_handler2, m_queue);
		Log.v(LOG_TAG, "user id: " + BBConfigue.USER_ID);
	}

	private void updateUserDetail(JSONObject jsonObject) throws JSONException {
		int retCode = jsonObject.getInt("ret_code");
		if (retCode == 1) {
			Log.v(LOG_TAG, "User does not exist!");
			return;
		}
		// String image = jsonObject.getString("image");
		String username = jsonObject.getString("username");
		m_nickName.setText(username);
	}
	
	private void updataDataFromServer(JSONObject jsonObject)
			throws JSONException {
		int retCode = jsonObject.getInt("ret_code");
		if (retCode == 1) {
			Log.v(LOG_TAG, "Database Exception!");
			return;
		}

		int balance = jsonObject.getInt("balance");
		int total_donate = jsonObject.getInt("total_donate");
		m_donateTextView.setText(total_donate + "");
		m_balanceTextView.setText(balance + "");
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.bb_fragment_my_account,
				container, false);

		m_donateTextView = (TextView) view.findViewById(R.id.tv_total_donate);
		m_balanceTextView = (TextView) view.findViewById(R.id.tv_balance);
		m_nickName = (TextView)view.findViewById(R.id.tv_nickname);

		InitTextView(view);
		InitImage(view);
		InitViewPager(view);

		beginDataRequest();
		return view;
	}

	/*
	 * 初始化标签名0
	 */
	public void InitTextView(View view) {
		view1 = (TextView) view.findViewById(R.id.tv_guid1);
		view2 = (TextView) view.findViewById(R.id.tv_guid2);
		view3 = (TextView) view.findViewById(R.id.tv_guid3);

		view1.setOnClickListener(new txListener(0));
		view2.setOnClickListener(new txListener(1));
		view3.setOnClickListener(new txListener(2));
	}

	public class txListener implements View.OnClickListener {
		private int index = 0;

		public txListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(index);
		}
	}

	/*
	 * 初始化图片的位移像素
	 */
	public void InitImage(View view) {
		image = (ImageView) view.findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(),
				R.drawable.bb_cursor).getWidth();
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;
		offset = (screenW / 3 - bmpW) / 2;

		// ImageView设置平移，使下划线平移到初始位置（平移一个offset）
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		image.setImageMatrix(matrix);
	}

	/*
	 * 初始化ViewPager
	 */
	public void InitViewPager(View view) {
		mPager = (ViewPager) view.findViewById(R.id.viewpager);
		fragmentList = new ArrayList<Fragment>();
		fragmentList.add(new ProjectFragment());
		fragmentList.add(new ShoppingCarFragment());
		fragmentList.add(new ShareProductFragment());

		// 给ViewPager设置适配器
		mPager.setAdapter(new MyFragmentPagerAdapter(getChildFragmentManager(),
				fragmentList));
		mPager.setCurrentItem(0);// 设置当前显示标签页为第一页
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());// 页面变化时的监听器
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {
		private int one = offset * 2 + bmpW;// 两个相邻页面的偏移量

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageSelected(int arg0) {
			Animation animation = new TranslateAnimation(currIndex * one, arg0
					* one, 0, 0);// 平移动画
			currIndex = arg0;
			animation.setFillAfter(true);// 动画终止时停留在最后一帧，不然会回到没有执行前的状态
			animation.setDuration(200);// 动画持续时间0.2秒
			image.startAnimation(animation);// 是用ImageView来显示动画的
		}
	}

	private class MyFragmentPagerAdapter extends FragmentPagerAdapter {
		ArrayList<Fragment> list;

		public MyFragmentPagerAdapter(FragmentManager fm,
				ArrayList<Fragment> list) {
			super(fm);
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Fragment getItem(int arg0) {
			return list.get(arg0);
		}
	}

}
