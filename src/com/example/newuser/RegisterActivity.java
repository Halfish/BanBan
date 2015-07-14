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

public class RegisterActivity extends Activity {

	private Context m_context;
	private static final String LOG_TAG = RegisterActivity.class.getName();

	private Button m_loginButton;
	private Button m_registerButton;
	private EditText m_usernameEditText;
	private EditText m_passwordEditText;
	private EditText m_confirmPWEditText;

	private String m_username;
	private String m_password;
	private String m_confirmPW;

	private RequestQueue m_queue;
	private Handler m_handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bb_activity_register);
		m_context = getBaseContext();

		initNetwork();
		initWidgets();
	}

	private void initWidgets() {
		m_usernameEditText = (EditText) findViewById(R.id.et_user);
		m_passwordEditText = (EditText) findViewById(R.id.et_passwd);
		m_confirmPWEditText = (EditText) findViewById(R.id.et_confirm_passwd);

		m_loginButton = (Button) findViewById(R.id.btn_go_to_login);
		m_registerButton = (Button) findViewById(R.id.btn_register);

		m_loginButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(RegisterActivity.this,
						LoginActivity.class);
				startActivity(intent);
				finish();
			}
		});

		m_registerButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				m_username = m_usernameEditText.getText().toString().trim();
				m_password = m_passwordEditText.getText().toString().trim();
				m_confirmPW = m_confirmPWEditText.getText().toString().trim();
				if (!m_password.equals(m_confirmPW)) {
					Toast.makeText(m_context, "两次密码不一致！", Toast.LENGTH_SHORT)
							.show();
					m_passwordEditText.setText("");
					m_confirmPWEditText.clearComposingText();
				} else {
					beginDataRequest();
				}
			}
		});
	}

	private void beginDataRequest() {

		Map<String, String> params = new HashMap<String, String>();
		params.put("username", m_username);
		params.put("password", m_password);
		HttpUtil.NormalPostRequest(params, BBConfigue.SERVER_HTTP
				+ "/users/register", m_handler, m_queue);
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

				default:
					break;
				}
				super.handleMessage(msg);
			}
		};
	}

	private void handleResponse(JSONObject response) throws JSONException {
		Log.v(LOG_TAG, response.toString());
		int retCode = response.getInt("ret_code");
		switch (retCode) {
		case 0:
			// success
			int userId = response.getInt("user_id");
			BBConfigue.USER_ID = userId;
			saveAccount();
			Intent intent = new Intent(RegisterActivity.this,
					BBMainActivity.class);
			startActivity(intent);
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
	
	private void saveAccount() {
		BBConfigue.USER_NAME = m_username;
		BBConfigue.PASSWORD = m_password;
		SharedPreferences pref = getSharedPreferences("account",
				Context.MODE_PRIVATE);
		pref.edit().putString("username", m_username).commit();
		pref.edit().putString("password", m_password).commit();
	}

}
