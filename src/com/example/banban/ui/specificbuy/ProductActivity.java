package com.example.banban.ui.specificbuy;

/*
 * @author: BruceZhang
 * @description: 特定抢下的某一个商家的某一个商品，可抢
 */

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.halfish.banban.R;
import com.example.banban.network.HttpUtil;
import com.example.banban.other.BBApplication;
import com.example.banban.other.BBConfigue;
import com.example.banban.ui.BaseActionBarActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProductActivity extends BaseActionBarActivity {
	private static final String LOG_TAG = ProductActivity.class.getName();
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
	private Handler m_buyHandler;
	private Handler m_zanHandler;
	private int m_productId;
	private int m_likeNum;
	private int m_remainAmount;
	private int m_donate;
	private boolean m_favorited = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bb_activity_specificbuy_product);
		m_queue = BBApplication.getQueue();

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

				if (BBConfigue.IS_VISITOR) {
					ProgressDialog dialog = new ProgressDialog(
							ProductActivity.this);
					dialog.setMessage("游客用户无权限，请登录或注册");
					dialog.show();
					return;
				}

				if (m_productId != -1) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("product_id", m_productId + "");
					HttpUtil.NormalPostRequest(map, BBConfigue.SERVER_HTTP
							+ "/products/purchases/spec", m_buyHandler, m_queue);
				}
			}
		});

		m_likeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				if (BBConfigue.IS_VISITOR) {
					ProgressDialog dialog = new ProgressDialog(
							ProductActivity.this);
					dialog.setMessage("游客用户无权限，请登录或注册");
					dialog.show();
					return;
				}

				String action = "";
				if (m_favorited) {
					action = "remove";
				} else {
					action = "add";
				}
				Map<String, String> map = new HashMap<String, String>();
				map.put("product_id", m_productId + "");
				HttpUtil.NormalPostRequest(map, BBConfigue.SERVER_HTTP
						+ "/products/favorites/" + action, m_zanHandler,
						m_queue);
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

		m_buyHandler = new Handler(getMainLooper()) {
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

	private void updataZanFromServer(JSONObject response) throws JSONException {
		int retCode = response.getInt("ret_code");
		switch (retCode) {
		case 0:
			if (m_favorited) {
				m_likeNum--;
			} else {
				m_likeNum++;
			}
			m_zan.setText(m_likeNum + "");
			m_favorited = !m_favorited;
			break;

		default:
			String message = response.getString("message");
			Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
					.show();
			break;
		}

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
		switch (retCode) {
		case 0:
			Toast.makeText(getBaseContext(), "已抢到商品！", Toast.LENGTH_SHORT)
					.show();
			m_remains.setText("剩余" + (m_remainAmount - 1) + "个");
			m_buyButton.setText("已购买");
			m_buyButton.setEnabled(false);
			m_fund.setText("已获得 " + m_donate + "元 公益资金");
			// TODO
			break;

		default:
			String message = response.getString("message");
			Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT)
					.show();
			break;
		}

	}

	private void parseDataFromJson(JSONObject response) throws JSONException {
		Log.v(LOG_TAG, "parseDataFromJson");

		String name = response.getString("name");
		int original_price = response.getInt("original_price");
		int price = response.getInt("price");
		int donate = response.getInt("donate");
		m_donate = donate;
		int amount_spec = response.getInt("amount_spec");
		m_remainAmount = amount_spec;
		int favorites = response.getInt("favorites");
		String image = response.getString("image");
		int purchased = response.getInt("purchased");

		boolean isPurchased = purchased == 1 ? true : false;
		// 购买过此商品，就不能再次购买
		if (isPurchased) {
			m_buyButton.setText("已购买");
			m_buyButton.setEnabled(false);
			m_fund.setText("已获得" + donate + "元公益资金");
		}
		// 如果还剩0个，就抢不了了
		if (amount_spec == 0) {
			m_buyButton.setText("没有商品了");
			m_buyButton.setEnabled(false);
		}

		int favorited = response.getInt("favorited");
		m_favorited = (favorited == 1) ? true : false;

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
