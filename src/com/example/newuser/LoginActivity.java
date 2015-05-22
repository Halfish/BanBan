package com.example.newuser;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.BanBanBusiness.Merchant_main;
import com.example.BanBanBusiness.localStore;
import com.example.banban.R;
import com.example.banban.network.HttpUtil;
import com.example.banban.other.BBConfigue;
import com.example.banban.ui.BBMainActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private Context m_context;
	private static final String LOG_TAG = LoginActivity.class.getName();

	private Button m_loginButton;
	private Button m_registerButton;
	private EditText m_usernameEditText;
	private EditText m_passwordEditText;

	private String m_username;
	private String m_password;
	private int m_userId = -1;

	private RequestQueue m_queue;
	private Handler m_handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bb_activity_login);
		m_context = getBaseContext();

		initNetwork();
		initWidgets();
	}

	private void initWidgets() {
		m_usernameEditText = (EditText) findViewById(R.id.et_user);
		m_passwordEditText = (EditText) findViewById(R.id.et_passwd);

		m_loginButton = (Button) findViewById(R.id.btn_login);
		m_registerButton = (Button) findViewById(R.id.btn_register);

		m_loginButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				m_username = m_usernameEditText.getText().toString().trim();
				m_password = m_passwordEditText.getText().toString().trim();

				beginDataRequest();
			}
		});

		m_registerButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this,
						RegisterActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}

	private void beginDataRequest() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", m_username);
		params.put("password", m_password);
		HttpUtil.NormalPostRequest(params, BBConfigue.SERVER_HTTP
				+ "/users/login", m_handler, m_queue);
	}

	private void initNetwork() {
		m_queue = Volley.newRequestQueue(m_context);
		m_handler = new Handler(getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case HttpUtil.SUCCESS_CODE:
					JSONObject response = (JSONObject) msg.obj;
					try {
						handleResponse(response);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;

				case HttpUtil.FAILURE_CODE:
					Toast.makeText(m_context, "网络错误！", Toast.LENGTH_SHORT)
							.show();
					break;

				default:
					break;
				}
				super.handleMessage(msg);
			}
		};
	}

	private void saveAccount() {
		BBConfigue.USER_NAME = m_username;
		BBConfigue.PASSWORD = m_password;

		SharedPreferences pref = getSharedPreferences("account",
				Context.MODE_PRIVATE);
		pref.edit().putString("username", m_username).commit();
		pref.edit().putString("password", m_password).commit();
		pref.edit().putInt("user_id", m_userId).commit();
	}

	private void handleResponse(JSONObject response) throws JSONException {
		Log.v(LOG_TAG, response.toString());
		int retCode = response.getInt("ret_code");
		int is_store = response.getInt("is_store");

		switch (retCode) {
		case 0:
			// success

			int duration = response.getInt("duration");
			BBConfigue.DURATION = duration;

			String token = response.getString("token");
			BBConfigue.TOKEN = token;

			m_userId = response.getInt("user_id");
			BBConfigue.USER_ID = m_userId;

			saveAccount();
			if (is_store != 1) {
				Intent intent = new Intent(LoginActivity.this,
						BBMainActivity.class);
				startActivity(intent);
				SharedPreferences pref = getSharedPreferences("account",
						Context.MODE_PRIVATE);
				pref.edit().putString("type", "user").commit();
			} else {
				SharedPreferences pref = getSharedPreferences("account",
						Context.MODE_PRIVATE);
				pref.edit().putString("type", "store").commit();
				Intent intent = new Intent(LoginActivity.this, Merchant_main.class);
				localStore.store_id = response.getInt("store_id") + "";
				Log.v("ll", localStore.store_id);
				localStore.USER_NAME = m_username;
				localStore.PASSWORD = m_password;
				
				pref.edit().putString("username", m_username).commit();
				pref.edit().putString("password", m_password).commit();
				pref.edit().putString("store_id", localStore.store_id ).commit();

				startActivity(intent);
			}
			finish();
			break;

		case 1:
			String message = response.getString("message");
			Toast.makeText(m_context, message, Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}
	}
}
