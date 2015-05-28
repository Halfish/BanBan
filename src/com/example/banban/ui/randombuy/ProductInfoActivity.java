package com.example.banban.ui.randombuy;

/*
 * @author: BruceZhang
 * @description: 随机抢 抢到的某商品，不可再抢，只是静态展示没有功能
 * 特定抢下面也有一个商品的页面，但是可以抢。
 * @see com.example.banban.ui.specificbuy.ProductActivity，
 */

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.example.banban.R;
import com.example.banban.network.HttpUtil;
import com.example.banban.other.BBApplication;
import com.example.banban.other.BBConfigue;
import com.example.banban.ui.BaseActionBarActivity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProductInfoActivity extends BaseActionBarActivity {
	private Handler m_handler;
	private Handler m_purchaseHandler;
	private RequestQueue m_queue;
	private static final String LOG_TAG = ProductInfoActivity.class.getName();

	private TextView m_likeNumberTV;
	private TextView m_nameTextView;
	private TextView m_priceTextView;
	private TextView m_storeNameTextView;
	private TextView m_donateTextView;
	private ImageView m_image;
	private ImageButton m_zanButton;
	private Handler m_zanHandler;

	private int m_productId = -1;
	private int m_likeNum;
	private ActionBar m_actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bb_activity_product_info);
		getActionBar().hide();
		m_queue = BBApplication.getQueue();

		initWidgets();
		initHandler();
		beginDataRequest();
	}

	private void initWidgets() {
		initActionBar();
		m_likeNumberTV = (TextView) findViewById(R.id.tv_like_number);
		m_nameTextView = (TextView) findViewById(R.id.tv_product_name);
		m_priceTextView = (TextView) findViewById(R.id.tv_product_price);
		m_storeNameTextView = (TextView) findViewById(R.id.tv_store_name);
		m_donateTextView = (TextView) findViewById(R.id.tv_donate);
		m_image = (ImageView) findViewById(R.id.img_product);
		m_image.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		m_zanButton = (ImageButton) findViewById(R.id.img_like);
		m_zanButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (m_productId != -1) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("product_id", m_productId + "");
					HttpUtil.NormalPostRequest(map, BBConfigue.SERVER_HTTP
							+ "/products/favorites/add", m_zanHandler, m_queue);
				}
			}
		});
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

	private void beginDataRequest() {
		HttpUtil.JsonGetRequest(BBConfigue.SERVER_HTTP
				+ "/products/show/random", m_handler, m_queue);

		Map<String, String> map = new HashMap<String, String>();
		map.put("product_id", m_productId + "");
		HttpUtil.NormalPostRequest(map, BBConfigue.SERVER_HTTP
				+ "/products/purchases/random", m_purchaseHandler, m_queue);
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
		
		m_purchaseHandler = new Handler(getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case HttpUtil.SUCCESS_CODE:
					JSONObject response = (JSONObject) msg.obj;
					try {
						int retCode = response.getInt("ret_code");
						switch (retCode) {
						case 0:
							// success
							break;
						case 1:
						case 2:
						case 3:
						case 4:
						case 5:
						case 6:
							String message = response.getString("message");
							Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT)
									.show();
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

		m_zanHandler = new Handler(getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case HttpUtil.SUCCESS_CODE:
					JSONObject response = (JSONObject) msg.obj;
					try {
						updataZanFromServer(response);
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
		int retCode = response.getInt("ret_code");
		switch (retCode) {
		case 0:
			parseDataFromJson(response);
			break;

		case 1:
		case 2:
			String message = response.getString("message");
			Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT)
					.show();
			break;

		default:
			break;
		}
	}

	private void updataZanFromServer(JSONObject response) throws JSONException {
		int retCode = response.getInt("ret_code");
		String infoString = "Wrong Code";
		switch (retCode) {
		case 0:
			infoString = "Succeed";
			m_likeNumberTV.setText((m_likeNum + 1) + "");
			break;

		case 1:
			infoString = "Invalid query";
			break;

		case 2:
			infoString = "Product not exist";
			break;

		case 3:
			infoString = "Database exception";
			break;

		case 4:
			infoString = "您已经点过赞了！";
			break;
		}
		Toast.makeText(getApplicationContext(), infoString, Toast.LENGTH_SHORT)
				.show();
	}

	private void parseDataFromJson(JSONObject response) throws JSONException {
		Log.v(LOG_TAG, "parseDataFromJson");

		m_productId = response.getInt("product_id");
		String product_name = response.getString("product_name");
		int original_price = response.getInt("original_price");
		int donate = response.getInt("donate");
		int favorite = response.getInt("favorites");
		m_likeNum = favorite;
		String image = response.getString("image");
		String store_name = response.getString("store_name");

		m_likeNumberTV.setText(favorite + "");
		m_nameTextView.setText("产品名：" + product_name);
		m_priceTextView.setText("原价：" + original_price + "元");
		m_storeNameTextView.setText("商家：" + store_name);
		m_donateTextView.setText("将获得公益资金" + donate + "元");

		updateImage(image);
	}

	private void updateImage(String image) {
		ImageLoader imageLoader = BBApplication.getImageLoader();
		ImageListener listener = ImageLoader.getImageListener(m_image,
				R.drawable.loading_01, R.drawable.loading_01);
		imageLoader.get(BBConfigue.SERVER_HTTP + image, listener);
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
