package com.example.banban.ui;

/*
 * @author: BruceZhang
 * @description: 公益众筹中的某一个特定的公益项目详情，含有三个选项卡
 * 我的账户中的公益项目，点击某一项，也可以跳转到此界面
 */

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.example.banban.R;
import com.example.banban.network.BitmapCache;
import com.example.banban.network.HttpUtil;
import com.example.banban.other.BBConfigue;
import com.example.banban.ui.publicwelfare.DetailFragment;
import com.example.banban.ui.publicwelfare.SupportFragment;
import com.example.banban.ui.publicwelfare.SupporterFragment;
import com.example.sortlistview.AlphabetaContactActicity;

public class ProjectActivity extends FragmentActivity {

	private static final String LOG_TAG = ProjectActivity.class.getName();

	private ViewPager mPager;
	private ArrayList<Fragment> fragmentList;
	private ImageView image;
	private TextView view1, view2, view3;
	private int currIndex;// 当前页卡编号
	private int bmpW;// 横线图片宽度
	private int offset;// 图片移动的偏移量

	public int m_projectId;
	private TextView m_projectName; // 项目名称
	private TextView m_goal; // 目标：元
	private TextView m_achieve; // 已经筹资：元
	private TextView m_accumulate; // 达成：100%
	private ImageView m_image;

	private Handler m_handler;
	private RequestQueue m_queue;
	private ActionBar m_actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bb_activity_publicwelfare_project);

		// 要展示该醒目详细信息，必须给此Activity一个projectID
		m_projectId = getIntent().getIntExtra("projectId", -1); 
		
		initWidgets();
		initHandler();

		InitTextView();
		InitImage();
		InitViewPager();
		beginDataGetRequest();
	}

	private void initWidgets() {
		initActionBar();
		
		m_projectName = (TextView) findViewById(R.id.tv_project_name);
		m_goal = (TextView) findViewById(R.id.tv_goal);
		m_achieve = (TextView) findViewById(R.id.tv_achieve);
		m_accumulate = (TextView) findViewById(R.id.tv_accumulation);
		m_image = (ImageView)findViewById(R.id.img_project);
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

	private void beginDataGetRequest() {
		m_queue = Volley.newRequestQueue(this);
		HttpUtil.JsonGetRequest(BBConfigue.SERVER_HTTP + "/projects/detail/"
				+ m_projectId, m_handler, m_queue);
	}

	private void initHandler() {
		m_handler = new Handler(getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case HttpUtil.SUCCESS_CODE:
					JSONObject response = (JSONObject) msg.obj;
					try {
						parseDataFromServer(response);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					Log.v(LOG_TAG, response.toString());
					break;
				case HttpUtil.FAILURE_CODE:
					Log.v(LOG_TAG, "failed");

				default:
					break;
				}
				super.handleMessage(msg);
			}
		};
	}

	private void parseDataFromServer(JSONObject response) throws JSONException {
		int ret_code = response.getInt("ret_code");
		if (ret_code == 1) {
			Toast.makeText(this, "Project does not exist!", Toast.LENGTH_SHORT)
					.show();
			return;
		}

		// else ret_code == 0
		String name = response.getString("name");
		String imageUrl = response.getString("image");
		int total_support = response.getInt("total_support");
		int expect = response.getInt("expect");
		int percentage = response.getInt("percentage");
		// String description = response.getString("description");
		// String feedback = response.getString("");

		m_projectName.setText("项目名称：" + name);
		m_goal.setText("目标：" + expect + "元");
		m_achieve.setText("已筹资：" + total_support + "元");
		m_accumulate.setText("达成：" + percentage + "%");
		
		ImageLoader imageLoader = new ImageLoader(m_queue, new BitmapCache());
		ImageListener listener = ImageLoader.getImageListener(m_image,
				R.drawable.heartstone_thrall, R.drawable.heartstone_thrall);
		imageLoader.get(BBConfigue.SERVER_HTTP + imageUrl, listener);

	}

	/*
	 * 初始化标签名0
	 */
	public void InitTextView() {
		view1 = (TextView) findViewById(R.id.tv_guid1);
		view2 = (TextView) findViewById(R.id.tv_guid2);
		view3 = (TextView) findViewById(R.id.tv_guid3);

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
	public void InitImage() {
		image = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(),
				R.drawable.bb_cursor).getWidth();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
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
	public void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.viewpager);
		fragmentList = new ArrayList<Fragment>();
		fragmentList.add(new DetailFragment());
		fragmentList.add(new SupportFragment());
		fragmentList.add(new SupporterFragment());

		// 给ViewPager设置适配器
		mPager.setAdapter(new MyFragmentPagerAdapter(
				getSupportFragmentManager(), fragmentList));
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
