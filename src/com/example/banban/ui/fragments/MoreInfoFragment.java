package com.example.banban.ui.fragments;

/*
 * @author: BruceZhang
 * @description: 更多信息 fragment
 */


import com.example.banban.R;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MoreInfoFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.bb_fragment_more_info,
				container, false);

		return view;
	}
}
