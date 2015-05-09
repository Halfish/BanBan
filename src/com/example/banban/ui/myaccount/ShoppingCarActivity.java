package com.example.banban.ui.myaccount;

/*
 * @author: BruceZhang
 * @description: 我的账户 购物车Tab选项卡中的某一项点击后，进入此项商品的特定界面
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.banban.R;
import com.example.banban.ui.BaseActionBarActivity;
import com.example.banban.ui.specificbuy.StoreInfoActivity;

public class ShoppingCarActivity extends BaseActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bb_activity_myaccount_shoppingcar);

		initWidgets();
	}

	private void initWidgets() {
		Button storeNameBtn = (Button) findViewById(R.id.btn_store_name);
		Button shoppingCodeBtn = (Button) findViewById(R.id.btn_shopping_code);
		Button shareBtn = (Button) findViewById(R.id.btn_share);
		Button giveUpBtn = (Button) findViewById(R.id.btn_giveup);

		storeNameBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ShoppingCarActivity.this,
						StoreInfoActivity.class);
				startActivity(intent);
			}
		});

		shoppingCodeBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// do nothing
			}
		});

		shareBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				new AlertDialog.Builder(ShoppingCarActivity.this)
				.setTitle("确定分享该商品？")
				.setPositiveButton("确定", new OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
						finish();
					}
				})
				.setNegativeButton("取消", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// do nothing
					}
				})
				.show();
			}
		});

		giveUpBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				new AlertDialog.Builder(ShoppingCarActivity.this)
						.setTitle("确定放弃吗？")
						.setPositiveButton("确定", new OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								finish();
							}
						})
						.setNegativeButton("取消", new OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								// do nothing
							}
						})
						.show();
			}
		});

	}
}
