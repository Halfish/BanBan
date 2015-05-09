package com.example.banban.ui;

/*
 * @author: BruceZhang
 * @description: 随机抢 抢到的某商品，不可再抢，只是静态展示没有功能
 */

import com.example.banban.R;

import android.app.Activity;
import android.os.Bundle;

public class ProductInfoActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bb_activity_product_info);
	}
}
