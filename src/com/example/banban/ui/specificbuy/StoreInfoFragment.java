package com.example.banban.ui.specificbuy;

import com.example.banban.R;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class StoreInfoFragment extends Fragment {

	private TextView m_storeInfoTV;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.bb_fragment_specificbuy_storeinfo, container, false);
		m_storeInfoTV = (TextView)rootView.findViewById(R.id.tv_store_detail_zhishan);
		m_storeInfoTV.setMovementMethod(ScrollingMovementMethod.getInstance());
		return rootView;
	}

}
