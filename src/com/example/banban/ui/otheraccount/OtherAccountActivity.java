package com.example.banban.ui.otheraccount;

/*
 * @author: BruceZhang
 * @description: 我的账户 fragment
 */

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.example.banban.R;
import com.example.banban.network.BitmapCache;
import com.example.banban.network.HttpUtil;
import com.example.banban.other.BBConfigue;
import com.example.banban.ui.myaccount.ShareProductFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class OtherAccountActivity extends FragmentActivity {

	protected static final String LOG_TAG = OtherAccountActivity.class.getName();
	private ViewPager mPager;
	private ArrayList<Fragment> fragmentList;
	private TextView m_donateTextView;
	private TextView m_nickName;
	private ImageView m_userPic;

	private Handler m_handler;
	private Handler m_handler2;
	private RequestQueue m_queue;
	private Button book_Store;
	private Button book_Project;
	private Button foucus_people;
	private Button fans_people;
	private Button add_focusButton;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setTitle("Ta的账户");
		setContentView(R.layout.bb_fragment_other_account);
		
		m_nickName =(TextView) findViewById(R.id.tv_nickname);
		m_donateTextView = (TextView) findViewById(R.id.tv_total_donate);
		m_userPic = (ImageView)findViewById(R.id.btn_nickname);
		add_focusButton=(Button) findViewById(R.id.button1);
		book_Store=(Button) findViewById(R.id.button_shoucang);
		book_Project=(Button) findViewById(R.id.button_shoucangXM);
		foucus_people=(Button) findViewById(R.id.button_guanzhu);
		fans_people=(Button) findViewById(R.id.button_fans); 
		add_focusButton.setOnClickListener(listener);
		book_Store.setOnClickListener(listener);
		book_Project.setOnClickListener(listener);
		foucus_people.setOnClickListener(listener);
		fans_people.setOnClickListener(listener);
		
		initHandler();
		initView();
		beginDataRequest();
	}
	private void initView() {
		mPager = (ViewPager)findViewById(R.id.viewpager);
		fragmentList = new ArrayList<Fragment>();
		fragmentList.add(new ShareProductFragment());
		mPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(),
				fragmentList));
		mPager.setCurrentItem(0);// 设置当前显示标签页为第一页
	}
	private OnClickListener listener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.button1:
				//加关注的网络操作
				break;
			case R.id.button_shoucang:
				//切换到一个新的activity;
				Intent intent=new Intent(OtherAccountActivity.this,OtherAccount_SCSJ.class);
				startActivity(intent);
				break;
			case R.id.button_shoucangXM:
				Intent intent1=new Intent(OtherAccountActivity.this,OtherAccount_SCXM.class);
				startActivity(intent1);
				break;
			case R.id.button_guanzhu:
				Intent intent2=new Intent(OtherAccountActivity.this,OtherAccount_GZDR.class);
				startActivity(intent2);
				break;
			
			case R.id.button_fans :
				Intent intent3=new Intent(OtherAccountActivity.this,OtherAccount_TDFS.class);
				startActivity(intent3);
				break;
			default:
				break;
			}
		}
	};
	
	private void initHandler() {
		m_handler = new Handler(getMainLooper()) {
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

		m_handler2 = new Handler(getMainLooper()) {
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
		m_queue = Volley.newRequestQueue(this);
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
		String image = jsonObject.getString("image");
		ImageLoader imageLoader = new ImageLoader(m_queue, new BitmapCache());
		ImageListener listener = ImageLoader.getImageListener(m_userPic,
				R.drawable.heartstone_thrall, R.drawable.heartstone_thrall);
		imageLoader.get(BBConfigue.SERVER_HTTP + image, listener);

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

		//int balance = jsonObject.getInt("balance");
		int total_donate = jsonObject.getInt("total_donate");
		m_donateTextView.setText(total_donate + "");
		//m_balanceTextView.setText(balance + "");
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