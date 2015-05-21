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
}
