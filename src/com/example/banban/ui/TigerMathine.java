package com.example.banban.ui;

import java.util.List;

import android.content.Context;
import android.util.Log;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.SlotMachineAdapter;

public class TigerMathine {
	protected static final String LOG_TAG = TigerMathine.class.getName();
	private Context m_context;
	private List<WheelView> m_wheelViews;

	private boolean isWheelScrolling = false; // 车轮滚动标志

	public TigerMathine(Context context, List<WheelView> wheelViews) {
		m_context = context;
		m_wheelViews = wheelViews;
		init();
	}

	private void init() {
		for (int i = 0; i < m_wheelViews.size(); i++) {
			WheelView wheelView = m_wheelViews.get(i);
			wheelView.setViewAdapter(new SlotMachineAdapter(m_context));
			wheelView.setCurrentItem(0);
			wheelView.setCyclic(true); // can cycle
			wheelView.setEnabled(false); // TODO ?
			wheelView.addScrollingListener(scrolledListener);
			wheelView.addChangingListener(changedListener);
		}
	}

	public void startAnim() {
		if (isWheelScrolling) {
			return;
		}

		for (int i = 0; i < m_wheelViews.size(); i++) {
			WheelView wheelView = m_wheelViews.get(i);
			wheelView.scroll(wheelView.getVisibleItems()
					* ((int) (Math.random() + 0.5)), (i + 4) * 1000);
		}

	}

	// 车轮滚动的监听器
	private OnWheelScrollListener scrolledListener = new OnWheelScrollListener() {
		public void onScrollingStarted(WheelView wheel) {
			isWheelScrolling = true;
			Log.v(LOG_TAG, "wheel started with " + wheel.getCurrentItem());
		}

		public void onScrollingFinished(WheelView wheel) {
			isWheelScrolling = false;
			Log.v(LOG_TAG, "wheel finished with " + wheel.getCurrentItem());
		}
	};

	// 车轮item改变的监听器
	private OnWheelChangedListener changedListener = new OnWheelChangedListener() {
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			if (!isWheelScrolling) {
				Log.v(LOG_TAG, "wheel change from" + oldValue + " to "
						+ newValue);
			}
		}
	};

}
