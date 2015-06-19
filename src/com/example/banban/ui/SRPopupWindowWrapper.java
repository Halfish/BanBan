package com.example.banban.ui;

import com.example.banban.R;
import com.example.banban.other.STTimer;
import com.example.banban.other.STTimer.OnTimer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.PaintDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;

/* @Author: newma
 * @date: 2015-01-26
 * @detail: 重载了popupWindow，为其添加显示后定时消失的功能。并且针对工程提供了两个快速设置内容的接口：bindImageView和bindExpressionView
 */
public class SRPopupWindowWrapper extends PopupWindow implements OnTimer {

	private static final int DELAY_DISMISS_TIME = 600;	// ms
	
	private static final int IMAGE_DEF_WITH = 400;
	private static final int IMAGE_DEF_HEIGHT = 400;
	private static final int EXPRESSION_WIDTH = 600;
	private static final int EXPRESSION_HEIGHT = 400;
	
	private STTimer m_timer;
	private View m_viewShowInside;
	
	private View m_popImageView;
	
	public SRPopupWindowWrapper(Context context) {
		this(context, null);
	}
	
	@SuppressLint("InflateParams") 
	public SRPopupWindowWrapper(Context context, View parent) {
		initAttributes(); 
		
		m_viewShowInside = parent;
		m_timer = new STTimer(this);
		
		// 初始化好图片的popup内容
		LayoutInflater lyf = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		m_popImageView = lyf.inflate(R.layout.sr_popup_client_view, null);		
	}
	
	private void initAttributes() {
		setOutsideTouchable(true);		// 允许点击外部时，窗口消失
	}
	
	public void bindImageView(int resId) {
		bindImageView(resId, IMAGE_DEF_WITH, IMAGE_DEF_HEIGHT);
	}
	
	public void bindImageView(int resId, int width, int height) {
		ImageView imageView = (ImageView) m_popImageView.findViewById(R.id.img_pop);
		imageView.setImageResource(resId);
		setContentView(m_popImageView);	
		
		setWidth(width);
		setHeight(height);
	}
	
	public void bindExpressionView(View v) {
		bindExpressionView(v, EXPRESSION_WIDTH, EXPRESSION_HEIGHT);
	}
	
	public void bindExpressionView(View v, int width, int height) {
		setContentView(v);
		setBackgroundDrawable(new PaintDrawable());
		
		setWidth(width);
		setHeight(height);
	}
	
	public void show(boolean autoDismiss) {
		setFocusable(true);
		showAtLocation(m_viewShowInside, Gravity.CENTER, 0, 0);
		if (autoDismiss) {
			m_timer.start(DELAY_DISMISS_TIME);
		}
	}
	
	@Override
	public void dismiss() {
		super.dismiss();
		if (m_timer.isSchedule()) {
			m_timer.stop();
		}
	}
	
	@Override
	public void OnTrigger(Object arg) {
		dismiss();
	}
}
