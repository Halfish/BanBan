package com.example.banban.ui.specificbuy;

/*
 * @author: BruceZhang
 * @description: 特定抢的商家评论，查看更多的评论图片
 */

import java.util.ArrayList;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.banban.R;
import com.example.banban.other.BBApplication;
import com.example.banban.other.BBConfigue;
import com.example.banban.ui.BaseActionBarActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

public class ImageViewerActivity extends BaseActionBarActivity {

	private ArrayList<String> m_listItems;
	private GridView m_gridView;
	private ImageViewBaseAdapter m_adapter;
	private ImageLoader m_imageLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_viewer);

		m_listItems = getIntent().getStringArrayListExtra("image_url");
		m_imageLoader = BBApplication.getImageLoader();

		initWidgets();
	}

	private void initWidgets() {
		m_gridView = (GridView) findViewById(R.id.gv_images);
		m_adapter = new ImageViewBaseAdapter();
		m_gridView.setAdapter(m_adapter);
	}

	private static class ViewHolder {
		NetworkImageView image;
	}

	private class ImageViewBaseAdapter extends BaseAdapter {

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
				convertView = getLayoutInflater().inflate(
						R.layout.bb_cell_imageview, parent, false);
				/*
				 * initialize viewHolder;
				 */
				viewHolder = new ViewHolder();
				viewHolder.image = (NetworkImageView) convertView
						.findViewById(R.id.img_image);

				convertView.setTag(viewHolder);
			} else {
				/*
				 * with viewHolder, we just avoid callingfindViewById every time
				 */
				viewHolder = (ViewHolder) convertView.getTag();
			}

			String imageUrl = m_listItems.get(position);
			viewHolder.image.setImageUrl(BBConfigue.SERVER_HTTP + imageUrl, m_imageLoader);

			return convertView;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
