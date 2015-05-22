package com.example.banban.ui.otheraccount;

/*
 * @author: BruceZhang
 * @description: 关注TA的人页面，进入此页面前，要传一个此用户的ID，
 * 则会根据此ID去查关注TA的用户，此Package下的Activity同理
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.example.banban.R;
import com.example.banban.network.BitmapCache;
import com.example.banban.network.HttpUtil;
import com.example.banban.other.BBConfigue;

public class MyFansActivity extends Activity {
	private static final String LOG_TAG = MyFansActivity.class
			.getName();

	private ListView listView;
	private StoresBaseAdapter m_adapter;
	private ArrayList<Map<String, Object>> m_listItems;
	private Map<String, Object> m_item;

	private int m_userId;
	private Handler m_userInfoHandler;
	private RequestQueue m_queue;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bb_other_list);

		initUser();
		initListView();
		initHandlers();
		beginDataRequest();
	}

	private void initUser() {
		m_userId = getIntent().getIntExtra("user_id", -1);
		if (m_userId == -1) {
			// 否则是当前账户用户
			m_userId = BBConfigue.USER_ID;
		}
	}

	private void initListView() {
		m_listItems = new ArrayList<Map<String, Object>>();
		listView = (ListView) findViewById(R.id.listview);
		// 将数据绑定
		m_adapter = new StoresBaseAdapter();
		listView.setAdapter(m_adapter);
	}

	private void initHandlers() {
		m_userInfoHandler = new Handler(getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case HttpUtil.SUCCESS_CODE:
					JSONObject response = (JSONObject) msg.obj;
					try {
						updateUserInfo(response);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					Log.v(LOG_TAG, response.toString());
					break;

				default:
					break;
				}
				super.handleMessage(msg);
			}
		};
	}

	private void beginDataRequest() {
		m_queue = Volley.newRequestQueue(this);
		HttpUtil.JsonGetRequest(BBConfigue.SERVER_HTTP
				+ "/users/followings/" + m_userId, m_userInfoHandler,
				m_queue);
		Log.v(LOG_TAG, "beginDataRequest");
	}

	private void updateUserInfo(JSONObject jsonObject) throws JSONException {
		int retCode = jsonObject.getInt("ret_code");
		if (retCode == 1) {
			Log.v(LOG_TAG, "User not exist");
			return;
		}

		// else retCode == 0
		m_listItems.clear();
		m_adapter.notifyDataSetChanged();
		JSONArray jsonArray = jsonObject.getJSONArray("followings");
		if (jsonArray.length() == 0) {
			Toast.makeText(this, "TA没有关注任何人！", Toast.LENGTH_LONG).show();
			return;
		}
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject object = jsonArray.getJSONObject(i);
			addItem(object);
		}
	}

	private void addItem(JSONObject object) throws JSONException {
		String username = object.getString("username");
		int user_id = object.getInt("user_id");
		String image = object.getString("image");

		m_item = new HashMap<String, Object>();
		m_item.put("name", username);
		m_item.put("user_id", user_id);
		m_item.put("image", image);

		m_listItems.add(m_item);
		m_adapter.notifyDataSetChanged();
	}

	private static class ViewHolder {
		NetworkImageView head;
		TextView nickname;
	}

	private class StoresBaseAdapter extends BaseAdapter {

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
				convertView = getLayoutInflater().inflate(
						R.layout.bb_item_users, parent, false);
				/*
				 * initialize viewHolder;
				 */
				viewHolder = new ViewHolder();
				viewHolder.head = (NetworkImageView) convertView
						.findViewById(R.id.img_head);
				viewHolder.nickname = (TextView) convertView
						.findViewById(R.id.tv_name);

				convertView.setTag(viewHolder);
			} else {
				/*
				 * with viewHolder, we just avoid callingfindViewById every time
				 */
				viewHolder = (ViewHolder) convertView.getTag();
			}

			String name = (String) m_listItems.get(position).get("name");
			String image = (String) m_listItems.get(position).get("image");

			ImageLoader imageLoader = new ImageLoader(m_queue,
					new BitmapCache());
			viewHolder.head.setImageUrl(BBConfigue.SERVER_HTTP + image,
					imageLoader);

			// viewHolder.storeImg.setImageDrawable(storeImg);
			viewHolder.nickname.setText(name);

			return convertView;
		}
	}

}
