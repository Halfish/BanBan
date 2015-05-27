package com.example.BanBanBusiness;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.BBput.ThingPutting;
import com.example.BusinessBuyManager.Fragment3;
import com.example.BusinessMyStore.Fragment1;
import com.example.BusinessThingManager.Fragment2;
import com.example.banban.R;
import com.example.newuser.ChooseLoginActivity;
import com.example.newuser.LoginActivity;

import android.R.bool;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RadioGroup;
import android.widget.TabHost.TabSpec;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class Merchant_main extends FragmentActivity {

	private FragmentTabHost mTabHost;
	private RadioGroup mTabRg;
	private static final long INTERVAL_MS_TIME = 2000;
	private static long back_pressed;

	private final Class[] fragments = { Fragment1.class, Fragment2.class,
			Fragment3.class };
	public static RequestQueue BBQueue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getActionBar().setDisplayShowTitleEnabled(false);
		ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
				ActionBar.LayoutParams.WRAP_CONTENT,
				ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER);

		TextView title = (TextView) getLayoutInflater().inflate(
				R.layout.title, null);
		getActionBar().setCustomView(title,lp);
		getActionBar().setDisplayShowCustomEnabled(true);
		
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
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
	}
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		Builder dialog;
		switch (id) {
		case android.R.id.home:
			dialog=new AlertDialog.Builder(Merchant_main .this);
			dialog.setTitle("是否退出");
			dialog.setPositiveButton("是",new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
						Intent intent=new Intent(Merchant_main .this,ChooseLoginActivity.class);
						startActivity(intent);
						SharedPreferences sp = getSharedPreferences("account", Context.MODE_PRIVATE);
						Editor editor = sp.edit();  
						editor.clear();  
						editor.commit(); 
						finish();
					}
			});
			dialog.setNegativeButton("否", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					}
			});
			dialog.show();
			break;
		}
		return false;
	}  
	public void onBackPressed() {
		if (back_pressed + INTERVAL_MS_TIME > System.currentTimeMillis()) {
			super.onBackPressed();
			android.os.Process.killProcess(android.os.Process.myPid()); // 完全退出程序
		} else {
			Toast.makeText(getBaseContext(),
					getResources().getString(R.string.bb_double_press_back),
					Toast.LENGTH_SHORT).show();
		}
		back_pressed = System.currentTimeMillis();
	}
}
