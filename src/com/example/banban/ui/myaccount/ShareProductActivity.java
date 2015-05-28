package com.example.banban.ui.myaccount;

/*
 * @author: BruceZhang
 * @description: 我的账户 分享商品 Tab选项卡点击后，进入的某一特定商品的分享界面
 * 可继续跳转到此商品对应的商家主页
 */

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.example.banban.R;
import com.example.banban.network.HttpUtil;
import com.example.banban.other.BBApplication;
import com.example.banban.other.BBConfigue;
import com.example.banban.ui.BaseActionBarActivity;
import com.example.banban.ui.specificbuy.StoreActivity;

public class ShareProductActivity extends BaseActionBarActivity {

	private static final String LOG_TAG = ShareProductActivity.class.getName();
	private int m_productId;
	private String m_purchaseCode;
	private Handler m_handler;
	private Handler m_zanHandler;
	private Handler m_keepHandler;
	private RequestQueue m_queue;

	private TextView m_zan;
	private TextView m_productName;
	private TextView m_originPrice;
	private TextView m_currentPrice;
	private TextView m_fund;

	private Button m_storeNameBtn;
	private Button m_keepButton;

	private ImageButton m_zanButton;
	private int m_zanNum = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bb_activity_myaccount_share);

		m_productId = getIntent().getIntExtra("product_id", -1);
		m_purchaseCode = getIntent().getStringExtra("purchase_code");
		if (m_purchaseCode == null) {
			m_purchaseCode = "";
		}
		Log.v(LOG_TAG, "m_productId is " + m_productId);
		Log.v(LOG_TAG, "m_purchase_code is " + m_purchaseCode);

		initHandler();
		m_queue = BBApplication.getQueue();
		initWidgets();
		beginDataRequest();
	}

	private void initWidgets() {
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

		m_zan = (TextView) findViewById(R.id.tv_zan);
		m_productName = (TextView) findViewById(R.id.tv_product_name);
		m_originPrice = (TextView) findViewById(R.id.tv_origin_price);
		m_currentPrice = (TextView) findViewById(R.id.tv_current_price);
		m_fund = (TextView) findViewById(R.id.tv_fund);

		m_storeNameBtn = (Button) findViewById(R.id.btn_store_name);
		m_keepButton = (Button) findViewById(R.id.btn_collected);

		m_storeNameBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ShareProductActivity.this,
						StoreActivity.class);
				startActivity(intent);
			}
		});

		m_keepButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				new AlertDialog.Builder(ShareProductActivity.this)
						.setTitle("确定留为己用吗？")
						.setPositiveButton("确定", new OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Map<String, String> map = new HashMap<String, String>();
								map.put("purchase_code", m_purchaseCode);
								Log.v(LOG_TAG, "purchaseCode is " + m_purchaseCode);
								HttpUtil.NormalPostRequest(map,
										BBConfigue.SERVER_HTTP
												+ "/products/purchases/transfer",
										m_keepHandler, m_queue);
							}
						}).setNegativeButton("取消", new OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// do nothing
							}
						}).show();
			}
		});

	}

	private void initHandler() {
		m_handler = new Handler(getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case HttpUtil.SUCCESS_CODE:
					JSONObject response = (JSONObject) msg.obj;
					try {
						parseDataFromJson(response);
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
						parseZanDataFromServer(response);
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

		m_keepHandler = new Handler(getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case HttpUtil.SUCCESS_CODE:
					JSONObject response = (JSONObject) msg.obj;
					Log.v(LOG_TAG, "revoke: " + response.toString());
					try {
						int retCode = response.getInt("ret_code");
						switch (retCode) {
						case 0:
							Toast.makeText(getApplicationContext(), "验证成功！",
									Toast.LENGTH_LONG).show();
							break;

						case 1:
						case 2:
						case 3:
						case 4:
						case 5:
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
					break;

				default:
					break;
				}
				super.handleMessage(msg);
			}
		};
	}

	private void beginDataRequest() {
		HttpUtil.JsonGetRequest(BBConfigue.SERVER_HTTP + "/products/detail/"
				+ m_productId, m_handler, m_queue);
	}

	private void parseDataFromJson(JSONObject response) throws JSONException {
		Log.v(LOG_TAG, "parseDataFromJson");

		int favorites = response.getInt("favorites");
		String name = response.getString("name");
		int original_price = response.getInt("original_price");
		int price = response.getInt("price");
		int donate = response.getInt("donate");
		String store_name = response.getString("store_name");

		m_zan.setText(favorites + "");
		m_productName.setText(name);
		m_originPrice.setText("原价：" + original_price + "元");
		m_currentPrice.setText("现价：" + price + "元");
		m_fund.setText("将获得" + donate + "元公益资金");
		m_storeNameBtn.setText(store_name);
	}

	private void parseZanDataFromServer(JSONObject jsonObject)
			throws JSONException {
		int retCode = jsonObject.getInt("ret_code");
		switch (retCode) {
		case 0:
			m_zan.setText((m_zanNum + 1) + "");
			m_zanNum++;
			Toast.makeText(getApplicationContext(), "点赞 成功！", Toast.LENGTH_LONG)
					.show();
			break;

		case 1:
		case 2:
		case 3:
			String message = jsonObject.getString("message");
			Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG)
					.show();
			break;

		case 4:
			Toast.makeText(getApplicationContext(), "您已经点过赞了！",
					Toast.LENGTH_LONG).show();
			break;

		default:
			break;
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
		return super.onOptionsItemSelected(item);
	}

}
