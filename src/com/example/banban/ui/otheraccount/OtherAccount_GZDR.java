package com.example.banban.ui.otheraccount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.banban.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class OtherAccount_GZDR extends Activity {
	private ListView listView;
	MyAdapter adapter;
	ArrayList<HashMap<String, Object>> lstViewItem;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bb_other_list);
		lstViewItem = new ArrayList<HashMap<String, Object>>();
		listView=(ListView) findViewById(R.id.listview);
		//将数据绑定
		adapter = new MyAdapter(OtherAccount_GZDR.this,lstViewItem,
				 R.layout.bb_item_gaunzhu,
				 new String[] { "guanzhu_name"},
				 new int[] {R.id.textView1}
				 );
		listView.setAdapter(adapter); 
	}
	//缺handler
	
	class MyAdapter extends SimpleAdapter{

		public MyAdapter(Context context, List<? extends Map<String, ?>> data,
				int resource, String[] from, int[] to) {
			super(context, data, resource, from, to);
			// TODO Auto-generated constructor stub
		}
		public View getView(final int position, View convertView, ViewGroup parent){
			View v = super.getView(position, convertView, parent);
			//网络获取头像操作
			
			return v;
		}
	}
}
