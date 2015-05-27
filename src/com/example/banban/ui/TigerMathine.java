package com.example.banban.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.SlotMachineAdapter;

public class TigerMathine {

	public interface TigerAnimFinishedCallBack {
		public void onTigerAnimFinished();
	}

	private TigerAnimFinishedCallBack m_tigerAnimFinishedCallBack;

	protected static final String LOG_TAG = TigerMathine.class.getName();
	private Context m_context;
	private List<WheelView> m_wheelViews;

	private List<Boolean> m_isWheelFinished;
	private Handler m_handler;

	private boolean isWheelScrolling = false; // 车轮滚动标志

	public TigerMathine(Context context, List<WheelView> wheelViews,
			TigerAnimFinishedCallBack callback) {
		m_context = context;
		m_wheelViews = wheelViews;
		m_tigerAnimFinishedCallBack = callback;
		m_isWheelFinished = new ArrayList<Boolean>();
		init();
	}

	private void init() {
		for (int i = 0; i < m_wheelViews.size(); i++) {
			m_isWheelFinished.add(false);
			WheelView wheelView = m_wheelViews.get(i);
			wheelView.setViewAdapter(new SlotMachineAdapter(m_context));
			wheelView.setCurrentItem(0);
			wheelView.setCyclic(true); // can cycle
			wheelView.setEnabled(false); // TODO ?
			wheelView.addScrollingListener(scrolledListener);
		}
		
		m_handler = new Handler(Looper.getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					m_tigerAnimFinishedCallBack.onTigerAnimFinished();
					break;

				default:
					break;
				}
				super.handleMessage(msg);
			}
		};
	}

	public void startAnim(boolean flag) {
		if (isWheelScrolling) {
			return;
		}

		/*
		 * reset to start position
		 */
		Random random = new Random();
		int resetIndex = random.nextInt(m_wheelViews.size());
		resetIndex = Math.abs(resetIndex);
		for (int i = 0; i < m_wheelViews.size(); i++) {
			WheelView wheelView = m_wheelViews.get(i);
			wheelView.setCurrentItem(resetIndex, false);
		}

		m_handler.sendEmptyMessageDelayed(0, 6 * 1000);
		
		if (flag) {
			for (int i = 0; i < m_wheelViews.size(); i++) {
				WheelView wheelView = m_wheelViews.get(i);
				wheelView.scroll(wheelView.getVisibleItems() * 5,
						(i + 3) * 1000);
			}
		} else {
			for (int i = 0; i < m_wheelViews.size(); i++) {
				WheelView wheelView = m_wheelViews.get(i);
				wheelView.scroll(wheelView.getVisibleItems() * 5 + i,
						(i + 3) * 1000);
			}
		}

	}

	// 车轮滚动的监听器
	private OnWheelScrollListener scrolledListener = new OnWheelScrollListener() {
		public void onScrollingStarted(WheelView wheel) {
			isWheelScrolling = true;
			m_isWheelFinished.set(wheel.getCurrentItem(), false);
			Log.v(LOG_TAG, "wheel started with " + wheel.getCurrentItem());
		}

		public void onScrollingFinished(WheelView wheel) {
			isWheelScrolling = false;
			m_isWheelFinished.set(wheel.getCurrentItem(), true);
			Log.v(LOG_TAG, "wheel finished with " + wheel.getCurrentItem());

			// 用的代码写的不靠谱，所以不一定能完全停下来，先注释掉，用下面的方法 TODO
			// for (int i = 0; i < m_isWheelFinished.size(); i++) {
			// if (!m_isWheelFinished.get(i)) {
			// return;
			// }
			// }
			// m_tigerAnimFinishedCallBack.onTigerAnimFinished();

//			/*
//			 * 只要最后两个中有停下来的，就暴力的表示全部停了
//			 */
//			if (wheel.getCurrentItem() == (m_wheelViews.size() - 1)) {
//				m_tigerAnimFinishedCallBack.onTigerAnimFinished();
//			}

		}
	};

}
