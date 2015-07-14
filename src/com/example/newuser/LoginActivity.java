package com.example.newuser;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.halfish.banban.R;
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
	private Button m_visitorButton;
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
		BBConfigue.IS_VISITOR = false;
		initNetwork();
		initWidgets();
	}

	private void initWidgets() {
		m_usernameEditText = (EditText) findViewById(R.id.et_user);
		m_passwordEditText = (EditText) findViewById(R.id.et_passwd);

		m_loginButton = (Button) findViewById(R.id.btn_login);
		m_registerButton = (Button) findViewById(R.id.btn_register);
		m_visitorButton = (Button) findViewById(R.id.btn_visitor);

		m_loginButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				m_username = m_usernameEditText.getText().toString().trim();
				m_password = m_passwordEditText.getText().toString().trim();
				BBConfigue.IS_VISITOR = false;
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

		m_visitorButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				clearAccount();
				BBConfigue.IS_VISITOR = true;
				BBConfigue.USER_NAME = "guest";
				BBConfigue.PASSWORD = "guest";
				BBConfigue.USER_ID = -1;
				Intent intent = new Intent(LoginActivity.this,
						BBMainActivity.class);
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
		pref.edit().putString("type", "user").commit();
	}

	private void clearAccount() {
		BBConfigue.USER_NAME = "";
		BBConfigue.PASSWORD = "";

		SharedPreferences pref = getSharedPreferences("account",
				Context.MODE_PRIVATE);
		pref.edit().clear().commit();
	}

	private void handleResponse(JSONObject response) throws JSONException {
		Log.v(LOG_TAG, response.toString());
		int retCode = response.getInt("ret_code");

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
			Intent intent = new Intent(LoginActivity.this, BBMainActivity.class);
			startActivity(intent);
			finish();
			break;

		default:
			String message = response.getString("message");
			Toast.makeText(m_context, message, Toast.LENGTH_SHORT).show();
			break;
		}
	}
}
