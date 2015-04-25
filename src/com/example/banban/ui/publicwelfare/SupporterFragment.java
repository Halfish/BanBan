package com.example.banban.ui.publicwelfare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.banban.R;
import com.example.banban.ui.fragments.BaseActionBarFragment;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SupporterFragment extends BaseActionBarFragment {

	private Activity m_activity;
	private ListView m_listView;
	private SupporterAdapter m_adapter;
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
				R.layout.bb_fragment_publicwelfare_supporter, container, false);

		m_listView = (ListView) rootView.findViewById(R.id.lv_supporter);
		m_adapter = new SupporterAdapter();
		m_listView.setAdapter(m_adapter);
		m_listView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO
					}
				});

		return rootView;
	}

	private void initListViewData() {
		m_listItems = new ArrayList<Map<String, Object>>();

		item = new HashMap<String, Object>();
		item.put("supporter_img",
				getResources().getDrawable(R.drawable.bb_valeera_sanguinar));
		item.put("nickname", "瓦利拉");
		item.put("date", "215/2/30");
		item.put("money", "支持了3,000元");
		item.put("project_num", "我一共支持了22个项目");
		
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
		ImageView supporterImg;
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

			Log.e("getView", "getView function called");

			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = m_activity.getLayoutInflater().inflate(
						R.layout.bb_item_project, parent, false);
				/*
				 * initialize viewHolder;
				 */
				viewHolder = new ViewHolder();
				viewHolder.supporterImg = (ImageView) convertView
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

			Drawable supporterImg = (Drawable) m_listItems.get(position).get(
					"supporter_img");
			String nickname = (String) m_listItems.get(position).get(
					"nickname");
			String date = (String) m_listItems.get(position).get(
					"date");
			String money = (String) m_listItems.get(position).get("money");
			String projectNum = (String) m_listItems.get(position)
					.get("project_num");
			
			
			
			viewHolder.supporterImg.setImageDrawable(supporterImg);
			viewHolder.nickname.setText(nickname);
			viewHolder.date.setText(date);
			viewHolder.money.setText(money);
			viewHolder.projectNum.setText(projectNum);

			return convertView;
		}

	}
}
