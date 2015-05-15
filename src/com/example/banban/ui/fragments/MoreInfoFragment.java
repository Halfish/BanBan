package com.example.banban.ui.fragments;

/*
 * @author: BruceZhang
 * @description: 更多信息 fragment
 */


import com.example.banban.R;
import com.example.newuser.LoginActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MoreInfoFragment extends BaseActionBarFragment {
	
	private Button m_logoutButton;
	private Activity m_activity;
	private ActionBar m_actionBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_activity = getActivity();
		m_actionBar = m_activity.getActionBar();
	}
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		//setActionBarCenterTitle(R.string.bb_tab_more_info);
		m_actionBar.setDisplayShowTitleEnabled(false);
		m_actionBar.setDisplayUseLogoEnabled(false);
		m_actionBar.setDisplayShowHomeEnabled(false);
		
		View view = inflater.inflate(R.layout.bb_fragment_more_info,
				container, false);
		
		m_logoutButton = (Button)view.findViewById(R.id.btn_logout);
		m_logoutButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), LoginActivity.class);
				startActivity(intent);
				getActivity().finish();
			}
		});

		return view;
	}
}
