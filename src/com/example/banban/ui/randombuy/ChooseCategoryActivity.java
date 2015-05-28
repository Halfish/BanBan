package com.example.banban.ui.randombuy;

import java.util.ArrayList;

import com.example.banban.R;
import com.example.banban.other.BBConfigue;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ChooseCategoryActivity extends Activity {
	public static final String LOG_TAG = ChooseCategoryActivity.class.getName();
	private ListView m_listview;
	private ArrayAdapter<String> m_adapter;
	private ArrayList<String> m_listItems;
	public static final int CATEGORY_RESULT_CODE = 2001;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bb_activity_choose_category);
		initListView();
	}

	private void initListView() {
		m_listview = (ListView) findViewById(R.id.lv_category);
		m_listItems = new ArrayList<String>();

		String category[] = getResources().getStringArray(R.array.category);
		for (int i = 0; i < category.length; ++i) {
			m_listItems.add(category[i]);
		}

		m_adapter = new ArrayAdapter<String>(getBaseContext(), R.layout.title,
				m_listItems);
		
		m_listview.setAdapter(m_adapter);
		
		m_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				BBConfigue.CATEGORY = position + 1;
				finish();
			}
		});
		
	}
}
