package com.example.banban.ui;

/*
 * @author: BruceZhang
 * @description: APP入口，主界面。包含五个Fragment选项卡
 */

import java.util.ArrayList;
import java.util.List;

import com.example.banban.R;
import com.example.banban.other.BBConfigue;
import com.example.banban.ui.fragments.MoreInfoFragment;
import com.example.banban.ui.fragments.MyAccountFragment;
import com.example.banban.ui.fragments.PublicWelfareFragment;
import com.example.banban.ui.fragments.RandomBuyFragment;
import com.example.banban.ui.fragments.SpecificBuyFragment;
import com.example.sortlistview.AlphabetaContactActicity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.RadioGroup;
import android.widget.Toast;

public class BBMainActivity extends FragmentActivity {

	public static final int REQUEST_CODE_LOCATION = 1000;
	public static final int RESULT_CODE_LOCATION = 2000;

	private ActionBar m_actionBar;
	private RadioGroup m_rgs;
	private List<Fragment> m_fragments;
	private FragmentTabAdapter m_tabAdapter;
	private static final String LOG_TAG = BBMainActivity.class.getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bb_activity_main);

		initActionBar();
		initFragments();
	}

	private void initActionBar() {
		m_actionBar = getActionBar();
		m_actionBar.setTitle(R.string.guangzhou);
		m_actionBar.setIcon(R.drawable.bb_location);
		m_actionBar.setDisplayShowTitleEnabled(true);
		m_actionBar.setDisplayUseLogoEnabled(true);
		m_actionBar.setDisplayShowHomeEnabled(true);
		m_actionBar.setHomeButtonEnabled(true);

		BBConfigue.CURRENT_CITY = getResources().getString(R.string.guangzhou);

		SharedPreferences pref = getSharedPreferences("location",
				Context.MODE_PRIVATE);
		String location = pref.getString("location", "");
		if (!location.equals("")) {
			getActionBar().setTitle(location);
			BBConfigue.CURRENT_CITY = location;
		}
	}

	private void initFragments() {
		m_rgs = (RadioGroup) findViewById(R.id.tabs_rg);
		m_fragments = new ArrayList<Fragment>();
		m_fragments.add(new RandomBuyFragment());
		m_fragments.add(new SpecificBuyFragment());
		m_fragments.add(new PublicWelfareFragment());
		m_fragments.add(new MyAccountFragment());
		m_fragments.add(new MoreInfoFragment());

		m_tabAdapter = new FragmentTabAdapter(this, m_fragments,
				R.id.tab_content, m_rgs);
		m_tabAdapter
				.setOnRgsExtraCheckedChangedListener(new FragmentTabAdapter.OnRgsExtraCheckedChangedListener() {
					@Override
					public void OnRgsExtraCheckedChanged(RadioGroup radioGroup,
							int checkedId, int index) {
						System.out.println("Extra---- " + index
								+ " checked!!! ");
					}
				});
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
		super.onActivityResult(requestCode, resultCode, data);
		Log.v(LOG_TAG, "requestCode = " + requestCode + "resultCode = "
				+ resultCode);
		if (requestCode == REQUEST_CODE_LOCATION
				&& resultCode == RESULT_CODE_LOCATION) {
			String location = data.getStringExtra("location");
			getActionBar().setTitle(location);

			SharedPreferences pref = getSharedPreferences("location",
					Context.MODE_PRIVATE);
			pref.edit().putString("location", location).commit();
			BBConfigue.CURRENT_CITY = location;

			/*
			 * 这样子只要在MainActivity里，就算不在SpecificBuy里改变LOCATION，
			 * 也可以更新城市区级（District）
			 */
			int tab = m_tabAdapter.getCurrentTab();
			if (tab == 1) {
				SpecificBuyFragment fragment = (SpecificBuyFragment) m_tabAdapter
						.getCurrentFragment();
				fragment.beginUpdateDistrictRequest();
			}
		}
		// m_tabAdapter.getCurrentFragment().onActivityResult(requestCode,
		// resultCode, data);
	}

	/*
	 * 防止按返回键不小心退出
	 */
	private static final long INTERVAL_MS_TIME = 2000;
	private static long back_pressed;

	@Override
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
