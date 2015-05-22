package com.example.BanBanBusiness;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.BBput.ThingPutting;
import com.example.BusinessBuyManager.Fragment3;
import com.example.BusinessMyStore.Fragment1;
import com.example.BusinessThingManager.Fragment2;
import com.example.banban.R;
import com.example.newuser.LoginActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioGroup;
import android.widget.TabHost.TabSpec;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class Merchant_main extends FragmentActivity {

	private FragmentTabHost mTabHost;
	private RadioGroup mTabRg;

	private final Class[] fragments = { Fragment1.class, Fragment2.class,
			Fragment3.class };
	public static RequestQueue BBQueue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setCustomView(R.layout.title);
		getActionBar().setDisplayShowCustomEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setIcon(R.drawable.bb_back);
		BBQueue = Volley.newRequestQueue(Merchant_main.this);
		initView();
	}

	private void initView() {
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		final TextView text = (TextView) findViewById(R.id.text);
		// 得到fragment的个数
		int count = fragments.length;
		for (int i = 0; i < count; i++) {
			// 为每一个Tab按钮设置图标、文字和内容
			TabSpec tabSpec = mTabHost.newTabSpec(i + "").setIndicator(i + "");
			// 将Tab按钮添加进Tab选项卡中
			mTabHost.addTab(tabSpec, fragments[i], null);
		}
		mTabRg = (RadioGroup) findViewById(R.id.tab_rg_menu);
		mTabRg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.tab_rb_1:
					mTabHost.setCurrentTab(0);
					text.setText("我的店铺");
					break;
				case R.id.tab_rb_2:
					mTabHost.setCurrentTab(1);
					text.setText("宝贝管理");
					break;
				case R.id.tab_rb_3:
					mTabHost.setCurrentTab(2);
					text.setText("交易管理");
					break;

				default:
					break;
				}
			}
		});

		mTabHost.setCurrentTab(0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case android.R.id.home:
			Intent intent = new Intent(Merchant_main.this,LoginActivity.class);
			startActivity(intent);
			finish();
			break;

		case R.id.action_puttings:
			Intent intent2 = new Intent(Merchant_main.this, ThingPutting.class);
			startActivity(intent2);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
