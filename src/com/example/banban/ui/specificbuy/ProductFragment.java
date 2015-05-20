package com.example.banban.ui.specificbuy;

/*
 * @author: BruceZhang
 * @description: 商家页面 第一个Tab选项 列出该商家的所有待抢商品
 */


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.banban.R;
import com.example.banban.network.HttpUtil;
import com.example.banban.other.BBConfigue;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProductFragment extends Fragment {

	protected static final String LOG_TAG = "ProductFragment";
	private Activity m_activity;
	private GridView m_gridView;
	private StoreInfoAdapter m_adapter;
	private List<Map<String, Object>> m_listItems;
	private Map<String, Object> item;
	private Handler m_handler;
	private RequestQueue m_queue;
	private int m_storeId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_activity = getActivity();
		m_storeId = m_activity.getIntent().getIntExtra(("store_id"), 1); //TODO
		initHandler();
	}
	
	private void initHandler() {
		m_handler = new Handler(getActivity().getMainLooper()) {
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

	private void updataDataFromServer(JSONObject response) throws JSONException {
		int ret_code = response.getInt("ret_code");
		if(ret_code == 1) {
			Toast.makeText(m_activity, "Store not exist", Toast.LENGTH_SHORT).show();
			return;
		}
		
		// else ret_code == 0
		m_listItems = new ArrayList<Map<String, Object>>();
		JSONArray jsonArray = response.getJSONArray("products");
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			addItem(jsonObject);
		}
	}
	
	private void addItem(JSONObject jsonObject) throws JSONException {
		
		//String image = jsonObject.getString("image");
		int price = jsonObject.getInt("price");
		//int product_id = jsonObject.getInt("product_id");
		//int original_price = jsonObject.getInt("original_price");
		String name = jsonObject.getString("name");
		int favorites = jsonObject.getInt("favorites");
		int amount_spec = jsonObject.getInt("amount_spec");
		
		item = new HashMap<String, Object>();
		item.put("product_img",
				getResources().getDrawable(R.drawable.bb_store_zhao_big));
		item.put("product_name", name);
		item.put("like_number", favorites + "");
		item.put("price", price + "");
		item.put("remains", amount_spec + "");
		m_listItems.add(item);

	}

	private void beginDataRequest() {
		m_queue = Volley.newRequestQueue(getActivity());
		HttpUtil.JsonGetRequest(BBConfigue.SERVER_HTTP + "/stores/products/"
				+ m_storeId, m_handler, m_queue);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.bb_fragment_specificbuy_product, container, false);

		m_gridView = (GridView) rootView.findViewById(R.id.gv_product);
		m_adapter = new StoreInfoAdapter();
		m_gridView.setAdapter(m_adapter);
		m_gridView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Intent intent = new Intent(getActivity(), ProductActivity.class);
						startActivity(intent);
					}
				});
		
		beginDataRequest();
		
		return rootView;
	}

	private static class ViewHolder {
		ImageView productImg;
		TextView productNameTV;
		TextView likeNumberTV;
		TextView priceTV;
		TextView remainsTV;
	}

	private class StoreInfoAdapter extends BaseAdapter {

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
						R.layout.bb_cell_product, parent, false);
				/*
				 * initialize viewHolder;
				 */
				viewHolder = new ViewHolder();
				viewHolder.productImg = (ImageView) convertView
						.findViewById(R.id.img_product);
				viewHolder.productNameTV = (TextView) convertView
						.findViewById(R.id.tv_product_name);
				viewHolder.likeNumberTV = (TextView) convertView
						.findViewById(R.id.tv_like_number);
				viewHolder.priceTV = (TextView) convertView
						.findViewById(R.id.tv_product_price);
				viewHolder.remainsTV = (TextView) convertView
						.findViewById(R.id.tv_remains);

				convertView.setTag(viewHolder);
			} else {
				/*
				 * with viewHolder, we just avoid callingfindViewById every time
				 */
				viewHolder = (ViewHolder) convertView.getTag();
			}

			Drawable storeImg = (Drawable) m_listItems.get(position).get(
					"product_img");
			String storeName = (String) m_listItems.get(position).get(
					"product_name");
			String likeNumber = (String) m_listItems.get(position).get(
					"like_number");
			String distance = (String) m_listItems.get(position)
					.get("price");
			String remains = (String) m_listItems.get(position)
					.get("remains");

			viewHolder.productImg.setImageDrawable(storeImg);
			viewHolder.productNameTV.setText(storeName);
			viewHolder.likeNumberTV.setText(likeNumber);
			viewHolder.priceTV.setText(distance);
			viewHolder.remainsTV.setText(remains);

			return convertView;
		}

	}

}
