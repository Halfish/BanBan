package com.example.banban.ui.specificbuy;

/*
 * @author: BruceZhang
 * @description: 给商家写评价界面
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.example.banban.R;
import com.example.banban.network.HttpUtil;
import com.example.banban.other.BBApplication;
import com.example.banban.other.BBConfigue;
import com.example.banban.ui.BaseActionBarActivity;
import com.luminous.pick.Action;
import com.luminous.pick.CustomGallery;
import com.luminous.pick.GalleryAdapter;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RatingBar;
import android.widget.Toast;

public class WritingReviewActivity extends BaseActionBarActivity {

	private static final String LOG_TAG = null;
	private EditText m_editText;
	private RatingBar m_ratingBar;
	private Button m_uploadButton;
	private Button m_publishButton;
	private RequestQueue m_queue;
	private Handler m_handler;
	private ProgressDialog m_progDialog;

	private int m_storeId;
	private double m_rating;

	/*
	 * pick
	 */
	private GridView gridGallery;
	private GalleryAdapter adapter;
	private ImageLoader imageLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bb_activity_writing_review);

		m_storeId = getIntent().getIntExtra("store_id", -1);
		m_queue = BBApplication.getQueue();
		initWidgets();
		initHandler();

		initImageLoader();
		init();
	}

	// START PCIK
	private void initImageLoader() {
		@SuppressWarnings("deprecation")
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheOnDisc().imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
				this).defaultDisplayImageOptions(defaultOptions).memoryCache(
				new WeakMemoryCache());

		ImageLoaderConfiguration config = builder.build();
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(config);
	}

	private void init() {
		gridGallery = (GridView) findViewById(R.id.gridGallery);
		gridGallery.setFastScrollEnabled(true);
		adapter = new GalleryAdapter(getApplicationContext(), imageLoader);
		adapter.setMultiplePick(false);
		gridGallery.setAdapter(adapter);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
			String[] all_path = data.getStringArrayExtra("all_path");

			ArrayList<CustomGallery> dataT = new ArrayList<CustomGallery>();

			for (int i = 0; i < all_path.length && i < 9; ++i) {
				String string = all_path[i];
				CustomGallery item = new CustomGallery();
				item.sdcardPath = string;
				dataT.add(item);
			}
			adapter.addAll(dataT);
		}
	}

	// END PICK

	private void initWidgets() {
		m_editText = (EditText) findViewById(R.id.et_review);
		m_ratingBar = (RatingBar) findViewById(R.id.ratingBar_review);
		m_uploadButton = (Button) findViewById(R.id.btn_post_photo);
		m_uploadButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
				startActivityForResult(i, 200);
			}
		});
		m_progDialog = new ProgressDialog(this);
		m_publishButton = (Button) findViewById(R.id.btn_publish);
		m_publishButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				m_rating = m_ratingBar.getRating();
				String review = m_editText.getText().toString();
				if (!review.equals("")) {
					m_progDialog.setMessage("正在发布评论~");
					m_progDialog.show();
					beginDataRequest(review);
				}
			}
		});
	}

	private void beginDataRequest(String review) {

		Map<String, String> map = new HashMap<String, String>();
		map.put("store_id", m_storeId + "");
		map.put("rating", m_rating + "");
		map.put("content", review);
		map.put("image", ""); // TODO

		HttpUtil.NormalPostRequest(map, BBConfigue.SERVER_HTTP
				+ "/stores/reviews/add", m_handler, m_queue);
	}

	private void initHandler() {
		m_handler = new Handler(getMainLooper()) {
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
		int retCode = response.getInt("ret_code");
		switch (retCode) {
		case 0:
			m_progDialog.dismiss();
			finish();
			break;

		case 1:
			Toast.makeText(this, "Store Not Exist!", Toast.LENGTH_SHORT).show();

			break;

		default:
			break;
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
