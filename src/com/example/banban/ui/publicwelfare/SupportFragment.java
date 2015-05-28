package com.example.banban.ui.publicwelfare;

/*
 * @author: BruceZhang
 * @description: 公益众筹中PWProject的第二个Tab选项卡 支持
 * 用户可以在这里投入公益资金
 */

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.example.banban.R;
import com.example.banban.network.HttpUtil;
import com.example.banban.other.BBApplication;
import com.example.banban.other.BBConfigue;
import com.example.banban.ui.fragments.BaseActionBarFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SupportFragment extends BaseActionBarFragment {

	protected static final String LOG_TAG = SupportFragment.class.getName();
	private Handler m_handler;
	private Handler m_balanceHandler;
	private Handler m_detailHandler;
	private RequestQueue m_queue;
	private Button m_fundButton;
	private int m_balance = 0;
	private int m_userSupport = 0;
	private int m_donateMoney = 0;
	private TextView m_donateTextView;
	private Activity m_activity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_queue = BBApplication.getQueue();
		m_activity = getActivity();
		initHandler();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {

		if (isVisibleToUser && isAdded()) {
			HttpUtil.JsonGetRequest(BBConfigue.SERVER_HTTP
					+ "/projects/detail/"
					+ getActivity().getIntent().getIntExtra("projectId", -1),
					m_detailHandler, m_queue);
		}
		super.setUserVisibleHint(isVisibleToUser);
	}

	private void initHandler() {
		m_handler = new Handler(getActivity().getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case HttpUtil.SUCCESS_CODE:
					JSONObject response = (JSONObject) msg.obj;
					try {
						parseDataFromServer(response);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					Log.v(LOG_TAG, response.toString());
					break;
				case HttpUtil.FAILURE_CODE:
					Log.v(LOG_TAG, "failed");

				default:
					break;
				}
				super.handleMessage(msg);
			}
		};

		m_balanceHandler = new Handler(getActivity().getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case HttpUtil.SUCCESS_CODE:
					JSONObject response = (JSONObject) msg.obj;
					try {
						parseBalanceDataFromServer(response);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					Log.v(LOG_TAG, response.toString());
					break;
				case HttpUtil.FAILURE_CODE:
					Log.v(LOG_TAG, "failed");

				default:
					break;
				}
				super.handleMessage(msg);
			}
		};

		m_detailHandler = new Handler(getActivity().getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case HttpUtil.SUCCESS_CODE:
					JSONObject response = (JSONObject) msg.obj;
					try {
						parseProjectDetailFromServer(response);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					Log.v(LOG_TAG, response.toString());
					break;
				case HttpUtil.FAILURE_CODE:
					Log.v(LOG_TAG, "failed");

				default:
					break;
				}
				super.handleMessage(msg);
			}
		};

	}

	private void parseDataFromServer(JSONObject response) throws JSONException {
		int ret_code = response.getInt("ret_code");
		String infoString = "";
		switch (ret_code) {
		case 0:
			infoString = "成功投入公益基金" + m_donateMoney + "元";
			m_userSupport += m_donateMoney;
			m_donateTextView.setText("已投入公益资金：" + m_userSupport + " 元");
			break;

		case 1:
		case 2:
		case 3:
		case 4:
			infoString = response.getString("message");
			break;

		default:
			break;
		}

		Toast.makeText(getActivity(), infoString, Toast.LENGTH_SHORT).show();
	}

	private void parseBalanceDataFromServer(JSONObject response)
			throws JSONException {
		int ret_code = response.getInt("ret_code");
		String infoString = "";
		switch (ret_code) {
		case 0:
			infoString = "Succeed";
			m_balance = response.getInt("balance");
			initAlertDialog();
			break;

		case 1:
			infoString = "DataBase Exception";
			break;

		default:
			break;
		}
		Log.v(LOG_TAG, "parseBalance" + infoString);
	}

	@SuppressLint("InflateParams")
	private void initAlertDialog() {
		View view = getActivity().getLayoutInflater().inflate(
				R.layout.bb_dialogview_donate, null, false);
		TextView textView = (TextView) view.findViewById(R.id.textView1);
		textView.setText("我的剩余公益基金还有：" + m_balance + "元");
		final EditText editText = (EditText) view.findViewById(R.id.editText1);

		new AlertDialog.Builder(getActivity()).setTitle("投入公益基金").setView(view)
				.setNegativeButton("取消", null)
				.setPositiveButton("确定投入", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String money = editText.getText().toString();
						try {
							m_donateMoney = Integer.parseInt(money);
						} catch (Exception e) {
							m_donateMoney = -1;
						}

						Log.v(LOG_TAG, "I want to donate money "
								+ m_donateMoney);
						if (m_donateMoney > 0 && m_donateMoney < 99999) {
							beginAddSupportRequest(m_donateMoney);
						}
					}
				}).show();

	}

	private void parseProjectDetailFromServer(JSONObject jsonObject)
			throws JSONException {
		int ret_code = jsonObject.getInt("ret_code");
		String infoString = "";
		switch (ret_code) {
		case 0:
			infoString = "Succeed";
			m_userSupport = jsonObject.getInt("user_support");
			m_donateTextView.setText("已投入公益资金：" + m_userSupport + " 元");
			break;

		case 1:
			infoString = jsonObject.getString("message");
			break;

		default:
			break;
		}
		Log.v(LOG_TAG, "parseBalance" + infoString);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View view = getActivity().getLayoutInflater().inflate(
				R.layout.bb_fragment_publicwelfare_support, container, false);

		m_donateTextView = (TextView) view.findViewById(R.id.tv_donate);
		m_fundButton = (Button) view.findViewById(R.id.btn_fund);
		m_fundButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (BBConfigue.IS_VISITOR) {
					ProgressDialog dialog = new ProgressDialog(m_activity);
					dialog.setMessage("游客用户无权限，请登录或注册");
					dialog.show();
					return;
				}
				// 查询剩余公益基金，等查到数据再启动AlertDialog
				HttpUtil.JsonGetRequest(BBConfigue.SERVER_HTTP
						+ "/users/balance", m_balanceHandler, m_queue);
			}
		});

		return view;
	}

	private void beginAddSupportRequest(int amount) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("project_id",
				getActivity().getIntent().getIntExtra("projectId", -1) + "");
		map.put("amount", amount + "");
		HttpUtil.NormalPostRequest(map, BBConfigue.SERVER_HTTP
				+ "/projects/support/add", m_handler, m_queue);
	}
}
