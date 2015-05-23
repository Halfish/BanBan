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
import com.android.volley.toolbox.Volley;
import com.example.banban.R;
import com.example.banban.network.BitmapCache;
import com.example.banban.network.HttpUtil;
import com.example.banban.other.BBConfigue;
import com.example.banban.ui.specificbuy.StoreInfoActivity;

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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

public class SpecificBuyFragment extends BaseActionBarFragment implements
		OnQueryTextListener {
	private static final String LOG_TAG = SpecificBuyFragment.class.getName();

	private Activity m_activity;
	private SearchView m_searchView;
	private Spinner m_citySpinner;
	private Spinner m_districtSpinner;
	private Spinner m_smartOrderSpinner;

	private ListView m_listView;
	private StoreBaseAdapter m_adapter;
	private List<Map<String, Object>> m_listItems;
	private List<Map<String, Object>> m_citiesItems;
	private List<Map<String, Object>> m_districtsItems;
	private SimpleAdapter m_citiesSimpleAdapter;
	private SimpleAdapter m_districtsSimpleAdapter;
	private Map<String, Object> item;
	private RequestQueue m_queue;
	private Handler m_handler;
	private Handler m_citiesHandler;
	private Handler m_districtHandler;
	private String m_orderBy = "favorite";
	private String m_city = BBConfigue.CURRENT_CITY;
	private String m_district = "";

	private ProgressDialog m_progDiag;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_activity = getActivity();
		m_progDiag = new ProgressDialog(m_activity);
		m_queue = Volley.newRequestQueue(m_activity);
		m_listItems = new ArrayList<Map<String, Object>>();
		initHandler();

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

		m_citiesHandler = new Handler(m_activity.getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case HttpUtil.SUCCESS_CODE:
					JSONObject response = (JSONObject) msg.obj;
					try {
						updateCitiesSpinner(response);
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

	private void beginDataRequest() {

		// HttpUtil.JsonGetRequest(BBConfigue.SERVER_HTTP
		// + "/stores/list?order_by=" + "favorite", m_handler, m_queue);
		//
		HttpUtil.JsonGetRequest(BBConfigue.SERVER_HTTP
				+ "/locations/regions?id=440000", m_citiesHandler, m_queue);

		HttpUtil.JsonGetRequest(BBConfigue.SERVER_HTTP
				+ "/locations/regions?id=440100", m_districtHandler, m_queue);

		Log.v(LOG_TAG, "beginDataRequest");
	}

	private void updataDataFromServer(JSONObject jsonObject)
			throws JSONException {
		Log.v(LOG_TAG, "m_handler updateDataFromServer");
		int retCode = jsonObject.getInt("ret_code");
		switch (retCode) {
		case 0:
			m_listItems.clear(); // TODO
			m_adapter.notifyDataSetChanged();
			JSONArray jsonArray = jsonObject.getJSONArray("stores");
			if (jsonArray.length() == 0) {
				Toast.makeText(m_activity, "没有商家！", Toast.LENGTH_SHORT).show();
				return;
			}
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

	private void updateCitiesSpinner(JSONObject response) throws JSONException {
		JSONArray jsonArray = response.getJSONArray("result").getJSONArray(0);
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject city = jsonArray.getJSONObject(i);
			int id = city.getInt("id");
			String fullname = city.getString("fullname");
			String name = city.getString("name");
			if (name.equals(BBConfigue.CURRENT_CITY)) {
				HttpUtil.JsonGetRequest(BBConfigue.SERVER_HTTP
						+ "/locations/regions?id=" + id, m_districtHandler,
						m_queue);
			}

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("city", fullname);
			map.put("city_id", id);
			m_citiesItems.add(map);
			m_citiesSimpleAdapter.notifyDataSetChanged();
		}
	}

	private void updateDistrictSpinner(JSONObject response)
			throws JSONException {
		m_districtsItems.clear();

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

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.bb_fragment_specfic_buy,
				container, false);

		m_searchView = (SearchView) view.findViewById(R.id.sv_store);
		m_citySpinner = (Spinner) view.findViewById(R.id.sp_city);
		m_districtSpinner = (Spinner) view.findViewById(R.id.sp_district);
		m_smartOrderSpinner = (Spinner) view.findViewById(R.id.sp_smart_order);
		m_listView = (ListView) view.findViewById(R.id.lv_stores);

		initSearchView();
		initSpinners();
		initListView();

		beginDataRequest();
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
		m_citiesItems = new ArrayList<Map<String, Object>>();
		m_districtsItems = new ArrayList<Map<String, Object>>();

		m_citiesSimpleAdapter = new SimpleAdapter(m_activity, m_citiesItems,
				R.layout.bb_item_spinner, new String[] { "city" },
				new int[] { R.id.tv_text });

		m_districtsSimpleAdapter = new SimpleAdapter(m_activity,
				m_districtsItems, R.layout.bb_item_spinner,
				new String[] { "district" }, new int[] { R.id.tv_text });

		m_citySpinner.setAdapter(m_citiesSimpleAdapter);
		m_districtSpinner.setAdapter(m_districtsSimpleAdapter);

		m_citySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO
				Log.v(LOG_TAG, "city selected: "
						+ m_citiesItems.get(position).get("city").toString());
				int city_id = (Integer) m_citiesItems.get(position).get(
						"city_id");
				HttpUtil.JsonGetRequest(BBConfigue.SERVER_HTTP
						+ "/locations/regions?id=" + city_id,
						m_districtHandler, m_queue);
				Log.v("Halfish", "Halfish id " + city_id);

				m_city = m_citiesItems.get(position).get("city").toString();
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
						String url = BBConfigue.SERVER_HTTP
								+ "/stores/list?order_by=" + m_orderBy
								+ "&city=" + m_city + "&district=" + m_district;
						HttpUtil.JsonGetRequest(url, m_handler, m_queue);
						Log.v(LOG_TAG, "district url is " + url);
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
						String url = BBConfigue.SERVER_HTTP
								+ "/stores/list?order_by=" + m_orderBy
								+ "&city=" + m_city + "&district=" + m_district;
						HttpUtil.JsonGetRequest(url, m_handler, m_queue);
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
								StoreInfoActivity.class);
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

			ImageLoader imageLoader = new ImageLoader(m_queue,
					new BitmapCache());
			viewHolder.storeImg.setImageUrl(BBConfigue.SERVER_HTTP + storeImg,
					imageLoader);

			// viewHolder.storeImg.setImageDrawable(storeImg);
			viewHolder.storeNameTV.setText(storeName);
			viewHolder.likeNumberTV.setText(likeNumber);
			viewHolder.distanceTV.setText(distance);

			return convertView;
		}

	}
}
