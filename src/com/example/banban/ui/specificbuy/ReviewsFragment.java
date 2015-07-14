package com.example.banban.ui.specificbuy;

/*
 * @author: BruceZhang
 * @description: 商家页面 第三个Tab选项 对该商家的评价，
 * 可以跳到评价界面
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
import com.halfish.banban.R;
import com.example.banban.network.HttpUtil;
import com.example.banban.other.BBApplication;
import com.example.banban.other.BBConfigue;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class ReviewsFragment extends Fragment implements OnClickListener {

	protected static final String LOG_TAG = ReviewsFragment.class.getName();
	private ListView m_listView;
	private ReviewsAdapter m_adapter;
	private List<Map<String, Object>> m_listItems;
	private Map<String, Object> m_item;
	private Button m_reviewButton;

	private Handler m_handler;
	private Activity m_activity;
	private RequestQueue m_queue;
	private ImageLoader m_imageLoader;
	private int m_storeId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_activity = getActivity();
		m_storeId = m_activity.getIntent().getIntExtra("store_id", -1);
		m_listItems = new ArrayList<Map<String, Object>>();
		m_queue = BBApplication.getQueue();
		m_imageLoader = BBApplication.getImageLoader();

		initHandler();
	}

	@Override
	public void onResume() {
		Log.v(LOG_TAG, "onResume called");
		beginDataRequest(); // update data when resume from
							// WritingReviewActivity
		super.onResume();
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
		m_listItems.clear();

		// int avg_rating = response.getInt("avg_ration");
		JSONArray jsonArray = response.getJSONArray("reviews");
		addItem(jsonArray);
	}

	private void addItem(JSONArray jsonArray) throws JSONException {
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			int user_id = jsonObject.getInt("user_id");
			String username = jsonObject.getString("username");
			int rating = jsonObject.getInt("rating");
			String content = jsonObject.getString("content");
			JSONArray image = jsonObject.getJSONArray("image");
			String time = jsonObject.getString("time");

			m_item = new HashMap<String, Object>();
			m_item.put("user_id", user_id + "");
			m_item.put("username", username);
			m_item.put("rating", rating + "");
			m_item.put("content", content);
			m_item.put("time", time);

			ArrayList<String> imageUrls = new ArrayList<String>();
			for (int j = 0; j < image.length(); j++) {
				imageUrls.add(image.getString(j));
			}
			m_item.put("image_url", imageUrls);

			m_listItems.add(m_item);
			m_adapter.notifyDataSetChanged();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.bb_fragment_specificbuy_reviews, container, false);
		m_reviewButton = (Button) rootView.findViewById(R.id.btn_write_reviews);
		m_reviewButton.setOnClickListener(this);

		m_listView = (ListView) rootView.findViewById(R.id.lv_reviews);
		m_adapter = new ReviewsAdapter();
		m_listView.setAdapter(m_adapter);

		beginDataRequest(); // get date when created
		return rootView;
	}

	private void beginDataRequest() {

		HttpUtil.JsonGetRequest(BBConfigue.SERVER_HTTP + "/stores/reviews/"
				+ m_storeId, m_handler, m_queue);
	}

	@Override
	public void onClick(View v) {
		if (BBConfigue.IS_VISITOR) {
			ProgressDialog dialog = new ProgressDialog(m_activity);
			dialog.setMessage("游客用户无权限，请登录或注册");
			dialog.show();
			return;
		}
		Intent intent = new Intent(getActivity(), WritingReviewActivity.class);
		intent.putExtra("store_id", m_storeId);
		startActivity(intent);
	}

	private static class ViewHolder {
		TextView mUsername;
		RatingBar mRatingBar;
		TextView mDate;
		TextView mContent;
		NetworkImageView mFirstImg;
		NetworkImageView mSecondImg;
		Button mMoreImg;
	}

	private class ReviewsAdapter extends BaseAdapter {

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
						R.layout.bb_item_reviews, parent, false);
				/*
				 * initialize viewHolder;
				 */
				viewHolder = new ViewHolder();
				viewHolder.mUsername = (TextView) convertView
						.findViewById(R.id.tv_nickname);
				viewHolder.mRatingBar = (RatingBar) convertView
						.findViewById(R.id.ratingBar_review);
				viewHolder.mDate = (TextView) convertView
						.findViewById(R.id.tv_review_time);
				viewHolder.mContent = (TextView) convertView
						.findViewById(R.id.tv_reviews);
				viewHolder.mFirstImg = (NetworkImageView) convertView
						.findViewById(R.id.img_first);
				viewHolder.mSecondImg = (NetworkImageView) convertView
						.findViewById(R.id.img_second);
				viewHolder.mMoreImg = (Button) convertView
						.findViewById(R.id.img_more);

				convertView.setTag(viewHolder);
			} else {
				/*
				 * with viewHolder, we just avoid callingfindViewById every time
				 */
				viewHolder = (ViewHolder) convertView.getTag();
			}

			String username = (String) m_listItems.get(position)
					.get("username");
			String rating = (String) m_listItems.get(position).get("rating");
			String content = (String) m_listItems.get(position).get("content");
			String time = (String) m_listItems.get(position).get("time");

			@SuppressWarnings("unchecked")
			final ArrayList<String> imageUrl = (ArrayList<String>) m_listItems
					.get(position).get("image_url");

			Log.v(LOG_TAG,
					"image size is: " + imageUrl.size() + " position is:"
							+ position + " image_url is: "
							+ imageUrl.toString());

			if (imageUrl.size() > 0) {
				viewHolder.mFirstImg.setImageUrl(BBConfigue.SERVER_HTTP
						+ imageUrl.get(0), m_imageLoader);
				viewHolder.mFirstImg.setVisibility(View.VISIBLE);
			} else {
				viewHolder.mFirstImg.setVisibility(View.GONE);
			}

			if (imageUrl.size() > 1) {
				viewHolder.mSecondImg.setImageUrl(BBConfigue.SERVER_HTTP
						+ imageUrl.get(1), m_imageLoader);
				viewHolder.mSecondImg.setVisibility(View.VISIBLE);
			}else {
				viewHolder.mSecondImg.setVisibility(View.GONE);
			}

			// if the number of pictures is more than 3, then start
			// another activity to view these pictures
			if (imageUrl.size() > 2) {
				viewHolder.mMoreImg.setVisibility(View.VISIBLE);
				viewHolder.mMoreImg
						.setOnClickListener(new View.OnClickListener() {
							public void onClick(View v) {
								Intent intent = new Intent(m_activity,
										ImageViewerActivity.class);
								intent.putStringArrayListExtra("image_url",
										imageUrl);
								startActivity(intent);
							}
						});
			}else {
				viewHolder.mMoreImg.setVisibility(View.GONE);
			}

			viewHolder.mUsername.setText(username);
			viewHolder.mRatingBar.setRating(Float.parseFloat(rating));
			viewHolder.mContent.setText(content);
			viewHolder.mDate.setText(time);

			return convertView;
		}

	}

}
