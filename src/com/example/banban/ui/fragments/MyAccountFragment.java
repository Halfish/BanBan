package com.example.banban.ui.fragments;

/*
 * @author: BruceZhang
 * @description: 我的账户 fragment
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.example.BBput.Compress_Save;
import com.example.BBput.PhotoM;
import com.example.BBput.SaveinSD;
import com.example.banban.R;
import com.example.banban.network.BitmapCache;
import com.example.banban.network.HttpUtil;
import com.example.banban.other.BBConfigue;
import com.example.banban.ui.BBSearchUserActivity;
import com.example.banban.ui.myaccount.ProjectFragment;
import com.example.banban.ui.myaccount.ShareProductFragment;
import com.example.banban.ui.myaccount.ShoppingCarFragment;
import com.example.banban.ui.otheraccount.CollectedProjectsActivity;
import com.example.banban.ui.otheraccount.CollectedStoresActivity;
import com.example.banban.ui.otheraccount.FollowingOtherPeopleActivity;
import com.example.banban.ui.otheraccount.MyFansActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAccountFragment extends BaseActionBarFragment {

	private static final String LOG_TAG = MyAccountFragment.class.getName();
	private ViewPager mPager;
	private ArrayList<Fragment> fragmentList;
	private ImageView image;
	private TextView view1, view2, view3;
	private int currIndex;// 当前页卡编号
	private int bmpW;// 横线图片宽度
	private int offset;// 图片移动的偏移量
	private static int RESULT_LOAD_IMAGE = 1;
	private static int RESULT_LOAD = 2;

	private Activity m_activity;
	private TextView m_donateTextView;
	private TextView m_balanceTextView;
	private TextView m_nickName;
	private ImageView m_userPic;
	private ImageView m_back_veiw;

	private Button m_storeButton;
	private Button m_projectButton;
	private Button m_followButton;
	private Button m_followingsButton;

	private Handler m_handler;
	private Handler m_handler2;
	private RequestQueue m_queue;
	private ImageLoader m_imageLoader;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		m_activity = getActivity();
		m_actionBar = m_activity.getActionBar();
		m_queue = Volley.newRequestQueue(m_activity);
		m_imageLoader = new ImageLoader(m_queue, new BitmapCache());
		initHandler();

		Log.v(LOG_TAG, "onCreate called");
	}

	@Override
	public void onResume() {
		Log.v(LOG_TAG, "onResume called");
		super.onResume();
	}

	@Override
	public void onStart() {
		Log.v(LOG_TAG, "onStart called");
		super.onStart();
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

		m_handler2 = new Handler(m_activity.getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case HttpUtil.SUCCESS_CODE:
					JSONObject response = (JSONObject) msg.obj;
					try {
						updateUserDetail(response);
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

	private void beginDataRequest() {

		HttpUtil.JsonGetRequest(BBConfigue.SERVER_HTTP + "/users/balance",
				m_handler, m_queue);
		HttpUtil.JsonGetRequest(BBConfigue.SERVER_HTTP + "/users/"
				+ BBConfigue.USER_ID, m_handler2, m_queue);
		Log.v(LOG_TAG, "user id: " + BBConfigue.USER_ID);
	}

	private void updateUserDetail(JSONObject jsonObject) throws JSONException {
		int retCode = jsonObject.getInt("ret_code");
		if (retCode == 1) {
			Log.v(LOG_TAG, "User does not exist!");
			return;
		}
		String image = jsonObject.getString("image");

		ImageListener listener = ImageLoader.getImageListener(m_userPic,
				R.drawable.default_head, R.drawable.default_head);
		m_imageLoader.get(BBConfigue.SERVER_HTTP + image, listener);

		String username = jsonObject.getString("username");
		m_nickName.setText(username);
	}

	private void updataDataFromServer(JSONObject jsonObject)
			throws JSONException {
		int retCode = jsonObject.getInt("ret_code");
		if (retCode == 1) {
			Log.v(LOG_TAG, "Database Exception!");
			return;
		}

		int balance = jsonObject.getInt("balance");
		int total_donate = jsonObject.getInt("total_donate");
		m_donateTextView.setText(total_donate + "");
		m_balanceTextView.setText(balance + "");
	}

	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.bb_fragment_my_account, container,
					false);

			m_donateTextView = (TextView) view
					.findViewById(R.id.tv_total_donate);
			m_balanceTextView = (TextView) view.findViewById(R.id.tv_balance);
			m_nickName = (TextView) view.findViewById(R.id.tv_nickname);
			m_userPic = (ImageView) view.findViewById(R.id.btn_nickname);

			m_storeButton = (Button) view.findViewById(R.id.btn_stores);
			m_projectButton = (Button) view.findViewById(R.id.btn_projects);
			m_followButton = (Button) view.findViewById(R.id.btn_following);
			m_followingsButton = (Button) view
					.findViewById(R.id.btn_followings);

			m_back_veiw = (ImageView) view.findViewById(R.id.img_beijing);
			initButtons();

			InitTextView(view);
			InitImage(view);
			InitViewPager(view);

			beginDataRequest();
		}

		// 缓存的view需要判断是否已经被加过parent，
		// 如果有parent需要从parent删除，要不然会发生这个view已经有parent的错误。
		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null) {
			parent.removeView(view);
		}

		return view;
	}

	private void initButtons() {
		m_storeButton.setOnClickListener(listener);
		m_projectButton.setOnClickListener(listener);
		m_followButton.setOnClickListener(listener);
		m_followingsButton.setOnClickListener(listener);
		m_userPic.setOnClickListener(listener);
		m_back_veiw.setOnLongClickListener(listener2);
	}

	private OnLongClickListener listener2 = new OnLongClickListener() {

		@Override
		public boolean onLongClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.img_beijing:
				Builder dialog = new AlertDialog.Builder(getActivity());
				dialog.setPositiveButton("切换背景",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(
										Intent.ACTION_PICK,
										android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
								startActivityForResult(intent,
										RESULT_LOAD_IMAGE);
							}

						});
				dialog.show();
				break;
			}
			return false;
		}
	};
	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_stores:
				// 切换到一个新的activity;
				Intent intent = new Intent(m_activity,
						CollectedStoresActivity.class);
				startActivity(intent);
				break;
			case R.id.btn_projects:
				Intent intent1 = new Intent(m_activity,
						CollectedProjectsActivity.class);
				startActivity(intent1);
				break;
			case R.id.btn_following:
				Intent intent2 = new Intent(m_activity,
						FollowingOtherPeopleActivity.class);
				startActivity(intent2);
				break;

			case R.id.btn_followings:
				Intent intent3 = new Intent(m_activity, MyFansActivity.class);
				startActivity(intent3);
				break;
			case R.id.btn_nickname:
				Builder dialog = new AlertDialog.Builder(getActivity());
				dialog.setPositiveButton("切换头像",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(
										Intent.ACTION_PICK,
										android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
								startActivityForResult(intent, RESULT_LOAD);
							}

						});
				dialog.show();
				break;
			default:
				break;
			}
		}
	};

	/*
	 * 头像上传
	 */
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v(LOG_TAG, "onResult called: requestCode = " + requestCode
				+ " resultCode = " + resultCode);
		if (requestCode == RESULT_LOAD_IMAGE && resultCode == -1
				&& data != null) {
			// 这个图片的URI
			Uri selectedImage = data.getData();
			// 获得图像的绝对路径
			String picturePath = PhotoM
					.getpicture(getActivity(), selectedImage);
			// 将图像进行压缩;
			Bitmap bitmap2 = Compress_Save.getSmallBitmap(picturePath,
					m_back_veiw.getWidth(), m_back_veiw.getHeight());
			//
			Log.v("haha", "dod");
			m_back_veiw.setImageBitmap(bitmap2);
			// 图片保存
			SaveinSD.savephoto(bitmap2, "user_beijing.png");
			// 将图片上传
		} else if (requestCode == RESULT_LOAD) {
			// 这个图片的URI
			Uri selectedImage = data.getData();
			// 获得图像的绝对路径
			String picturePath = PhotoM
					.getpicture(getActivity(), selectedImage);
			// 将图像进行压缩;
			Bitmap bitmap2 = Compress_Save.getSmallBitmap(picturePath,
					m_userPic.getWidth(), m_userPic.getHeight());
			m_userPic.setImageBitmap(bitmap2);
			Map<String, String> map = new HashMap<String, String>();
			map.put("image", PhotoM.imgToBase64(bitmap2));
			// HttpUtil.NormalPostRequest(map, uri2, handler,
			// Merchant_main.BBQueue);
		}
		//super.onActivityResult(requestCode, resultCode, data);
	}

	/*
	 * 初始化标签名0
	 */
	public void InitTextView(View view) {
		view1 = (TextView) view.findViewById(R.id.tv_guid1);
		view2 = (TextView) view.findViewById(R.id.tv_guid2);
		view3 = (TextView) view.findViewById(R.id.tv_guid3);

		view1.setOnClickListener(new txListener(0));
		view2.setOnClickListener(new txListener(1));
		view3.setOnClickListener(new txListener(2));
	}

	public class txListener implements View.OnClickListener {
		private int index = 0;

		public txListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(index);
		}
	}

	/*
	 * 初始化图片的位移像素
	 */
	public void InitImage(View view) {
		image = (ImageView) view.findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(),
				R.drawable.bb_cursor).getWidth();
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;
		offset = (screenW / 3 - bmpW) / 2;

		// ImageView设置平移，使下划线平移到初始位置（平移一个offset）
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		image.setImageMatrix(matrix);
	}

	/*
	 * 初始化ViewPager
	 */
	public void InitViewPager(View view) {
		mPager = (ViewPager) view.findViewById(R.id.viewpager);
		fragmentList = new ArrayList<Fragment>();
		fragmentList.add(new ProjectFragment());
		fragmentList.add(new ShoppingCarFragment());
		fragmentList.add(new ShareProductFragment());

		// 给ViewPager设置适配器
		mPager.setAdapter(new MyFragmentPagerAdapter(getChildFragmentManager(),
				fragmentList));
		mPager.setCurrentItem(0);// 设置当前显示标签页为第一页
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());// 页面变化时的监听器
		mPager.setOffscreenPageLimit(4);
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {
		private int one = offset * 2 + bmpW;// 两个相邻页面的偏移量

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageSelected(int arg0) {
			Animation animation = new TranslateAnimation(currIndex * one, arg0
					* one, 0, 0);// 平移动画
			currIndex = arg0;
			animation.setFillAfter(true);// 动画终止时停留在最后一帧，不然会回到没有执行前的状态
			animation.setDuration(200);// 动画持续时间0.2秒
			image.startAnimation(animation);// 是用ImageView来显示动画的
		}
	}

	private class MyFragmentPagerAdapter extends FragmentPagerAdapter {
		ArrayList<Fragment> list;

		public MyFragmentPagerAdapter(FragmentManager fm,
				ArrayList<Fragment> list) {
			super(fm);
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Fragment getItem(int arg0) {
			return list.get(arg0);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.bb_menu_fragment_my_account, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_search:
			Intent intent = new Intent(getActivity(),
					BBSearchUserActivity.class);
			startActivity(intent);
			Log.v(LOG_TAG, "menu search selected");
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
