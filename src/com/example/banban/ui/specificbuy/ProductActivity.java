package com.example.banban.ui.specificbuy;

import com.example.banban.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ProductActivity extends Activity {
	private Button m_buyButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bb_activity_specificbuy_product);

		m_buyButton = (Button)findViewById(R.id.btn_buy);
		m_buyButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getBaseContext(), "已经加入购物车", Toast.LENGTH_SHORT)
						.show();
				m_buyButton.setEnabled(false);
			}
		});
	}
}
