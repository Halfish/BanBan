package com.example.banban.ui.fragments;

/*
 * @author: BruceZhang
 * @description: 更多信息 fragment
 */

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.example.banban.R;
import com.example.banban.other.BBConfigue;
import com.example.banban.ui.BBUIUtil;
import com.example.newuser.ChooseLoginActivity;
import com.example.newuser.LoginActivity;
import com.example.newuser.RegisterActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MoreInfoFragment extends BaseActionBarFragment {

	private static final String LOG_TAG = MoreInfoFragment.class.getName();
	private Button m_aboutButton;
	private Button m_inviteButton;
	private Button m_feedBackButton;
	private Button m_remindButton;
	private Button m_payAssistButton;
	private Button m_clearButton;
	private Button m_checkUpdateButton;
	private Button m_logoutButton;
	private Button m_loginButton;
	private Button m_registerButton;
	private RelativeLayout m_visitorRly;

	private Activity m_activity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_activity = getActivity();
		Log.v(LOG_TAG, "onCreate called");
	}
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		if(BBConfigue.IS_VISITOR) {
			m_visitorRly.setVisibility(View.VISIBLE);
		} else {
			m_visitorRly.setVisibility(View.GONE);
		}
		super.setUserVisibleHint(isVisibleToUser);
	}

	@Override
	public void onStart() {
		Log.v(LOG_TAG, "onStart called");
		super.onStart();
	}

	@Override
	public void onResume() {
		Log.v(LOG_TAG, "onResume called");
		super.onResume();
	}

	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		if (view == null) {
			view = inflater.inflate(R.layout.bb_fragment_more_info, container,
					false);
			m_visitorRly = (RelativeLayout)view.findViewById(R.id.rly_visitor);
			
			m_aboutButton = (Button) view.findViewById(R.id.btn_about_banban);
			m_inviteButton = (Button) view.findViewById(R.id.btn_invite);
			m_feedBackButton = (Button) view.findViewById(R.id.btn_feedback);
			m_remindButton = (Button) view.findViewById(R.id.btn_remind);
			m_payAssistButton = (Button) view.findViewById(R.id.btn_pay_assist);
			m_clearButton = (Button) view.findViewById(R.id.btn_clear_cache);
			m_checkUpdateButton = (Button) view
					.findViewById(R.id.btn_check_update);
			m_logoutButton = (Button) view.findViewById(R.id.btn_logout);
			m_loginButton = (Button)view.findViewById(R.id.btn_login);
			m_registerButton = (Button)view.findViewById(R.id.btn_register);
			initButtons(view);
		}
		// 缓存的view需要判断是否已经被加过parent，
		// 如果有parent需要从parent删除，要不然会发生这个view已经有parent的错误。
		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null) {
			parent.removeView(view);
		}

		return view;
	}

	private void showShare() {
		ShareSDK.initSDK(getActivity());
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();

		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(getString(R.string.share));
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl("http://sharesdk.cn");
		// text是分享文本，所有平台都需要这个字段
		oks.setText("我是分享文本");
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		// oks.setImagePath("/sdcard/test.jpg");// 确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("http://sharesdk.cn");
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("我是测试评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl("http://sharesdk.cn");

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
				showShare();
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
				// TODO 首先你得有缓存
				Toast.makeText(m_activity, "已经清除", Toast.LENGTH_SHORT).show();
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
				Intent intent = new Intent(getActivity(), ChooseLoginActivity.class);
				startActivity(intent);
				getActivity().finish();
			}
		});
		
		m_loginButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), LoginActivity.class);
				startActivity(intent);
				getActivity().finish();
			}
		});
		
		m_registerButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), RegisterActivity.class);
				startActivity(intent);
				getActivity().finish();
			}
		});
	}
}
