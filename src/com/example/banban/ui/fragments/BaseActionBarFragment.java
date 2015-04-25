package com.example.banban.ui.fragments;

import com.example.banban.R;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.widget.TextView;

/*
 * 继承自这个Fragment，可以获得ActionBar标题居中的效果
 */

public class BaseActionBarFragment extends Fragment {
	Activity m_activity;
	ActionBar m_actionBar;
	TextView m_title;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_activity = getActivity();

		initActionBar();
	}

	@SuppressLint("InflateParams") 
	private void initActionBar() {
		m_actionBar = m_activity.getActionBar();

		m_title = (TextView) m_activity.getLayoutInflater().inflate(
				R.layout.bb_view_actionbar, null);

		ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
				ActionBar.LayoutParams.WRAP_CONTENT,
				ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
		m_actionBar.setCustomView(m_title, lp);
		m_actionBar.setDisplayShowCustomEnabled(true);		
	}

	public void setActionBarCenterTitle(int title) {
		m_title.setText(title);
		m_title.invalidate();
	}

}
