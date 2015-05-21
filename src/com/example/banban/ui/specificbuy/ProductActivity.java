package com.example.banban.ui.specificbuy;

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
import com.example.banban.ui.ProductInfoActivity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProductActivity extends Activity {
	private Button m_buyButton;
	private ImageView m_image;
	private TextView m_zan;
	private TextView m_productName;
	private TextView m_originalPrice;
	private TextView m_currentPrice;
	private TextView m_remains;
	private TextView m_fund;
	private ImageButton m_likeButton;

	private Handler m_handler;
	private RequestQueue m_queue;
	private static final String LOG_TAG = ProductInfoActivity.class.getName();
	private Handler m_handler2;
	private Handler m_handler3;
	private int m_productId;
	private int m_likeNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bb_activity_specificbuy_product);
		m_queue = Volley.newRequestQueue(this);

		m_productId = getIntent().getIntExtra("product_id", -1);
		Log.v(LOG_TAG, "productId is: " + m_productId);
		initWidgets();
		initHandler();
		beginDataRequest();
	}

	private void initWidgets() {
		m_buyButton = (Button) findViewById(R.id.btn_buy);
		m_image = (ImageView) findViewById(R.id.img_product);
		m_zan = (TextView) findViewById(R.id.tv_zan);
		m_productName = (TextView) findViewById(R.id.tv_product_name);
		m_originalPrice = (TextView) findViewById(R.id.tv_origin_price);
		m_currentPrice = (TextView) findViewById(R.id.tv_current_price);
		m_remains = (TextView) findViewById(R.id.tv_remains);
		m_fund = (TextView) findViewById(R.id.tv_fund);
		m_likeButton = (ImageButton) findViewById(R.id.img_like);

		m_buyButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (m_productId != -1) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("product_id", m_productId + "");
					HttpUtil.NormalPostRequest(map, BBConfigue.SERVER_HTTP
							+ "/products/purchases/spec", m_handler2, m_queue);
				}
			}
		});

		m_likeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("product_id", m_productId + "");
				HttpUtil.NormalPostRequest(map, BBConfigue.SERVER_HTTP
						+ "/products/favorites/add", m_handler3, m_queue);
			}
		});

	}

	private void beginDataRequest() {
		HttpUtil.JsonGetRequest(BBConfigue.SERVER_HTTP + "/products/detail/"
				+ m_productId, m_handler, m_queue);
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
						updataProductFromServer(response);
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

		m_handler3 = new Handler(getMainLooper()) {
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

	private void updataZanFromServer(JSONObject response) throws JSONException {
		int retCode = response.getInt("ret_code");
		String infoString = "Wrong Code";
		switch (retCode) {
		case 0:
			infoString = "Succeed";
			m_zan.setText((m_likeNum + 1) + "");
			m_likeNum++;
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
		}
		Toast.makeText(getApplicationContext(), infoString, Toast.LENGTH_SHORT)
				.show();
	}

	private void updataDataFromServer(JSONObject response) throws JSONException {
		int retCode = response.getInt("ret_code");
		switch (retCode) {
		case 0:
			parseDataFromJson(response);
			Log.v(LOG_TAG, response.toString());
			break;

		default:
			String messageString = response.getString("message");
			Toast.makeText(getBaseContext(), messageString, Toast.LENGTH_SHORT)
					.show();
			break;

		}
	}

	private void updataProductFromServer(JSONObject response)
			throws JSONException {
		int retCode = response.getInt("ret_code");
		String infoString = "Wrong Code";
		switch (retCode) {
		case 0:
			infoString = "Succeed";
			break;
		default:
			infoString = response.getString("message");
			
			break;
		}
		Toast.makeText(getBaseContext(), infoString, Toast.LENGTH_SHORT)
		.show();
	}

	private void parseDataFromJson(JSONObject response) throws JSONException {
		Log.v(LOG_TAG, "parseDataFromJson");

		String name = response.getString("name");
		int original_price = response.getInt("original_price");
		int price = response.getInt("price");
		int donate = response.getInt("donate");
		int amount_spec = response.getInt("amount_spec");
		int favorites = response.getInt("favorites");
		String image = response.getString("image");

		m_likeNum = favorites;
		m_zan.setText(favorites + "");
		m_productName.setText(name);
		m_originalPrice.setText("原价：" + original_price + "元");
		m_currentPrice.setText("现价" + price + "元");
		m_remains.setText("剩余" + amount_spec + "个");
		m_fund.setText("将获得" + donate + "元公益资金");
		updateImage(image);
	}

	private void updateImage(String image) {
		ImageLoader imageLoader = new ImageLoader(m_queue, new BitmapCache());
		ImageListener listener = ImageLoader.getImageListener(m_image,
				R.drawable.heartstone_thrall, R.drawable.heartstone_thrall);
		imageLoader.get(BBConfigue.SERVER_HTTP + image, listener);
	}
}
