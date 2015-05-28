package com.example.banban.ui.fragments;

/*
 * @author: BruceZhang
 * @description: 特定抢 fragment
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
import com.example.banban.R;
import com.example.banban.network.HttpUtil;
import com.example.banban.other.BBApplication;
import com.example.banban.other.BBConfigue;
import com.example.banban.ui.specificbuy.StoreActivity;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.SearchView.OnQueryTextListener;

public class SpecificBuyFragment extends BaseActionBarFragment implements
		OnQueryTextListener {
	private static final String LOG_TAG = SpecificBuyFragment.class.getName();

	private Activity m_activity;
	private SearchView m_searchView;
	private Spinner m_categorySpinner;
	private Spinner m_districtSpinner;
	private Spinner m_smartOrderSpinner;
	private TextView m_infoTextView;

	private ListView m_listView;
	private StoreBaseAdapter m_adapter;
	private List<Map<String, Object>> m_listItems;
	private List<Map<String, Object>> m_districtsItems;
	private SimpleAdapter m_districtsSimpleAdapter;
	private Map<String, Object> item;
	private Map<String, String> m_cityId;
	private RequestQueue m_queue;
	private ImageLoader m_imageLoader;
	private Handler m_handler;
	private Handler m_districtHandler;
	private String m_orderBy = "favorite";
	private String m_district = "";
	private String m_category = "1";

	private ProgressDialog m_progDiag;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_activity = getActivity();
		m_progDiag = new ProgressDialog(m_activity);
		m_queue = BBApplication.getQueue();
		m_imageLoader = BBApplication.getImageLoader();
		m_listItems = new ArrayList<Map<String, Object>>();
		initArray();
		initHandler();
		Log.v(LOG_TAG, "onCreate called");
	}

	@Override
	public void onResume() {
		beginUpdateDistrictRequest();
		Log.v(LOG_TAG, "onResume called");
		super.onResume();
	}

	@Override
	public void onStart() {
		Log.v(LOG_TAG, "onStart called");
		super.onStart();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		Log.v(LOG_TAG, "setUserVisibleHint: " + isVisibleToUser);
		super.setUserVisibleHint(isVisibleToUser);
	}

	private void initArray() {
		m_cityId = new HashMap<String, String>();
		String[] cities = getResources().getStringArray(R.array.city_id_key);
		String[] ids = getResources().getStringArray(R.array.city_id_value);
		for (int i = 0; i < cities.length; i++) {
			m_cityId.put(cities[i], ids[i]);
		}
	}

	private void initHandler() {
		m_handler = new Handler(m_activity.getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				m_progDiag.dismiss();

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

		m_districtHandler = new Handler(m_activity.getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				m_progDiag.dismiss();
				switch (msg.what) {
				case HttpUtil.SUCCESS_CODE:
					JSONObject response = (JSONObject) msg.obj;
					try {
						updateDistrictSpinner(response);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					Log.v(LOG_TAG + " districtHandler", response.toString());
					break;

				default:
					break;
				}
				super.handleMessage(msg);
			}
		};
	}

	public void beginUpdateDistrictRequest() {

		String id = m_cityId.get(BBConfigue.CURRENT_CITY);

		HttpUtil.JsonGetRequest(BBConfigue.SERVER_HTTP
				+ "/locations/regions?id=" + id, m_districtHandler, m_queue);

		Log.v(LOG_TAG, "beginDataRequest");
	}

	private void beginSearchWithParamRequest() {
		String url = BBConfigue.SERVER_HTTP + "/stores/list?order_by="
				+ m_orderBy + "&city=" + BBConfigue.CURRENT_CITY + "&district="
				+ m_district + "&category=" + m_category;
		HttpUtil.JsonGetRequest(url, m_handler, m_queue);
	}

	private void updataDataFromServer(JSONObject jsonObject)
			throws JSONException {
		Log.v(LOG_TAG, "m_handler updateDataFromServer");
		int retCode = jsonObject.getInt("ret_code");
		switch (retCode) {
		case 0:
			m_listItems.clear();
			m_adapter.notifyDataSetChanged();
			JSONArray jsonArray = jsonObject.getJSONArray("stores");
			if (jsonArray.length() == 0) {
				m_infoTextView.setVisibility(View.VISIBLE);
				return;
			}
			m_infoTextView.setVisibility(View.GONE);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject object = jsonArray.getJSONObject(i);
				addItem(object);
			}

			break;

		case 1:
		case 2:
		case 3:
			String message = jsonObject.getString("message");
			Log.v(LOG_TAG, message);
			break;

		default:
			break;
		}
	}

	private void addItem(JSONObject object) throws JSONException {
		int favorite = object.getInt("favorites");
		int id = object.getInt("store_id");
		String image = object.getString("image");
		String name = object.getString("name");
		double distance = object.getDouble("distance");

		item = new HashMap<String, Object>();
		item.put("id", id);
		item.put("store_img", image);
		item.put("store_name", name);
		item.put("like_number", favorite + "");
		item.put("distance",
				new java.text.DecimalFormat("#.00").format(distance) + "km");
		m_listItems.add(item);
		m_adapter.notifyDataSetChanged();
	}

	private void updateDistrictSpinner(JSONObject response)
			throws JSONException {
		m_districtsItems.clear();
		// also research after get new districts
		beginSearchWithParamRequest();
		JSONArray jsonArray = response.getJSONArray("result").getJSONArray(0);
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject city = jsonArray.getJSONObject(i);
			int id = city.getInt("id");
			String fullname = city.getString("fullname");

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("district", fullname);
			map.put("district_id", id);
			m_districtsItems.add(map);
			m_districtsSimpleAdapter.notifyDataSetChanged();
		}
	}

	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		if (view == null) {
			view = inflater.inflate(R.layout.bb_fragment_specfic_buy,
					container, false);

			m_searchView = (SearchView) view.findViewById(R.id.sv_store);
			m_categorySpinner = (Spinner) view.findViewById(R.id.sp_city);
			m_districtSpinner = (Spinner) view.findViewById(R.id.sp_district);
			m_smartOrderSpinner = (Spinner) view
					.findViewById(R.id.sp_smart_order);
			m_listView = (ListView) view.findViewById(R.id.lv_stores);
			m_infoTextView = (TextView) view.findViewById(R.id.tv_info_nostore);

			initSearchView();
			initSpinners();
			initListView();
			beginUpdateDistrictRequest();
		}

		// 缓存的view需要判断是否已经被加过parent，
		// 如果有parent需要从parent删除，要不然会发生这个view已经有parent的错误。
		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null) {
			parent.removeView(view);
		}

		return view;
	}

	private void initSearchView() {
		m_searchView.setIconifiedByDefault(false);
		m_searchView.setOnQueryTextListener(this);
		m_searchView.setSubmitButtonEnabled(true);
		String hintString = getResources().getString(
				R.string.bb_searchview_hint);
		m_searchView.setQueryHint(hintString);
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		// TODO
		m_progDiag.setMessage("正在搜寻");
		m_progDiag.show();
		HttpUtil.JsonGetRequest(BBConfigue.SERVER_HTTP
				+ "/stores/search?string=" + query, m_handler, m_queue);

		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {

		return false;
	}

	private void initSpinners() {
		m_districtsItems = new ArrayList<Map<String, Object>>();

		m_districtsSimpleAdapter = new SimpleAdapter(m_activity,
				m_districtsItems, R.layout.bb_item_spinner,
				new String[] { "district" }, new int[] { R.id.tv_text });

		m_districtSpinner.setAdapter(m_districtsSimpleAdapter);

		m_categorySpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						Log.v(LOG_TAG, "category selected: " + position);
						m_category = (position + 1) + "";
						beginSearchWithParamRequest();
						Log.v(LOG_TAG, "category spinner");
					}

					public void onNothingSelected(AdapterView<?> parent) {
					}
				});

		m_districtSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						m_district = m_districtsItems.get(position)
								.get("district").toString();
						Log.v(LOG_TAG, "district is selected: " + m_district);
						beginSearchWithParamRequest();
						Log.v(LOG_TAG, "district spinner ");
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
					}
				});

		m_smartOrderSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						switch (position) {
						case 0:
							m_orderBy = "favorite";
							break;

						case 1:
							m_orderBy = "purchase";
							break;

						default:
							break;
						}
						beginSearchWithParamRequest();
						Log.v(LOG_TAG, "order spinner");
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
					}
				});
	}

	private void initListView() {
		m_adapter = new StoreBaseAdapter();
		m_listView.setAdapter(m_adapter);
		m_listView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						int store_id = (Integer) m_listItems.get(position).get(
								"id");

						Intent intent = new Intent(m_activity,
								StoreActivity.class);
						intent.putExtra("store_id", store_id);
						startActivity(intent);
					}
				});

	}

	private static class ViewHolder {
		NetworkImageView storeImg;
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
				viewHolder.storeImg = (NetworkImageView) convertView
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

			String storeImg = (String) m_listItems.get(position).get(
					"store_img");
			String storeName = (String) m_listItems.get(position).get(
					"store_name");
			String likeNumber = (String) m_listItems.get(position).get(
					"like_number");
			String distance = (String) m_listItems.get(position)
					.get("distance");

			viewHolder.storeImg.setImageUrl(BBConfigue.SERVER_HTTP + storeImg,
					m_imageLoader);

			// viewHolder.storeImg.setImageDrawable(storeImg);
			viewHolder.storeNameTV.setText(storeName);
			viewHolder.likeNumberTV.setText(likeNumber);
			viewHolder.distanceTV.setText(distance);

			return convertView;
		}

	}
}
