package com.example.banban.ui.myaccount;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.banban.R;
import com.example.banban.ui.BaseActionBarActivity;
import com.example.banban.ui.StoreInfoActivity;

public class ShareActivity extends BaseActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bb_activity_myaccount_share);

		initWidgets();
	}

	private void initWidgets() {
		Button storeNameBtn = (Button) findViewById(R.id.btn_store_name);
		Button collectBtn = (Button) findViewById(R.id.btn_collected);

		storeNameBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ShareActivity.this,
						StoreInfoActivity.class);
				startActivity(intent);
			}
		});

		collectBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

	}
}
