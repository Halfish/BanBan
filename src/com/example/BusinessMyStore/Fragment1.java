package com.example.BusinessMyStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.example.BBput.Compress_Save;
import com.example.BBput.PhotoM;
import com.example.BBput.SaveinSD;
import com.example.BanBanBusiness.Merchant_main;
import com.example.BanBanBusiness.localStore;
import com.example.BusinessHttp.BitmapCache;
import com.example.BusinessHttp.HttpUtil;
import com.example.banban.R;

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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.animation.TranslateAnimation;

public class Fragment1 extends Fragment  {
	private ImageView imageView;
	private ImageView image;
	private Bitmap  bitmap=null;
	private ArrayList<Fragment> fragmentList;
	private static int RESULT_LOAD_IMAGE = 1;
	private ViewPager mPager;
	private int bmpW;// 横线图片宽度
	private TextView view1, view2, view3,view4;	//页卡头标
	private TextView tname,tdonate;
	private int currIndex;// 当前页卡编号
	private int offset;// 图片移动的偏移量
	private static String path="/sdcard/banban/photo/";
	private GettingHandler handler=new GettingHandler();
	private String  uri="http://omegaga.net/banban/stores/detail/";
	private String  uri2="http://omegaga.net/banban/stores/update";
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View parentView = inflater.inflate(R.layout.myyshop, container, false);
		bitmap=BitmapFactory.decodeFile(path+"head.png");
		image=(ImageView)parentView.findViewById(R.id.cursor);
		tname=(TextView) parentView.findViewById(R.id.tv_store_name);
		tdonate=(TextView) parentView.findViewById(R.id.tv_product_price);
		view1 = (TextView) parentView.findViewById(R.id.tv_guid1);
		view2 = (TextView) parentView.findViewById(R.id.tv_guid2);
		view3 = (TextView) parentView.findViewById(R.id.tv_guid3);
		view4 = (TextView) parentView.findViewById(R.id.tv_guid4);
		view1.setOnClickListener(new txListener(0));
		view2.setOnClickListener(new txListener(1));
		view3.setOnClickListener(new txListener(2));
		view4.setOnClickListener(new txListener(3));
		mPager = (ViewPager) parentView.findViewById(R.id.viewpager);
		imageView=(ImageView)parentView.findViewById(R.id.imageView1);
		
		//判断用户是否保存头像
			if(bitmap!=null){
				imageView.setImageBitmap(bitmap);
			}
		//调用网络
			Initnetwork();
		//初始化上部图像图像
			Initimage();
		//初始化动画 操作
			Initimage2();
		//初始化下面
			InitViewPager();
		return parentView;
	}
	//点击第几个就切换当前第几个的页卡
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
	private void Initnetwork(){
		if(localStore.donateString_numString==null||localStore.storenameString==null){
			String Furi=uri+localStore.store_id;
			HttpUtil.JsonGetRequest(Furi, handler,  Merchant_main.BBQueue);
		}
		else{
			tname.setText(localStore.storenameString);
			tdonate.setText(localStore.donateString+localStore.donateString_numString);
		}
		
	}
	private class GettingHandler extends Handler{
		
		 public void dispatchMessage(Message msg) {
			 switch (msg.what) {
			 case 0x123:
				 JSONObject jsonObj = (JSONObject) msg.obj;
			 	 int code=jsonObj.optInt("ret_code");
			 	switch (code) {
			 		case 0:
					try {
						String nameString=jsonObj.getString("name");
						tname.setText(nameString);
						localStore.storenameString=nameString;
						int total=jsonObj.getInt("total_donate");
						tdonate.setText("累计捐款 "+total);
						localStore.donateString_numString=total+"";
						Log.v("haha", ""+tdonate);
						String picString=jsonObj.getString("image");
						String picURI="http://omegaga.net/banban/"+picString;
						String address=jsonObj.getString("address");
						localStore.address=address;
						String phoneString=jsonObj.getString("phone");
						localStore.phoneString=phoneString;
						String description=jsonObj.getString("description");
						localStore.description=description;
						
						if(bitmap==null){
							ImageLoader imageLoader = new ImageLoader(Merchant_main.BBQueue, new BitmapCache()); 
							ImageListener listener = ImageLoader.getImageListener(imageView, 
									R.drawable.moren, R.drawable.moren);
							imageLoader.get(picURI,listener);
						}
								
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			 		break;
			 			
			 			
			 	}
			 break;
			 }
		 }
	}
	//初始化函数
	private void Initimage(){
		
			//图片长按切换头像操作
		imageView.setOnLongClickListener(new OnLongClickListener() {	
					@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				Builder dialog=new AlertDialog.Builder(getActivity());
				dialog.setPositiveButton("切换商家图片",new DialogInterface.OnClickListener() {
							
				@Override
					public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
						Intent intent=new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						startActivityForResult(intent, RESULT_LOAD_IMAGE);
					}
				});
				dialog.show();
				return false;
			}
		});
	}
	//监听长按函数
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		 if(requestCode==RESULT_LOAD_IMAGE && resultCode==-1 && data!=null)
	        {
			  //这个图片的URI	
			  Uri selectedImage =data.getData();
			  //获得图像的绝对路径
			  String picturePath = PhotoM.getpicture(getActivity(), selectedImage);
			  //将图像进行压缩;
			  Bitmap bitmap2=Compress_Save.getSmallBitmap(picturePath,imageView.getWidth(),imageView.getHeight());
			 // 
			  imageView.setImageBitmap(bitmap2);
			  //图片保存
			  SaveinSD.savephoto(bitmap2);  
			  //将图片上传
			  
			  Map<String, String> map=new HashMap<String, String>();
			  map.put("image", PhotoM.imgToBase64(bitmap2));
			  HttpUtil.NormalPostRequest(map, uri2, handler, Merchant_main.BBQueue);
	        }
	}
	/*
	 * 动画操作
	 */
	private void Initimage2(){
		bmpW = BitmapFactory.decodeResource(getResources(),
				R.drawable.add).getWidth();
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;
		offset = (screenW / 4 - bmpW) / 2;

		// ImageView设置平移，使下划线平移到初始位置（平移一个offset）
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		image.setImageMatrix(matrix);
	}	
	//初始化ViewPager
	private void InitViewPager(){
		fragmentList = new ArrayList<Fragment>();
		fragmentList.add(new Store_info());
		fragmentList.add(new Store_product());
		fragmentList.add(new Store_records());
		fragmentList.add(new Store_reviews());
		//先构建一个适配器
		mPager.setAdapter(new MyFragmentPagerAdapter(
				getChildFragmentManager(), fragmentList));
		mPager.setCurrentItem(0);// 设置当前显示标签页为第一页
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());// 页面变化时的监听器
	}
	/*
	 * 监听函数
	 */
	
	public class MyOnPageChangeListener implements OnPageChangeListener{

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageSelected(int arg0) {
			int one = offset * 2 + bmpW;// 两个相邻页面的偏移量
			// TODO Auto-generated method stub
			Animation animation = new TranslateAnimation(currIndex * one, arg0
					* one, 0, 0);// 平移动画
			currIndex = arg0;
			animation.setFillAfter(true);// 动画终止时停留在最后一帧，不然会回到没有执行前的状态
			animation.setDuration(200);// 动画持续时间0.2秒
			image.startAnimation(animation);// 是用ImageView来显示动画的
			int i = currIndex + 1;
		}
		
	};
	//适配器 
	private class MyFragmentPagerAdapter extends FragmentPagerAdapter{
		ArrayList<Fragment> list;
		public MyFragmentPagerAdapter(FragmentManager fm,ArrayList<Fragment> list) {
			super(fm);
			// TODO Auto-generated constructor stub
			this.list = list;
		}
		@Override
		public Fragment getItem(int arg0) {
			// TODO Auto-generated method stub
			return list.get(arg0);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}
		
	}
}
