package com.example.banban.ui.specificbuy;

/*
 * @author: BruceZhang
 * @description: 某商家页面 可用于特定抢
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.example.banban.R;
import com.example.banban.network.BitmapCache;
import com.example.banban.network.HttpUtil;
import com.example.banban.other.BBConfigue;

import android.annotation.SuppressLint;
import android.app.ActionBar;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class StoreActivity extends FragmentActivity {

	private static final String LOG_TAG = StoreActivity.class.getName();

	private ViewPager mPager;
	private ArrayList<Fragment> fragmentList;
	private ImageView image;
	private TextView view1, view2, view3;
	private int currIndex;// 当前页卡编号
	private int bmpW;// 横线图片宽度
	private int offset;// 图片移动的偏移量

	private int m_storeId;

	private RequestQueue m_queue;
	private Handler m_handler;
	private Handler m_likeHandler;
	private Handler m_collectHandler;

	private TextView m_totalDonate;
	private TextView m_storeName;

	private ImageButton m_likeButton;
	private ImageButton m_collectButton;
	private ImageView m_storeImageView;
	
	private ActionBar m_actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bb_activity_store);

		initWidgets();

		m_storeId = getIntent().getIntExtra("store_id", -1);
		Log.v(LOG_TAG, "m_storeId is: " + m_storeId);

		initHandler();
		beginDataRequest();

		InitTextView();
		InitImage();
		InitViewPager();
	}

	private void initWidgets() {
		m_totalDonate = (TextView) findViewById(R.id.tv_total_donate);
		m_storeName = (TextView) findViewById(R.id.tv_store_name);
		m_storeImageView = (ImageView)findViewById(R.id.img_store);

		m_likeButton = (ImageButton) findViewById(R.id.img_like);
		m_collectButton = (ImageButton) findViewById(R.id.img_collect);

		m_likeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("store_id", m_storeId + "");
				HttpUtil.NormalPostRequest(map, BBConfigue.SERVER_HTTP
						+ "/stores/favorites/add", m_likeHandler, m_queue);
			}
		});

		m_collectButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("store_id", m_storeId + "");
				HttpUtil.NormalPostRequest(map, BBConfigue.SERVER_HTTP
						+ "/users/bookmarks/stores/add", m_collectHandler, m_queue);
			}
		});
		
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

		m_likeHandler = new Handler(getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case HttpUtil.SUCCESS_CODE:
					JSONObject response = (JSONObject) msg.obj;
					try {
						updataLikeDataFromServer(response);
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

		m_collectHandler = new Handler(getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case HttpUtil.SUCCESS_CODE:
					JSONObject response = (JSONObject) msg.obj;
					try {
						updataCollectDataFromServer(response);
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
		HttpUtil.JsonGetRequest(BBConfigue.SERVER_HTTP + "/stores/detail/"
				+ m_storeId, m_handler, m_queue);
	}

	private void updataDataFromServer(JSONObject response) throws JSONException {
		int retCode = response.getInt("ret_code");
		switch (retCode) {
		case 0:
			parseDataFromJson(response);
			break;

		case 1:
			Toast.makeText(this, "Store not exist", Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}
	}

	private void updataLikeDataFromServer(JSONObject response)
			throws JSONException {
		int retCode = response.getInt("ret_code");
		switch (retCode) {
		case 0:
			Toast.makeText(this, "点赞成功！", Toast.LENGTH_SHORT).show();
			break;

		case 1:
		case 2:
		case 3:
		case 4:
			String message = response.getString("message");
			Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}
	}

	private void updataCollectDataFromServer(JSONObject response)
			throws JSONException {
		int retCode = response.getInt("ret_code");
		switch (retCode) {
		case 0:
			Toast.makeText(this, "已经收藏！", Toast.LENGTH_SHORT).show();
			m_collectButton.setBackgroundResource(R.drawable.heart_red);
			break;

		case 1:
			Toast.makeText(this, "DataBase Exception", Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}
	}

	private void parseDataFromJson(JSONObject response) throws JSONException {
		Log.v(LOG_TAG, "parseDataFromJson");

		int total_donate = response.getInt("total_donate");
		String image = response.getString("image");
		String name = response.getString("name");
		m_totalDonate.setText("累计捐款：" + total_donate + " 元");
		m_storeName.setText(name); //TODO
		updateImage(image);
	}
	
	private void updateImage(String image) {
		ImageLoader imageLoader = new ImageLoader(m_queue, new BitmapCache());
		ImageListener listener = ImageLoader.getImageListener(m_storeImageView,
				R.drawable.loading_01, R.drawable.loading_01);
		imageLoader.get(BBConfigue.SERVER_HTTP + image, listener);
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
		fragmentList.add(new ProductsFragment());
		fragmentList.add(new StoreInfoFragment());
		fragmentList.add(new ReviewsFragment());

		// 给ViewPager设置适配器
		mPager.setAdapter(new MyFragmentPagerAdapter(
				getSupportFragmentManager(), fragmentList));
		mPager.setCurrentItem(0);// 设置当前显示标签页为第一页
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());// 页面变化时的监听器
		mPager.setOffscreenPageLimit(2);
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