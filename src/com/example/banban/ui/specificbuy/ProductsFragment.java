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
import android.widget.Toast;

public class ProductsFragment extends Fragment {

	protected static final String LOG_TAG = "ProductFragment";
	private Activity m_activity;
	private GridView m_gridView;
	private StoreInfoAdapter m_adapter;
	private List<Map<String, Object>> m_listItems;
	private Map<String, Object> item;
	private Handler m_handler;
	private RequestQueue m_queue;
	private ImageLoader m_imageLoader;
	private int m_storeId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_activity = getActivity();
		m_queue = BBApplication.getQueue();
		m_imageLoader = BBApplication.getImageLoader();
		m_storeId = m_activity.getIntent().getIntExtra(("store_id"), 1);
		initHandler();
	}

	@Override
	public void onResume() {
		beginDataRequest();
		super.onResume();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// 可见时刷新数据
		if (isVisibleToUser && isAdded()) {
			beginDataRequest();
		}
		super.setUserVisibleHint(isVisibleToUser);
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
		if (ret_code == 1) {
			Toast.makeText(m_activity, "Store not exist", Toast.LENGTH_SHORT)
					.show();
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

		String image = jsonObject.getString("image");
		int price = jsonObject.getInt("price");
		int product_id = jsonObject.getInt("product_id");
		String name = jsonObject.getString("name");
		int favorites = jsonObject.getInt("favorites");
		int amount_spec = jsonObject.getInt("amount_spec");

		item = new HashMap<String, Object>();
		item.put("product_id", product_id);
		item.put("product_img", image);
		item.put("product_name", name);
		item.put("like_number", favorites + "");
		item.put("price", "价格：" + price + "元");
		item.put("remains", "剩余" + amount_spec + "个");
		m_listItems.add(item);

		m_adapter.notifyDataSetChanged();

	}

	private void beginDataRequest() {

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
						int productId = (Integer) m_listItems.get(position)
								.get("product_id");
						Intent intent = new Intent(getActivity(),
								ProductActivity.class);
						intent.putExtra("product_id", productId);
						startActivity(intent);
					}
				});

		beginDataRequest();

		return rootView;
	}

	private static class ViewHolder {
		NetworkImageView productImg;
		TextView productNameTV;
		TextView likeNumberTV;
		TextView priceTV;
		TextView remainsTV;
		ImageView grayView;
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
				viewHolder.productImg = (NetworkImageView) convertView
						.findViewById(R.id.img_product);
				viewHolder.productNameTV = (TextView) convertView
						.findViewById(R.id.tv_product_name);
				viewHolder.likeNumberTV = (TextView) convertView
						.findViewById(R.id.tv_like_number);
				viewHolder.priceTV = (TextView) convertView
						.findViewById(R.id.tv_product_price);
				viewHolder.remainsTV = (TextView) convertView
						.findViewById(R.id.tv_remains);
				viewHolder.grayView = (ImageView) convertView
						.findViewById(R.id.img_gray);

				convertView.setTag(viewHolder);
			} else {
				/*
				 * with viewHolder, we just avoid callingfindViewById every time
				 */
				viewHolder = (ViewHolder) convertView.getTag();
			}

			String productImg = (String) m_listItems.get(position).get(
					"product_img");
			String storeName = (String) m_listItems.get(position).get(
					"product_name");
			String likeNumber = (String) m_listItems.get(position).get(
					"like_number");
			String distance = (String) m_listItems.get(position).get("price");
			String remains = (String) m_listItems.get(position).get("remains");

			viewHolder.productImg.setImageUrl(BBConfigue.SERVER_HTTP
					+ productImg, m_imageLoader);

			// viewHolder.productImg.setImageDrawable(storeImg);
			viewHolder.productNameTV.setText(storeName);
			viewHolder.likeNumberTV.setText(likeNumber);
			viewHolder.priceTV.setText(distance);
			viewHolder.remainsTV.setText(remains);
			
			if (remains.equals("剩余0个")) {
				viewHolder.grayView.setVisibility(View.VISIBLE);
			}

			return convertView;
		}

	}

}
