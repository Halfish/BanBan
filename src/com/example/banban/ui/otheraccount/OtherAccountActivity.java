package com.example.banban.ui.otheraccount;

/*
 * @author: BruceZhang
 * @description: 我的账户 fragment
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

import android.annotation.SuppressLint;
import android.app.ActionBar;
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
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class OtherAccountActivity extends FragmentActivity {

	protected static final String LOG_TAG = OtherAccountActivity.class
			.getName();
	private ViewPager mPager;
	private ArrayList<Fragment> fragmentList;
	private TextView m_donateTextView;
	private TextView m_nickName;
	private ImageView m_userPic;

	private Handler m_userDetailHandler;
	private Handler m_followUserHandler;
	private RequestQueue m_queue;
	private Button book_Store;
	private Button book_Project;
	private Button foucus_people;
	private Button fans_people;
	private Button m_followButton;
	private ActionBar m_actionBar;
	private int m_userId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setTitle("Ta的账户");
		setContentView(R.layout.bb_fragment_other_account);

		/*
		 * 进入别人用户的界面，一定要保证传一个此用户的user_id过来！
		 */
		m_userId = getIntent().getIntExtra("user_id", -1);

		m_nickName = (TextView) findViewById(R.id.tv_nickname);
		m_donateTextView = (TextView) findViewById(R.id.tv_total_donate);
		m_userPic = (ImageView) findViewById(R.id.btn_nickname);
		m_followButton = (Button) findViewById(R.id.btn_follow);
		book_Store = (Button) findViewById(R.id.button_shoucang);
		book_Project = (Button) findViewById(R.id.button_shoucangXM);
		foucus_people = (Button) findViewById(R.id.button_guanzhu);
		fans_people = (Button) findViewById(R.id.button_fans);

		m_followButton.setOnClickListener(listener);
		book_Store.setOnClickListener(listener);
		book_Project.setOnClickListener(listener);
		foucus_people.setOnClickListener(listener);
		fans_people.setOnClickListener(listener);

		m_queue = Volley.newRequestQueue(this);
		initHandler();
		initView();
		beginDataRequest();
		initActionBar();
	}

	@SuppressLint("InflateParams") 
	private void initActionBar() {
		m_actionBar = getActionBar();

		TextView m_title = (TextView) getLayoutInflater().inflate(
				R.layout.bb_view_actionbar, null);

		ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
				ActionBar.LayoutParams.WRAP_CONTENT,
				ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
		m_actionBar.setCustomView(m_title, lp);
		m_actionBar.setDisplayShowCustomEnabled(true);
		m_actionBar.setDisplayShowTitleEnabled(false);
		m_actionBar.setHomeButtonEnabled(true);
		m_actionBar.setIcon(R.drawable.bb_back);	
	}
	
	private void initView() {
		mPager = (ViewPager) findViewById(R.id.viewpager);
		fragmentList = new ArrayList<Fragment>();
		fragmentList.add(new ShareProductFragment());
		mPager.setAdapter(new MyFragmentPagerAdapter(
				getSupportFragmentManager(), fragmentList));
		mPager.setCurrentItem(0);// 设置当前显示标签页为第一页
	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_follow:
				// 加关注的网络操作
				beginFollowRequest();
				break;
			case R.id.button_shoucang:
				// 切换到一个新的activity;
				Intent intent = new Intent(OtherAccountActivity.this,
						CollectedStoresActivity.class);
				intent.putExtra("user_id", m_userId);
				startActivity(intent);
				break;
			case R.id.button_shoucangXM:
				Intent intent1 = new Intent(OtherAccountActivity.this,
						CollectedProjectsActivity.class);
				intent1.putExtra("user_id", m_userId);
				startActivity(intent1);
				break;
			case R.id.button_guanzhu:
				Intent intent2 = new Intent(OtherAccountActivity.this,
						FollowingOtherPeopleActivity.class);
				intent2.putExtra("user_id", m_userId);
				startActivity(intent2);
				break;

			case R.id.button_fans:
				Intent intent3 = new Intent(OtherAccountActivity.this,
						MyFansActivity.class);
				intent3.putExtra("user_id", m_userId);
				startActivity(intent3);
				break;
			default:
				break;
			}
		}
	};

	private void initHandler() {

		m_userDetailHandler = new Handler(getMainLooper()) {
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

		m_followUserHandler = new Handler(getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case HttpUtil.SUCCESS_CODE:
					JSONObject response = (JSONObject) msg.obj;
					try {
						int retCode = response.getInt("ret_code");
						switch (retCode) {
						case 0:
							Toast.makeText(getApplicationContext(), "关注成功！",
									Toast.LENGTH_LONG).show();
							m_followButton.setVisibility(View.GONE);
							break;
							
						case 1:
						case 2:
						case 3:
							String message = response.getString("message");
							Toast.makeText(getApplicationContext(), message,
									Toast.LENGTH_LONG).show();
							break;
						
						default:
							break;
						}
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

		HttpUtil.JsonGetRequest(BBConfigue.SERVER_HTTP + "/users/" + m_userId,
				m_userDetailHandler, m_queue);
		Log.v(LOG_TAG, "user id: " + m_userId);
	}

	private void beginFollowRequest() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("follow_user", m_userId + "");
		HttpUtil.NormalPostRequest(map, BBConfigue.SERVER_HTTP
				+ "/users/follow", m_followUserHandler, m_queue);
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
				R.drawable.default_head, R.drawable.default_head);
		imageLoader.get(BBConfigue.SERVER_HTTP + image, listener);

		String username = jsonObject.getString("username");
		m_nickName.setText(username);

		int total_donate = jsonObject.getInt("total_donate");
		m_donateTextView.setText(total_donate + "");
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
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;	

		default:
			break;
		}
		return false;
	}

}
