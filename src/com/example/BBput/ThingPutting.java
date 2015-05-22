package com.example.BBput;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import com.android.volley.toolbox.Volley;
import com.example.BanBanBusiness.Merchant_main;
import com.example.BusinessHttp.HttpUtil;
import com.example.banban.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ThingPutting extends Activity {

	private ImageView btButton1;
	private EditText editText1;
	private EditText editText2;
	private EditText editText3;
	private EditText editText4;
	private EditText editText5;
	private EditText editText8;
	private AlertDialog mDialog;
	private SelectPicPopupWindow menuWindow;
	private static int RESULT_LOAD_IMAGE = 1;
	private static int RESULT_Capture_IMAGE = 2;
	private static String uri = "http://omegaga.net/banban/products/add";
	private TextView itTextView;
	private View view1;
	private Button button1;
	private String picturePath;
	private Bitmap smallmap;
	private PuttingHandler handler = new PuttingHandler();

	protected void onCreate(Bundle savedInstanceState) {
		Volley.newRequestQueue(ThingPutting.this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.thingput);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setCustomView(R.layout.title2);
		getActionBar().setDisplayShowCustomEnabled(true);

		btButton1 = (ImageView) findViewById(R.id.imageButton1);
		button1 = (Button) findViewById(R.id.button1);
		view1 = (View) findViewById(R.id.r1);
		itTextView = (TextView) findViewById(R.id.textView8);
		editText1 = (EditText) findViewById(R.id.editText1);
		editText2 = (EditText) findViewById(R.id.editText2);
		editText3 = (EditText) findViewById(R.id.editText3);
		editText4 = (EditText) findViewById(R.id.editText4);
		editText5 = (EditText) findViewById(R.id.editText5);
		editText8 = (EditText) findViewById(R.id.editText8);
		// 对名字进行监听

		view1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Builder dialog = new AlertDialog.Builder(ThingPutting.this);
				LayoutInflater factory = LayoutInflater.from(ThingPutting.this);
				final View textEntryView = factory.inflate(
						R.layout.dialog_item, null);
				dialog.setTitle("请输入宝贝名称：");
				dialog.setView(textEntryView);
				dialog.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								EditText nameEditText = (EditText) textEntryView
										.findViewById(R.id.editText1);
								itTextView.setText(nameEditText.getText()
										.toString());
							}
						});
				dialog.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				dialog.show();
			}

		});

		btButton1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 实例化SelectPicPopupWindow
				// 去除软键盘
				View view = getWindow().peekDecorView();
				if (view != null && v.getWindowToken() != null) {
					Log.v("haha", "" + view);
					InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
					getSystemService(INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
				}
				if (view == null)
					Log.v("haha", "haha");
				menuWindow = new SelectPicPopupWindow(ThingPutting.this,
						itemsOnClick);
				// 显示窗口
				menuWindow.showAtLocation(
						ThingPutting.this.findViewById(R.id.tthingp),
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			}
		});
		button1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 发送数据,同时切换activity
				Map<String, String> map = new HashMap<String, String>();
				map.put("name", itTextView.getText().toString());
				map.put("original_price", editText3.getText().toString());
				map.put("price", editText4.getText().toString());
				map.put("donate", editText8.getText().toString());
				map.put("amount_random", editText1.getText().toString());
				map.put("amount_spec", editText2.getText().toString());
				map.put("description", editText5.getText().toString());
				if (smallmap != null)
					map.put("image", PhotoM.imgToBase64(smallmap));
				Log.v("haha", map + "");
				HttpUtil.NormalPostRequest(map, uri, handler,
						Merchant_main.BBQueue);
				showRoundProcessDialog(ThingPutting.this,
						R.layout.loading_process);

			}
		});

	}

	public class PuttingHandler extends Handler {
		public void dispatchMessage(Message msg) {
			switch (msg.what) {
			case 0x123:
				JSONObject jsonObj = (JSONObject) msg.obj;
				int code = jsonObj.optInt("ret_code");
				switch (code) {
				case 0:
					Toast.makeText(ThingPutting.this, "发布成功", Toast.LENGTH_LONG)
							.show();
					mDialog.dismiss();
					Intent intent = new Intent(ThingPutting.this,
							Merchant_main.class);
					startActivity(intent);
					finish();
					break;
				default:
					Toast.makeText(ThingPutting.this, "发布失败", Toast.LENGTH_LONG)
							.show();
					mDialog.dismiss();
					break;
				}
				break;
			case 0x124:
				Toast.makeText(ThingPutting.this, "服务器无响应", Toast.LENGTH_LONG)
						.show();
				mDialog.dismiss();
				break;
			}
		}

	}

	private OnClickListener itemsOnClick = new OnClickListener() {

		public void onClick(View v) {
			menuWindow.dismiss();
			switch (v.getId()) {
			case R.id.btn_take_photo:
				Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				// 同时设置图片存储的路径：
				Uri imageUri = Uri.fromFile(new File(Environment
						.getExternalStorageDirectory(), "temp.png"));
				intent1.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				startActivityForResult(intent1, RESULT_Capture_IMAGE);
				break;
			case R.id.btn_pick_photo:
				Intent intent2 = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(intent2, RESULT_LOAD_IMAGE);
				break;
			default:
				break;
			}

		}

	};

	// 找到图片
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
				&& data != null) {
			// 这个图片的URI
			Uri selectedImage = data.getData();
			picturePath = PhotoM.getpicture(this, selectedImage);
			// 对图片进行压缩
			smallmap = Compress_Save.getSmallBitmap(picturePath,
					btButton1.getHeight(), btButton1.getWidth());
			// 设置该ImageView
			btButton1.setImageBitmap(smallmap);

		} else if (requestCode == RESULT_Capture_IMAGE
				&& resultCode == RESULT_OK && data != null) {
			// 对图片进行压缩
			smallmap = Compress_Save.getSmallBitmap(
					Environment.getExternalStorageDirectory() + "/temp.png",
					btButton1.getHeight(), btButton1.getWidth());
			btButton1.setImageBitmap(smallmap);
		}
	}

	public void showRoundProcessDialog(Context mContext, int layout) {
		OnKeyListener keyListener = new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_HOME
						|| keyCode == KeyEvent.KEYCODE_SEARCH) {
					return true;
				}
				return false;
			}
		};

		mDialog = new AlertDialog.Builder(mContext).create();
		mDialog.setOnKeyListener(keyListener);
		mDialog.show();
		// 注意此处要放在show之后 否则会报异常
		mDialog.setContentView(layout);
	}

	@Override
	public boolean onTouchEvent(android.view.MotionEvent event) {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		return imm.hideSoftInputFromWindow(this.getCurrentFocus()
				.getWindowToken(), 0);
	}
}
