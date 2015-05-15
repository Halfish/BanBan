package com.example.newuser;

import com.example.banban.R;
import com.example.banban.other.BBConfigue;
import com.example.banban.ui.BBMainActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class StartUpActivity extends Activity {

	private static final int LOGIN_MESSAGE = 0;
	private Handler m_handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bb_activity_startup);

		initHandler();
		m_handler.sendEmptyMessageDelayed(LOGIN_MESSAGE, 2000);
	}

	private void initHandler() {
		m_handler = new Handler(getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case LOGIN_MESSAGE:
					loginFromLocal();
					break;

				default:
					break;
				}
			}
		};
	}

	private void loginFromLocal() {
		SharedPreferences pref = getSharedPreferences("account",
				Context.MODE_PRIVATE);
		String username = pref.getString("username", "");
		String password = pref.getString("password", "");
		Intent intent = null;
		if (username.equals("") || password.equals("")) {
			intent = new Intent(StartUpActivity.this, LoginActivity.class);
		} else {
			BBConfigue.USER_NAME = username;
			BBConfigue.PASSWORD = password;
			intent = new Intent(StartUpActivity.this, BBMainActivity.class);
		}
		startActivity(intent);
		finish();
	}
}
