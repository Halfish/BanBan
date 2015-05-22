package com.example.banban.ui;

/*
 * @author: BruceZhang
 * @description: APP入口，主界面。包含五个Fragment选项卡
 */

import com.example.banban.R;
import com.example.banban.other.BBConfigue;
import com.example.banban.ui.fragments.MoreInfoFragment;
import com.example.banban.ui.fragments.MyAccountFragment;
import com.example.banban.ui.fragments.PublicWelfareFragment;
import com.example.banban.ui.fragments.RandomBuyFragment;
import com.example.banban.ui.fragments.SpecificBuyFragment;
import com.example.sortlistview.AlphabetaContactActicity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class BBMainActivity extends FragmentActivity {

	public static final int REQUEST_CODE_LOCATION = 1;
	public static final int RESULT_CODE_LOCATION = 2;
	
	private FragmentTabHost m_tabHost = null;
	private Context m_context;
	private LayoutInflater m_inflater;
	private ActionBar m_actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bb_activity_main);
		m_context = getBaseContext();
		m_inflater = getLayoutInflater();
		m_actionBar = getActionBar();
		
		initActionBar();
		
		initTabHost();
	}
	
	private void initActionBar() {
		m_actionBar.setTitle(R.string.guangzhou);
		m_actionBar.setIcon(R.drawable.bb_location);
		m_actionBar.setDisplayShowTitleEnabled(true);
		m_actionBar.setDisplayUseLogoEnabled(true);
		m_actionBar.setDisplayShowHomeEnabled(true);
		m_actionBar.setHomeButtonEnabled(true);
		
		SharedPreferences pref = getSharedPreferences("location",
				Context.MODE_PRIVATE);
		String location = pref.getString("location", "");
		if (!location.equals("")) {
			getActionBar().setTitle(location);
			BBConfigue.CURRENT_CITY = location;
		}
	}

	@SuppressLint({ "InflateParams" })
	private void initTabHost() {
		m_tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		m_tabHost.setup(m_context, getSupportFragmentManager(),
				R.id.realtabcontent);

		View indicator;
		
		// 随机抢页面Tab
		indicator = getIndicatorView(R.string.bb_tab_random_buy,
				R.layout.bb_indicator_main_activity);
		m_tabHost.addTab(
				m_tabHost.newTabSpec("random_buy").setIndicator(indicator),
				RandomBuyFragment.class, null);

		// 特定抢页面Tab
		indicator = getIndicatorView(R.string.bb_tab_specific_buy,
				R.layout.bb_indicator_main_activity);
		m_tabHost.addTab(
				m_tabHost.newTabSpec("specific_buy").setIndicator(indicator),
				SpecificBuyFragment.class, null);

		// 公益众筹页面Tab
		indicator = getIndicatorView(R.string.bb_tab_public_welfare,
				R.layout.bb_indicator_main_activity);
		m_tabHost.addTab(
				m_tabHost.newTabSpec("public_welfare").setIndicator(indicator),
				PublicWelfareFragment.class, null);

		// 我的账号页面Tab
		indicator = getIndicatorView(R.string.bb_tab_my_account,
				R.layout.bb_indicator_main_activity);
		m_tabHost.addTab(
				m_tabHost.newTabSpec("my_account").setIndicator(indicator),
				MyAccountFragment.class, null);

		// 更多页面Tab
		indicator = getIndicatorView(R.string.bb_tab_more_info,
				R.layout.bb_indicator_main_activity);
		m_tabHost.addTab(
				m_tabHost.newTabSpec("more_info").setIndicator(indicator),
				MoreInfoFragment.class, null);
	}

	private View getIndicatorView(int name, int layoutId) {
		View v = m_inflater.inflate(layoutId, null);
		TextView tv = (TextView) v.findViewById(R.id.tv_indicator);
		tv.setText(name);
		return v;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(BBMainActivity.this,
					AlphabetaContactActicity.class);
			startActivityForResult(intent, REQUEST_CODE_LOCATION);
			break;	

		default:
			break;
		}
		return false;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_LOCATION
				&& resultCode == RESULT_CODE_LOCATION) {
			String location = data.getStringExtra("location");
			getActionBar().setTitle(location);
			
			SharedPreferences pref = getSharedPreferences("location",
					Context.MODE_PRIVATE);
			pref.edit().putString("location", location).commit();
		}
	}

}
