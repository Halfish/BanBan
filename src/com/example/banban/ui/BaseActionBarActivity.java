package com.example.banban.ui;

import com.example.banban.R;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

/*
 * 继承自这个Activity，可以获得ActionBar标题居中的效果
 */

public class BaseActionBarActivity extends Activity {
	ActionBar m_actionBar;
	TextView m_title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initActionBar();
	}


	@SuppressLint("InflateParams") 
	private void initActionBar() {
		m_actionBar = getActionBar();

		m_title = (TextView) getLayoutInflater().inflate(
				R.layout.bb_view_actionbar, null);

		ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
				ActionBar.LayoutParams.WRAP_CONTENT,
				ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
		m_actionBar.setCustomView(m_title, lp);
		m_actionBar.setDisplayShowCustomEnabled(true);
		m_actionBar.setDisplayShowTitleEnabled(false);
		m_actionBar.setHomeButtonEnabled(true);
		m_actionBar.setIcon(R.drawable.bb_back);	
	}

	public void setActionBarCenterTitle(int title) {
		m_title.setText(title);
		m_title.invalidate();
	}
	
	public void setActionBarCenterTitle(String title) {
		m_title.setText(title);
		m_title.invalidate();
	}

}
