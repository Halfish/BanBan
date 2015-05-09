package com.example.banban.ui.fragments;

/*
 * @author: BruceZhang
 * @description: 随机抢 fragment
 */


import com.example.banban.R;
import com.example.banban.ui.ProductInfoActivity;
import com.example.sortlistview.AlphabetaContactActicity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class RandomBuyFragment extends BaseActionBarFragment {

	public static final int REQUEST_CODE_LOCATION = 1;
	public static final int RESULT_CODE_LOCATION = 2;

	private Button m_randomBuyBtn;
	private TextView m_chanceTextView;
	private TextView m_infoTextView;
	private ProgressBar m_progBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onResume() {
		super.onResume();
		m_progBar.setVisibility(View.INVISIBLE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.bb_fragment_random_buy,
				container, false);
		initActionBar();
		initWidgets(view);
		return view;
	}

	private void initActionBar() {
		setActionBarCenterTitle(R.string.bb_tab_random_buy);
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setTitle(R.string.beijing);
		actionBar.setIcon(R.drawable.bb_location);
		actionBar.setHomeButtonEnabled(true);
		actionBar.show();
	}

	private void initWidgets(View view) {
		m_randomBuyBtn = (Button) view.findViewById(R.id.btn_random_buy);
		m_chanceTextView = (TextView) view.findViewById(R.id.tv_chance);
		m_infoTextView = (TextView) view.findViewById(R.id.tv_info);
		m_progBar = (ProgressBar) view.findViewById(R.id.progressBar_rb);

		m_randomBuyBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				m_chanceTextView.setVisibility(View.INVISIBLE);
				m_infoTextView.setVisibility(View.VISIBLE);
				m_randomBuyBtn.setVisibility(View.INVISIBLE);
				m_progBar.setVisibility(View.VISIBLE);
				Intent intent = new Intent(m_activity,
						ProductInfoActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.bb_menu_fragment_random_buy, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(m_activity,
					AlphabetaContactActicity.class);
			startActivityForResult(intent, REQUEST_CODE_LOCATION);
			break;

		case R.id.menu_category:
			Toast.makeText(m_activity, "Category", Toast.LENGTH_LONG).show();
			break;

		default:
			break;
		}
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_LOCATION
				&& resultCode == RESULT_CODE_LOCATION) {
			String location = data.getStringExtra("location");
			m_actionBar.setTitle(location);
		}
	}
}
