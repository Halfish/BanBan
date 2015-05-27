package com.example.newuser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.style.SuperscriptSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.banban.R;

public class ChooseLoginActivity extends Activity{
	Button m_bt1,m_bt2;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bb_chose_login);
		m_bt1=(Button) findViewById(R.id.button1);
		m_bt2=(Button) findViewById(R.id.button2);
		m_bt1.setOnClickListener(listener);
		m_bt2.setOnClickListener(listener);
	}
	OnClickListener listener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.button1:
				Intent intent=new Intent(ChooseLoginActivity.this,LoginActivity.class);
				startActivity(intent);
				finish();
				break;
			case R.id.button2:
				Intent intent1=new Intent(ChooseLoginActivity.this,BLoginActivity.class);
				startActivity(intent1);
				finish();
			default:
				break;
			}
		}
	};
}
