package com.example.banban.ui.publicwelfare;

/*
 * @author: BruceZhang
 * @description: 公益众筹中PWProject的第 三个Tab选项卡 支持者
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.example.banban.R;
import com.example.banban.network.BitmapCache;
import com.example.banban.network.HttpUtil;
import com.example.banban.other.BBConfigue;
import com.example.banban.ui.fragments.BaseActionBarFragment;
import com.example.banban.ui.otheraccount.OtherAccountActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SupporterFragment extends BaseActionBarFragment {

	protected static final String LOG_TAG = SupporterFragment.class.getName();
	private Activity m_activity;
	private ListView m_listView;
	private SupporterAdapter m_adapter;
	private List<Map<String, Object>> m_listItems;
	private Map<String, Object> item;
	private Handler m_handler;
	private RequestQueue m_queue;
	private ImageLoader m_imageLoader;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_activity = getActivity();
		m_queue = Volley.newRequestQueue(m_activity);
		initHandler();
		m_listItems = new ArrayList<Map<String, Object>>();
		m_imageLoader = new ImageLoader(m_queue, new BitmapCache());
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// 可见时刷新数据
		if (isVisibleToUser) {
			beginDataGetRequest();
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
	}

	private void beginDataGetRequest() {
		int project_id = getActivity().getIntent().getIntExtra("projectId", -1);
		HttpUtil.JsonGetRequest(BBConfigue.SERVER_HTTP
				+ "/projects/supporters/" + project_id, m_handler, m_queue);
	}

	private void parseDataFromServer(JSONObject response) throws JSONException {
		int ret_code = response.getInt("ret_code");
		if (ret_code == 1) {
			Toast.makeText(getActivity(), "Project does not exist!",
					Toast.LENGTH_SHORT).show();
			return;
		} else if (ret_code == 2) {
			Toast.makeText(getActivity(), "Invalid query", Toast.LENGTH_SHORT)
					.show();
			return;
		}

		// else ret_code == 0
		m_listItems.clear();
		m_adapter.notifyDataSetChanged();
		JSONArray jsonArray = response.getJSONArray("supporters");
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			addItem(jsonObject);
		}
		m_listView.smoothScrollToPosition(m_listView.getCount() - 1);
	}

	private void addItem(JSONObject jsonObject) throws JSONException {

		if (!isAdded()) {
			return;
		}

		int user_id = jsonObject.getInt("user_id");
		String image = jsonObject.getString("image");
		String username = jsonObject.getString("username");
		int amount = jsonObject.getInt("amount");
		String time = jsonObject.getString("time");
		int total_projects = jsonObject.getInt("total_projects");

		item = new HashMap<String, Object>();
		item.put("user_id", user_id);
		item.put("supporter_img", image);
		item.put("nickname", username);
		item.put("date", time);
		item.put("money", "支持了" + amount + "元");
		item.put("project_num", "我一共支持了" + total_projects + "个项目");

		m_listItems.add(item);

		m_adapter.notifyDataSetChanged();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.bb_fragment_publicwelfare_supporter, container, false);

		m_listView = (ListView) rootView.findViewById(R.id.lv_supporter);
		m_adapter = new SupporterAdapter();
		m_listView.setAdapter(m_adapter);
		m_listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(m_activity,
						OtherAccountActivity.class);
				int user_id = (Integer) m_listItems.get(position)
						.get("user_id");
				intent.putExtra("user_id", user_id);
				startActivity(intent);
			}
		});

		beginDataGetRequest();
		Log.v(LOG_TAG, "onCreateView called");
		return rootView;
	}

	private static class ViewHolder {
		NetworkImageView supporterImg;
		TextView nickname;
		TextView date;
		TextView money;
		TextView projectNum;
	}

	private class SupporterAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			int count = 0;
			if (m_listItems != null) {
				count = m_listItems.size();
			}
			return count;
		}

		@Override
		public Object getItem(int position) {
			if (m_listItems != null) {
				return m_listItems.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = m_activity.getLayoutInflater().inflate(
						R.layout.bb_item_project, parent, false);
				/*
				 * initialize viewHolder;
				 */
				viewHolder = new ViewHolder();
				viewHolder.supporterImg = (NetworkImageView) convertView
						.findViewById(R.id.img_supporter);
				viewHolder.nickname = (TextView) convertView
						.findViewById(R.id.tv_nickname);
				viewHolder.date = (TextView) convertView
						.findViewById(R.id.tv_date);
				viewHolder.money = (TextView) convertView
						.findViewById(R.id.tv_money);
				viewHolder.projectNum = (TextView) convertView
						.findViewById(R.id.tv_project_num);

				convertView.setTag(viewHolder);
			} else {

				/*
				 * with viewHolder, we just avoid callingfindViewById every time
				 */
				viewHolder = (ViewHolder) convertView.getTag();
			}

			String supporterImg = (String) m_listItems.get(position).get(
					"supporter_img");
			String nickname = (String) m_listItems.get(position)
					.get("nickname");
			String date = (String) m_listItems.get(position).get("date");
			String money = (String) m_listItems.get(position).get("money");
			String projectNum = (String) m_listItems.get(position).get(
					"project_num");

			viewHolder.supporterImg.setImageUrl(BBConfigue.SERVER_HTTP
					+ supporterImg, m_imageLoader);

			// viewHolder.supporterImg.setImageDrawable(supporterImg);
			viewHolder.nickname.setText(nickname);
			viewHolder.date.setText(date);
			viewHolder.money.setText(money);
			viewHolder.projectNum.setText(projectNum);

			return convertView;
		}

	}
}
