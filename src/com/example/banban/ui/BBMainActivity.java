package com.example.banban.ui;

/*
 * @author: BruceZhang
 * @description: APP入口，主界面。包含五个Fragment选项卡
 */

import com.example.banban.R;
import com.example.banban.ui.fragments.MoreInfoFragment;
import com.example.banban.ui.fragments.MyAccountFragment;
import com.example.banban.ui.fragments.PublicWelfareFragment;
import com.example.banban.ui.fragments.RandomBuyFragment;
import com.example.banban.ui.fragments.SpecificBuyFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class BBMainActivity extends FragmentActivity {

	private FragmentTabHost m_tabHost = null;
	private Context m_context;
	private LayoutInflater m_inflater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bb_activity_main);
		m_context = getBaseContext();
		m_inflater = getLayoutInflater();

		initTabHost();
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

}
