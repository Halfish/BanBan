package com.example.banban.ui;

/*
 * @author: BruceZhang
 * @description: 随机抢 抢到的某商品，不可再抢，只是静态展示没有功能
 */

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

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProductInfoActivity extends Activity {
	private Handler m_handler;
	private RequestQueue m_queue;
	private static final int SUCCESS_CODE = 0;
	private static final int NOT_CHOSEN_CODE = 1;
	private static final int DATABASE_EXCEPTION_CDDE = 2;
	private static final String LOG_TAG = ProductInfoActivity.class.getName();

	private TextView m_likeNumberTV;
	private TextView m_nameTextView;
	private TextView m_priceTextView;
	private TextView m_storeNameTextView;
	private ImageView m_image;
	private ImageButton m_zanButton;
	private Handler m_handler2;

	private int m_productId = -1;
	private int m_likeNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bb_activity_product_info);
		getActionBar().hide();
		m_queue = Volley.newRequestQueue(this);

		initWidgets();
		initHandler();
		beginDataRequest();
	}

	private void initWidgets() {
		m_likeNumberTV = (TextView) findViewById(R.id.tv_like_number);
		m_nameTextView = (TextView) findViewById(R.id.tv_product_name);
		m_priceTextView = (TextView) findViewById(R.id.tv_product_price);
		m_storeNameTextView = (TextView) findViewById(R.id.tv_store_name);
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
							+ "/products/favorites/add", m_handler2, m_queue);
				}
			}
		});
	}

	private void beginDataRequest() {
		HttpUtil.NormalPostRequest(new HashMap<String, String>(),
				BBConfigue.SERVER_HTTP + "/products/generate/random",
				m_handler, m_queue);
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

		m_handler2 = new Handler(getMainLooper()) {
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
		case SUCCESS_CODE:
			parseDataFromJson(response);
			Log.v(LOG_TAG, response.toString());
			break;

		case NOT_CHOSEN_CODE:
			Toast.makeText(getBaseContext(), "Generate Failed!",
					Toast.LENGTH_SHORT).show();
			break;

		case DATABASE_EXCEPTION_CDDE:
			Toast.makeText(getBaseContext(), "Database Exception!",
					Toast.LENGTH_SHORT).show();
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

		// int product_id = response.getInt("product_id");
		String product_name = response.getString("product_name");
		int original_price = response.getInt("original_price");
		// int donate = response.getInt("donate");
		int favorite = response.getInt("favorites");
		m_likeNum = favorite;
		String image = response.getString("image");
		// int store_id = response.getInt("store_id");
		String store_name = response.getString("store_name");
		m_productId = response.getInt("product_id");

		m_likeNumberTV.setText(favorite + "");
		m_nameTextView.setText(product_name);
		m_priceTextView.setText(original_price + "");
		m_storeNameTextView.setText(store_name);

		updateImage(image);
	}

	private void updateImage(String image) {
		ImageLoader imageLoader = new ImageLoader(m_queue, new BitmapCache());
		ImageListener listener = ImageLoader.getImageListener(m_image,
				R.drawable.heartstone_thrall, R.drawable.heartstone_thrall);
		imageLoader.get(BBConfigue.SERVER_HTTP + image, listener);
	}
}
