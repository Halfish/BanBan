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
import com.android.volley.toolbox.Volley;
import com.example.banban.R;
import com.example.banban.network.HttpUtil;
import com.example.banban.other.BBConfigue;
import com.example.banban.ui.fragments.BaseActionBarFragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SupportFragment extends BaseActionBarFragment {

	protected static final String LOG_TAG = SupportFragment.class.getName();
	private Handler m_handler;
	private RequestQueue m_queue;
	private Button m_fundButton;
	private TextView m_fundTextView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initHandler();
	}

	private void beginDataGetRequest() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("project_id",
				getActivity().getIntent().getIntExtra("projectId", -1) + "");
		map.put("amount", "3");
		m_queue = Volley.newRequestQueue(getActivity());
		HttpUtil.NormalPostRequest(map,
				BBConfigue.SERVER_HTTP + "/projects/support/add", m_handler, m_queue);
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
	}

	private void parseDataFromServer(JSONObject response) throws JSONException {
		int ret_code = response.getInt("ret_code");
		String infoString = "";
		switch (ret_code) {
		case 0:
			infoString = "Suceed";
			break;

		case 1:
			infoString = "Invalid query";
			break;

		case 2:
			infoString = "Project not exist";
			break;

		case 3:
			infoString = "Balance not enough";
			break;

		case 4:
			infoString = "Database exception";
			break;

		default:
			break;
		}

		Toast.makeText(getActivity(), infoString, Toast.LENGTH_SHORT).show();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View view = getActivity().getLayoutInflater().inflate(
				R.layout.bb_fragment_publicwelfare_support, container, false);

		m_fundButton = (Button) view.findViewById(R.id.btn_fund);
		m_fundTextView = (TextView) view.findViewById(R.id.tv_fund_money);
		m_fundButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				m_fundTextView.setVisibility(View.VISIBLE);
				m_fundButton.setEnabled(false);
				beginDataGetRequest();
			}
		});

		return view;
	}
}
