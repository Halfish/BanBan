package com.example.banban.ui.myaccount;

/*
 * @author: BruceZhang
 * @description: 我的账户 分享商品 Tab选项卡点击后，进入的某一特定商品的分享界面
 * 可继续跳转到此商品对应的商家主页
 */

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.banban.R;
import com.example.banban.network.HttpUtil;
import com.example.banban.other.BBConfigue;
import com.example.banban.ui.BaseActionBarActivity;
import com.example.banban.ui.specificbuy.StoreInfoActivity;

@SuppressWarnings("deprecation")
public class ShareProductActivity extends BaseActionBarActivity {

	private static final String LOG_TAG = ShareProductActivity.class.getName();
	private int m_productId;
	private String m_purchaseCode;
	private Handler m_handler;
	private RequestQueue m_queue;
	
	private TextView m_zan;
	private TextView m_productName;
	private TextView m_originPrice;
	private TextView m_currentPrice;
	private TextView m_fund;

	private Button m_storeNameBtn;
	private Button m_keepButton;
	
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
		m_queue = Volley.newRequestQueue(this);
		initWidgets();
		beginDataRequest();
	}

	private void initWidgets() {
		m_zan = (TextView) findViewById(R.id.tv_zan);
		m_productName = (TextView) findViewById(R.id.tv_product_name);
		m_originPrice = (TextView) findViewById(R.id.tv_origin_price);
		m_currentPrice = (TextView) findViewById(R.id.tv_current_price);
		m_fund = (TextView) findViewById(R.id.tv_fund);

		m_storeNameBtn = (Button) findViewById(R.id.btn_store_name);
		m_keepButton = (Button)findViewById(R.id.btn_collected);

		m_storeNameBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ShareProductActivity.this,
						StoreInfoActivity.class);
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
//								Map<String, String> map = new HashMap<String, String>();
//								map.put("purchase_code", m_purchaseCode);
//								HttpUtil.NormalPostRequest(map,
//										"/products/purchases/withdraw",
//										m_handler, m_queue);
//								m_isWithdrawed = true;
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
//					if (m_isShared) {
//						Toast.makeText(getApplicationContext(), "已分享",
//								Toast.LENGTH_SHORT).show();
//						finish();
//					}
//					if (m_isWithdrawed) {
//						Toast.makeText(getApplicationContext(), "已经放弃该商品",
//								Toast.LENGTH_SHORT).show();
//						finish();
//					}
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
