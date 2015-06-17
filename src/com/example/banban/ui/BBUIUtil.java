package com.example.banban.ui;

import com.example.banban.R;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class BBUIUtil {
	@SuppressLint("InflateParams")
	public static Dialog getInfoDialog(Context context, String title,
			String info) {
		Dialog dialog = new Dialog(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.bb_custom_dialog, null, false);
		TextView textView = (TextView) view.findViewById(R.id.textView1);
		textView.setText(info);
		dialog.setContentView(view);
		if (title != null) {
			dialog.setTitle(title);
		} else {
			dialog.setTitle(R.string.app_name);
		}

		return dialog;
	}

	// DIP 转 PX
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	// PX 转　DIP
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
}
