package com.example.banban.ui.myaccount;

/*
 * @author: BruceZhang
 * @description: 我的账户 第二个Tab选项卡 购物车
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

import android.app.Activity;
import android.content.Intent;
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

public class ShoppingCarFragment extends Fragment {

	private static final String LOG_TAG = ShoppingCarFragment.class.getName();
	private Activity m_activity;
	private GridView m_gridView;
	private StoreInfoAdapter m_adapter;
	private List<Map<String, Object>> m_listItems;
	private Map<String, Object> item;

	private Handler m_handler;
	private RequestQueue m_queue;
	private ImageLoader m_imageLoader;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_activity = getActivity();
		m_listItems = new ArrayList<Map<String, Object>>();
		m_queue = BBApplication.getQueue();
		m_imageLoader = BBApplication.getImageLoader();
		initHandler();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// 可见时刷新数据
		if (isVisibleToUser) {
			beginDataRequest();
		}
		super.setUserVisibleHint(isVisibleToUser);
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

	@Override
	public void onResume() {
		beginDataRequest();
		super.onResume();
	}

	private void beginDataRequest() {
		HttpUtil.JsonGetRequest(BBConfigue.SERVER_HTTP
				+ "/users/purchases/cart", m_handler, m_queue);
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
		m_adapter.notifyDataSetChanged();
		JSONArray jsonArray = jsonObject.getJSONArray("purchases");
		if (jsonArray == null) {
			return;
		}
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject object = jsonArray.getJSONObject(i);
			addItem(object);
		}
	}

	private void addItem(JSONObject object) throws JSONException {

		String purchase_code = object.getString("purchase_code");
		int product_id = object.getInt("product_id");
		String product_name = object.getString("product_name");

		int price = object.getInt("price");
		String image = object.getString("image");
		String amount_spec = object.getString("amount_spec");
		int favorites = object.getInt("favorites");
		String expire_at = object.getString("expire_at");
		boolean purchased = (object.getInt("purchased") == 0) ? false : true;

		item = new HashMap<String, Object>();
		item.put("image", image);
		item.put("purchase_code", purchase_code);
		item.put("product_id", product_id);
		item.put("product_name", product_name);
		item.put("like_number", favorites + "");
		item.put("price", "价格：" + price + "");
		item.put("remains", "还剩余" + amount_spec + "个");
		item.put("expire_at", "消费期限：\n" + expire_at);
		item.put("purchased", purchased);
		m_listItems.add(item);

		m_adapter.notifyDataSetChanged();
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
						int product_id = (Integer) m_listItems.get(position)
								.get("product_id");
						String purchase_code = (String) m_listItems.get(
								position).get("purchase_code");
						Intent intent = new Intent(getActivity(),
								ShoppingCarActivity.class);
						intent.putExtra("product_id", product_id);
						intent.putExtra("purchase_code", purchase_code);
						startActivity(intent);
					}
				});

		return rootView;
	}

	private static class ViewHolder {
		NetworkImageView productImg;
		TextView productNameTV;
		TextView likeNumberTV;
		TextView priceTV;
		TextView expireTV;
		ImageView grayImageView;
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
						R.layout.bb_cell_product_show, parent, false);
				/*
				 * initialize viewHolder;
				 */
				viewHolder = new ViewHolder();
				viewHolder.productImg = (NetworkImageView) convertView
						.findViewById(R.id.img_product);
				viewHolder.productNameTV = (TextView) convertView
						.findViewById(R.id.tv_product_name);
				viewHolder.likeNumberTV = (TextView) convertView
						.findViewById(R.id.tv_like_number);
				viewHolder.priceTV = (TextView) convertView
						.findViewById(R.id.tv_product_price);
				viewHolder.expireTV = (TextView) convertView
						.findViewById(R.id.tv_expire);
				viewHolder.grayImageView = (ImageView) convertView
						.findViewById(R.id.img_gray);

				convertView.setTag(viewHolder);
			} else {
				/*
				 * with viewHolder, we just avoid callingfindViewById every time
				 */
				viewHolder = (ViewHolder) convertView.getTag();
			}

			String storeName = (String) m_listItems.get(position).get(
					"product_name");
			String likeNumber = (String) m_listItems.get(position).get(
					"like_number");
			String distance = (String) m_listItems.get(position).get("price");
			String image = (String) m_listItems.get(position).get("image");
			String expire_at = (String) m_listItems.get(position).get(
					"expire_at");
			boolean purchased = (Boolean) m_listItems.get(position).get(
					"purchased");

			// viewHolder.productImg.setImageDrawable(storeImg);
			viewHolder.productNameTV.setText(storeName);
			viewHolder.likeNumberTV.setText(likeNumber);
			viewHolder.priceTV.setText(distance);
			viewHolder.expireTV.setText(expire_at);
			
			// 设置灰色蒙层
			if (purchased) {
				viewHolder.grayImageView.setVisibility(View.VISIBLE);
				viewHolder.expireTV.setVisibility(View.INVISIBLE);
			} else {
				viewHolder.grayImageView.setVisibility(View.INVISIBLE);
				viewHolder.expireTV.setVisibility(View.VISIBLE);
			}

			viewHolder.productImg.setImageUrl(BBConfigue.SERVER_HTTP + image,
					m_imageLoader);

			return convertView;
		}

	}

}
