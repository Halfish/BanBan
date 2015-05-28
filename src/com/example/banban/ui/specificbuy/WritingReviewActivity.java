package com.example.banban.ui.specificbuy;

/*
 * @author: BruceZhang
 * @description: 给商家写评价界面
 */

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

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bb_activity_writing_review);

		m_storeId = getIntent().getIntExtra("store_id", -1);
		m_queue = BBApplication.getQueue();
		initWidgets();
		initHandler();
	}

	private void initWidgets() {
		m_editText = (EditText) findViewById(R.id.et_review);
		m_ratingBar = (RatingBar) findViewById(R.id.ratingBar_review);
		m_uploadButton = (Button) findViewById(R.id.btn_post_photo);
		m_uploadButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

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
