package com.example.banban.ui.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.banban.R;
import com.example.banban.ui.StoreInfoActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.SearchView.OnQueryTextListener;

public class SpecificBuyFragment extends Fragment implements
		OnQueryTextListener {
	private Activity m_activity;
	private SearchView m_searchView;
//	private Spinner m_allCateSpinner;
//	private Spinner m_allCitySpinner;
//	private Spinner m_smartOrderSpinner;

	private ListView m_listView;
	private StoreBaseAdapter m_adapter;
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
		m_activity.getActionBar().hide();
		
		View view = inflater.inflate(R.layout.bb_fragment_specfic_buy,
				container, false);

		m_searchView = (SearchView) view.findViewById(R.id.sv_store);
		m_searchView.setIconifiedByDefault(false);
		m_searchView.setOnQueryTextListener(this);
		m_searchView.setSubmitButtonEnabled(true);
		String hintString = getResources().getString(
				R.string.bb_searchview_hint);
		m_searchView.setQueryHint(hintString);

//		m_allCateSpinner = (Spinner) view.findViewById(R.id.sp_all_category);
//		m_allCitySpinner = (Spinner) view.findViewById(R.id.sp_all_city);
//		m_smartOrderSpinner = (Spinner) view.findViewById(R.id.sp_smart_order);

		m_listView = (ListView) view.findViewById(R.id.lv_stores);
		m_adapter = new StoreBaseAdapter();
		m_listView.setAdapter(m_adapter);		
		m_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(m_activity, StoreInfoActivity.class);
				startActivity(intent);
			}
		});
		return view;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		
		return false;
	}

	private void initListViewData() {
		m_listItems = new ArrayList<Map<String, Object>>();

		item = new HashMap<String, Object>();
		item.put("store_img", getResources().getDrawable(R.drawable.bb_store_zhao));
		item.put("store_name", "暴雪公司");
		item.put("like_number", "34,334");
		item.put("distance", "1578.6km");
		m_listItems.add(item);

		item = new HashMap<String, Object>();
		item.put("store_img", getResources().getDrawable(R.drawable.bb_store_zhao));
		item.put("store_name", "网易游戏");
		item.put("like_number", "4,017");
		item.put("distance", "15.2km");
		m_listItems.add(item);
		
		item = new HashMap<String, Object>();
		item.put("store_img", getResources().getDrawable(R.drawable.bb_store_zhao));
		item.put("store_name", "暴雪公司");
		item.put("like_number", "34,334");
		item.put("distance", "1578.6km");
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
		ImageView storeImg;
		TextView storeNameTV;
		TextView likeNumberTV;
		TextView distanceTV;
	}

	private class StoreBaseAdapter extends BaseAdapter {

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
						R.layout.bb_item_specific_buy, parent, false);
				/*
				 * initialize viewHolder;
				 */
				viewHolder = new ViewHolder();
				viewHolder.storeImg = (ImageView) convertView
						.findViewById(R.id.img_store);
				viewHolder.storeNameTV = (TextView) convertView
						.findViewById(R.id.tv_store_name);
				viewHolder.likeNumberTV = (TextView) convertView
						.findViewById(R.id.tv_like_number);
				viewHolder.distanceTV = (TextView) convertView
						.findViewById(R.id.tv_distance);

				convertView.setTag(viewHolder);
			} else {
				/*
				 * with viewHolder, we just avoid callingfindViewById every time
				 */
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			Drawable storeImg = (Drawable)m_listItems.get(position).get("store_img");
			String storeName = (String)m_listItems.get(position).get("store_name");
			String likeNumber = (String)m_listItems.get(position).get("like_number");
			String distance = (String)m_listItems.get(position).get("distance");
			
			viewHolder.storeImg.setImageDrawable(storeImg);
			viewHolder.storeNameTV.setText(storeName);
			viewHolder.likeNumberTV.setText(likeNumber);
			viewHolder.distanceTV.setText(distance);

			return convertView;
		}

	}
}
