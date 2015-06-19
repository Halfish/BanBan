package com.example.banban.other;

import java.util.TimerTask;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/*
 * 定时类封装，目前已支持参数传递
 */
public class STTimer {
	private final String LOG_TAG = STTimer.class.getName();
	
	/* 
	 * 定时触发接口
	 */
	public interface OnTimer {
		/*
		 * arg参数是在定时器开启时传入的参数，在定时过程是不能被修改的
		 * 并且在响应此事件时，定时器将保证是在停止的状态
		 */
		public void OnTrigger(Object arg);
	}
	
	private OnTimer m_trigger;
	private long m_delay;		// 定时时间，以毫秒为单位
	private java.util.Timer m_timer;
	private boolean m_isRepeat;
			
	public STTimer(OnTimer onTimerCB) {
		m_trigger = onTimerCB;
		m_isRepeat = false;
	}
	
	/*
	 * 默认无参数传递的定时开启函数
	 */
	public boolean start(long delay) {
		return start(delay, null);
	}
	
	public boolean start(long delay, Object arg) {
		return start(delay, arg, false);
	}
	
	/*
	 * 有参数传递的定时开启函数，参数将被传递到定时响应函数里面去
	 */
	public boolean start(long delay, Object arg, boolean bRepeat) {
		if (delay < 0) {
			Log.w(LOG_TAG, "start: Delay time can not be a negative number");
			return false;
		}

		if (m_timer != null) {
			Log.e(LOG_TAG, "start: Timer is run now, it will be rescheduled");
			stop();
		}
		
		m_timer = new java.util.Timer();		
		m_delay = delay;
		m_isRepeat = bRepeat;
		
		MessageObject obj = new MessageObject(m_timer, arg, bRepeat);
		
		if (bRepeat) {
			m_timer.schedule(new OnTimerTask(obj), delay, delay);
		} else {
			m_timer.schedule(new OnTimerTask(obj), delay);
		}
		
		return true;	
	}
	
	public void stop() {
		if (m_timer != null) {
			m_timer.cancel();
			m_timer = null;
		}
	}

	public long getInitDelayTimer() {
		return m_delay;
	}
	
	public boolean isRepeat() {
		return m_isRepeat;
	}
	
	/*
	 * 判断是否正在处于定时状态中
	 */
	public boolean isSchedule() {
		return m_timer != null;
	}
	
	protected class OnTimerTask extends TimerTask {
		MessageObject m_obj;
		public OnTimerTask(MessageObject object) {
			m_obj = object;
		}
		
		@Override
		public void run() {
			Message msg = m_handler.obtainMessage(ON_TIMER, m_obj);
			m_handler.sendMessage(msg);
		}
	}
	
	protected class MessageObject {
		java.util.Timer timer;
		Object argument;
		boolean bRepeat;
		
		public MessageObject(java.util.Timer timer, Object arg, boolean bRepeat) {
			this.timer = timer;
			this.argument = arg;
			this.bRepeat = bRepeat;
		}
	}

	protected static final int ON_TIMER = 1;
	protected Handler m_handler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ON_TIMER:
				MessageObject object = (MessageObject)msg.obj;
				if (object == null || object.timer == null) {
					Log.w(LOG_TAG, "Handler on timer message: Timer is not in secheduled!");
					break;
				}
				
				//　先停止定时器再响应定时事件
				if (!object.bRepeat) {
					try {
						object.timer.cancel();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				
				if (STTimer.this.m_trigger != null) {
					STTimer.this.m_trigger.OnTrigger(object.argument);
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}		
	};
}
