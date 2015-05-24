package com.example.banban.ui.publicwelfare;

/*
 * @author: BruceZhang
 * @description: 公益众筹中PWProject的第一个Tab选项卡 公益项目的详情
 */

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
import android.widget.TextView;
import android.widget.Toast;

public class DetailFragment extends BaseActionBarFragment {

	protected static final String LOG_TAG = DetailFragment.class.getName();
	private Handler m_handler;
	private RequestQueue m_queue;
	private TextView m_detail;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_queue = Volley.newRequestQueue(getActivity());
		initHandler();
	}
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		/*
		 * 由于这里是第一个Fragment，所以要判断isAdd，否则直接网络请求的话，
		 * 会因为Activity还没来得及Create而getIntent引用空指针程序崩溃
		 */
		if (isVisibleToUser && isAdded()) {
			beginDataGetRequest();
		}
		super.setUserVisibleHint(isVisibleToUser);
	}

	private void beginDataGetRequest() {
		
		HttpUtil.JsonGetRequest(BBConfigue.SERVER_HTTP + "/projects/detail/"
				+ getActivity().getIntent().getIntExtra("projectId", -1),
				m_handler, m_queue);
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
		if (ret_code == 1) {
			Toast.makeText(getActivity(), "Project does not exist!",
					Toast.LENGTH_SHORT).show();
			return;
		}

		// else ret_code == 0
		String description = response.getString("description");
		m_detail.setText(description);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View view = getActivity().getLayoutInflater().inflate(
				R.layout.bb_fragment_publicwelfare_detail, container, false);
		m_detail = (TextView) view.findViewById(R.id.tv_detail);
		
		beginDataGetRequest();
		return view;
	}
}
