package com.example.banban.ui.publicwelfare;

/*
 * @author: BruceZhang
 * @description: 公益众筹 第一个Tab选项卡 最新
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
import com.example.banban.ui.ProjectActivity;
import com.example.banban.ui.fragments.BaseActionBarFragment;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class NewestFragment extends BaseActionBarFragment {
	
	public NewestFragment(String orderBy) {
		super();
		m_orderBy = orderBy;
	}

	private static final String LOG_TAG = NewestFragment.class.getName();

	private String m_orderBy;
	private Activity m_activity;
	private GridView m_gridView;
	private ProjectInfoAdapter m_adapter;
	private List<Map<String, Object>> m_listItems;
	private Map<String, Object> item;

	private Handler m_handler;
	private RequestQueue m_queue;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_activity = getActivity();
		m_listItems = new ArrayList<Map<String, Object>>();
		initHandler();
		beginDataRequest();
	}

	private void initHandler() {
		m_handler = new Handler(m_activity.getMainLooper()) {
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

	private void beginDataRequest() {
		m_queue = Volley.newRequestQueue(m_activity);
		HttpUtil.JsonGetRequest(BBConfigue.SERVER_HTTP
				+ "/projects/list?order_by=" + m_orderBy, m_handler, m_queue);
	}

	private void updataDataFromServer(JSONObject jsonObject)
			throws JSONException {
		int retCode = jsonObject.getInt("ret_code");
		if (retCode == 1) {
			Log.v(LOG_TAG, "Missing order condition");
			return;
		}

		// else retCode == 0
		m_listItems = new ArrayList<Map<String, Object>>();
		JSONArray jsonArray = jsonObject.getJSONArray("projects");
		if (jsonArray == null) {
			return;
		}
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject object = jsonArray.getJSONObject(i);
			addItem(object);
		}
	}

	private void addItem(JSONObject object) throws JSONException {

		int project_id = object.getInt("project_id");
		String name = object.getString("name");
		int expect_length = object.getInt("expect_length");
		int remaining_days = object.getInt("remaining_days");
		int expect = object.getInt("expect");
		int total_support = object.getInt("total_support");
		int percentage = object.getInt("percentage");
		String image = object.getString("image");

		item = new HashMap<String, Object>();
		item.put("project_id", project_id + "");
		item.put("project_img",
				image);
		item.put("project_name", name);
		item.put("like_number", "34,334");
		item.put("goal", "目标 " + expect_length + "天" + expect + "元");
		item.put("achieved", percentage + "%\n已达");
		item.put("accumulation", total_support + "元\n已融资");
		item.put("remain", remaining_days + "天\n剩余时间");
		m_listItems.add(item);
		
		m_adapter.notifyDataSetChanged();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.bb_fragment_publicwelfare_newest, container, false);

		m_gridView = (GridView) rootView.findViewById(R.id.gv_products_newest);
		m_adapter = new ProjectInfoAdapter();
		m_gridView.setAdapter(m_adapter);
		m_gridView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						String project_id = (String) m_listItems.get(position).get("project_id");
						Intent intent = new Intent(getActivity(),
								ProjectActivity.class);
						intent.putExtra("projectId", Integer.parseInt(project_id));
						startActivity(intent);
					}
				});

		return rootView;
	}

	private static class ViewHolder {
		NetworkImageView projectImg;
		TextView projectName;
		TextView likeNumber;
		TextView goal;
		TextView achieved;
		TextView accumulation;
		TextView remain;
	}

	private class ProjectInfoAdapter extends BaseAdapter {

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
						R.layout.bb_cell_project, parent, false);
				/*
				 * initialize viewHolder;
				 */
				viewHolder = new ViewHolder();
				viewHolder.projectImg = (NetworkImageView) convertView
						.findViewById(R.id.img_project);
				viewHolder.projectName = (TextView) convertView
						.findViewById(R.id.tv_project_name);
				viewHolder.likeNumber = (TextView) convertView
						.findViewById(R.id.tv_like_number);
				viewHolder.goal = (TextView) convertView
						.findViewById(R.id.tv_goal);
				viewHolder.achieved = (TextView) convertView
						.findViewById(R.id.tv_achieve);
				viewHolder.accumulation = (TextView) convertView
						.findViewById(R.id.tv_accumulation);
				viewHolder.remain = (TextView) convertView
						.findViewById(R.id.tv_remaining_days);

				convertView.setTag(viewHolder);
			} else {

				/*
				 * with viewHolder, we just avoid callingfindViewById every time
				 */
				viewHolder = (ViewHolder) convertView.getTag();
			}

			String projectImg = (String) m_listItems.get(position).get(
					"project_img");
			String projectName = (String) m_listItems.get(position).get(
					"project_name");
			String likeNumber = (String) m_listItems.get(position).get(
					"like_number");
			String goal = (String) m_listItems.get(position).get("goal");
			String achieved = (String) m_listItems.get(position)
					.get("achieved");
			String accumulation = (String) m_listItems.get(position).get(
					"accumulation");
			String remain = (String) m_listItems.get(position).get("remain");

			ImageLoader imageLoader = new ImageLoader(m_queue, new BitmapCache());
			viewHolder.projectImg.setImageUrl(BBConfigue.SERVER_HTTP + projectImg, imageLoader);
			
		//	viewHolder.projectImg.setImageDrawable(projectImg);
			viewHolder.projectName.setText(projectName);
			viewHolder.likeNumber.setText(likeNumber);
			viewHolder.goal.setText(goal);
			viewHolder.achieved.setText(achieved);
			viewHolder.accumulation.setText(accumulation);
			viewHolder.remain.setText(remain);

			return convertView;
		}

	}
}
