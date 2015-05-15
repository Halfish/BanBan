package com.example.banban.ui.fragments;

/*
 * @author: BruceZhang
 * @description: 公益众筹 fragment
 */


import java.util.ArrayList;

import com.example.banban.R;
import com.example.banban.ui.publicwelfare.HotestFragment;
import com.example.banban.ui.publicwelfare.NewestFragment;
import com.example.banban.ui.publicwelfare.RecommendedFragment;
import com.example.banban.ui.publicwelfare.SuccessfulFragment;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class PublicWelfareFragment extends BaseActionBarFragment {

	private ViewPager mPager;
	private ArrayList<Fragment> fragmentList;
	private ImageView image;
	private TextView view1, view2, view3;
	private int currIndex;// 当前页卡编号
	private int bmpW;// 横线图片宽度
	private int offset;// 图片移动的偏移量
	
	private Activity m_activity;
	private ActionBar m_actionBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_activity = getActivity();
		m_actionBar = m_activity.getActionBar();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.bb_fragment_public_welfare,
				container, false);

		//setActionBarCenterTitle(R.string.bb_tab_public_welfare);
		m_actionBar.setDisplayShowTitleEnabled(false);
		m_actionBar.setDisplayUseLogoEnabled(false);
		m_actionBar.setDisplayShowHomeEnabled(false);
		
		InitTextView(view);
		InitImage(view);
		InitViewPager(view);

		return view;
	}

	private void InitTextView(View view) {
		view1 = (TextView) view.findViewById(R.id.tv_guid1);
		view2 = (TextView) view.findViewById(R.id.tv_guid2);
		view3 = (TextView) view.findViewById(R.id.tv_guid3);

		view1.setOnClickListener(new txListener(0));
		view2.setOnClickListener(new txListener(1));
		view3.setOnClickListener(new txListener(2));
	}

	private class txListener implements View.OnClickListener {
		private int index = 0;

		public txListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(index);
		}
	}

	private void InitImage(View view) {
		image = (ImageView) view.findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(),
				R.drawable.bb_cursor).getWidth();
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;
		offset = (screenW / 4 - bmpW) / 2;

		// ImageView设置平移，使下划线平移到初始位置（平移一个offset）
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		image.setImageMatrix(matrix);
	}

	private void InitViewPager(View view) {
		mPager = (ViewPager) view.findViewById(R.id.viewpager);
		fragmentList = new ArrayList<Fragment>();
		fragmentList.add(new NewestFragment());
		fragmentList.add(new HotestFragment());
		fragmentList.add(new RecommendedFragment());
		fragmentList.add(new SuccessfulFragment());

		// 给ViewPager设置适配器
		mPager.setAdapter(new MyFragmentPagerAdapter(getChildFragmentManager(),
				fragmentList));
		mPager.setCurrentItem(0);// 设置当前显示标签页为第一页
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());// 页面变化时的监听器
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
}
