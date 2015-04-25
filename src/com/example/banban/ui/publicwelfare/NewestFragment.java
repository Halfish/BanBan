package com.example.banban.ui.publicwelfare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.banban.R;
import com.example.banban.ui.fragments.BaseActionBarFragment;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

	private Activity m_activity;
	private GridView m_gridView;
	private ProjectInfoAdapter m_adapter;
	private List<Map<String, Object>> m_listItems;
	private Map<String, Object> item;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_activity = getActivity();
		initListViewData();
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
						Intent intent = new Intent(getActivity(), PWProjectActivity.class);
						startActivity(intent);
					}
				});

		return rootView;
	}

	private void initListViewData() {
		m_listItems = new ArrayList<Map<String, Object>>();

		item = new HashMap<String, Object>();
		item.put("project_img",
				getResources().getDrawable(R.drawable.bb_valeera_sanguinar));
		item.put("project_name", "瓦利拉");
		item.put("like_number", "34,334");
		item.put("goal", "目标 30天 78,999元");
		item.put("achieved", "250%\n已达");
		item.put("accumulation", "22,333元\n已融资");
		item.put("remain", "20天\n剩余时间");
		m_listItems.add(item);
		m_listItems.add(item);
		m_listItems.add(item);
		m_listItems.add(item);
		m_listItems.add(item);
		m_listItems.add(item);
		m_listItems.add(item);
		m_listItems.add(item);
		m_listItems.add(item);
		m_listItems.add(item);
		m_listItems.add(item);
		m_listItems.add(item);
		m_listItems.add(item);
		m_listItems.add(item);
		m_listItems.add(item);
		m_listItems.add(item);
	}

	private static class ViewHolder {
		ImageView projectImg;
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
			
			Log.e("getView", "getView function called");
			
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = m_activity.getLayoutInflater().inflate(
						R.layout.bb_cell_product_newest, parent, false);
				/*
				 * initialize viewHolder;
				 */
				viewHolder = new ViewHolder();
				viewHolder.projectImg = (ImageView) convertView
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

			Drawable projectImg = (Drawable) m_listItems.get(position).get(
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

			viewHolder.projectImg.setImageDrawable(projectImg);
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
