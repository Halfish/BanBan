package com.example.banban.ui.publicwelfare;

import com.example.banban.R;
import com.example.banban.ui.fragments.BaseActionBarFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DetailFragment extends BaseActionBarFragment {
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View view = getActivity().getLayoutInflater().inflate(
				R.layout.bb_fragment_publicwelfare_detail, container, false);

		return view;
	}
}
