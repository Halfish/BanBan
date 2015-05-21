package com.example.banban.ui.fragments;

/*
 * @author: BruceZhang
 * @description: 更多信息 fragment
 */

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.example.banban.R;
import com.example.banban.ui.BBUIUtil;
import com.example.newuser.LoginActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class MoreInfoFragment extends BaseActionBarFragment {

	private Button m_aboutButton;
	private Button m_inviteButton;
	private Button m_feedBackButton;
	private Button m_remindButton;
	private Button m_payAssistButton;
	private Button m_clearButton;
	private Button m_checkUpdateButton;
	private Button m_logoutButton;

	private Activity m_activity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_activity = getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.bb_fragment_more_info, container,
				false);

		m_aboutButton = (Button) view.findViewById(R.id.btn_about_banban);
		m_inviteButton = (Button) view.findViewById(R.id.btn_invite);
		m_feedBackButton = (Button) view.findViewById(R.id.btn_feedback);
		m_remindButton = (Button) view.findViewById(R.id.btn_remind);
		m_payAssistButton = (Button) view.findViewById(R.id.btn_pay_assist);
		m_clearButton = (Button) view.findViewById(R.id.btn_clear_cache);
		m_checkUpdateButton = (Button) view.findViewById(R.id.btn_check_update);
		m_logoutButton = (Button) view.findViewById(R.id.btn_logout);
		initButtons(view);

		return view;
	}

	private void showShare() {
		ShareSDK.initSDK(getActivity());
		OnekeyShare oks = new OnekeyShare();
		// 分享时Notification的图标和文字
//		oks.setNotification(R.drawable.ic_launcher,
//				getString(R.string.app_name));
		oks.setText("我是分享文本");
		oks.setImageUrl("http://f1.sharesdk.cn/imgs/2014/05/21/oESpJ78_533x800.jpg");
		// 启动分享GUI
		oks.show(getActivity());
	}

	private void initButtons(View view) {
		m_aboutButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				BBUIUtil.getInfoDialog(m_activity, null, "半半公益是一款公益软件").show();
			}
		});

		m_inviteButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//showShare();
			}
		});

		m_feedBackButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				BBUIUtil.getInfoDialog(m_activity, null,
						"请发至反馈邮箱feedback@banban.com").show();
			}
		});

		m_remindButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

			}
		});

		m_payAssistButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

			}
		});

		m_clearButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				SharedPreferences pref = getActivity().getSharedPreferences(
						"account", Context.MODE_PRIVATE);
				pref.edit().putString("username", "").commit();
				pref.edit().putString("password", "").commit();
				Toast.makeText(m_activity, "已经清除", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(getActivity(), LoginActivity.class);
				startActivity(intent);
				getActivity().finish();
			}
		});

		m_checkUpdateButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(m_activity, "已经是最新版了！", Toast.LENGTH_SHORT)
						.show();
			}
		});

		m_logoutButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), LoginActivity.class);
				startActivity(intent);
				getActivity().finish();
			}
		});
	}
}
