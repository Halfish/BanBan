package com.example.banban.ui;

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
import com.example.banban.ui.otheraccount.OtherAccountActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

public class BBSearchUserActivity extends BaseActionBarActivity implements
		OnQueryTextListener {

	protected static final String LOG_TAG = BBSearchUserActivity.class
			.getName();
	private SearchView m_searchView;
	private ListView m_listView;
	private UsersBaseAdapter m_adapter;
	private List<Map<String, Object>> m_listItems;
	private Map<String, Object> item;
	private RequestQueue m_queue;
	private Handler m_handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bb_activity_search_user);

		m_queue = Volley.newRequestQueue(this);
		initWidgets();
		initHandler();
	}

	private void initWidgets() {
		m_searchView = (SearchView) findViewById(R.id.sv_users);
		m_searchView.setOnQueryTextListener(this);
		m_searchView.setSubmitButtonEnabled(true);
		m_searchView.setIconifiedByDefault(false);

		m_listView = (ListView) findViewById(R.id.lv_users);
		m_adapter = new UsersBaseAdapter();
		m_listView.setAdapter(m_adapter);

		m_listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(BBSearchUserActivity.this,
						OtherAccountActivity.class);
				int userId = (Integer)m_listItems.get(position).get("user_id");
				intent.putExtra("user_id", userId);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		beginDataRequest(query);
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		return false;
	}

	private void beginDataRequest(String query) {
		HttpUtil.JsonGetRequest(BBConfigue.SERVER_HTTP
				+ "/users/search?string=" + query, m_handler, m_queue);
		Log.v(LOG_TAG, "beginDataRequest");
	}

	private void initHandler() {
		m_handler = new Handler(getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case HttpUtil.SUCCESS_CODE:
					JSONObject response = (JSONObject) msg.obj;
					try {
						updataDataFromServer(response);
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

	private void updataDataFromServer(JSONObject jsonObject)
			throws JSONException {
		int retCode = jsonObject.getInt("ret_code");
		if (retCode == 1) {
			Log.v(LOG_TAG, "Invalid Query");
			return;
		}

		// else retCode == 0
		m_listItems = new ArrayList<Map<String, Object>>();
		JSONArray jsonArray = jsonObject.getJSONArray("users");
		if (jsonArray == null) {
			return;
		}
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject object = jsonArray.getJSONObject(i);
			addItem(object);
		}
	}

	private void addItem(JSONObject object) throws JSONException {
		int user_id = object.getInt("user_id");
		String username = object.getString("username");
		String image = object.getString("image");

		item = new HashMap<String, Object>();
		item.put("user_id", user_id);
		item.put("username", username);
		item.put("image", image);

		m_listItems.add(item);
		m_adapter.notifyDataSetChanged();
	}

	private static class ViewHolder {
		NetworkImageView head;
		TextView nickname;
	}

	private class UsersBaseAdapter extends BaseAdapter {

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

			String username = (String) m_listItems.get(position)
					.get("username");
			String image = (String) m_listItems.get(position).get("image");

			ImageLoader imageLoader = new ImageLoader(m_queue,
					new BitmapCache());
			viewHolder.head.setImageUrl(BBConfigue.SERVER_HTTP + image,
					imageLoader);

			// viewHolder.storeImg.setImageDrawable(storeImg);
			viewHolder.nickname.setText(username);

			return convertView;
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
