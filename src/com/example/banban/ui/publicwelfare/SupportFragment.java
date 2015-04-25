package com.example.banban.ui.publicwelfare;

import com.example.banban.R;
import com.example.banban.ui.fragments.BaseActionBarFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class SupportFragment extends BaseActionBarFragment {
	private Button m_fundButton;
	private TextView m_fundTextView;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View view = getActivity().getLayoutInflater().inflate(
				R.layout.bb_fragment_publicwelfare_support, container, false);

		m_fundButton = (Button)view.findViewById(R.id.btn_fund);
		m_fundTextView = (TextView)view.findViewById(R.id.tv_fund_money);
		m_fundButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				m_fundTextView.setVisibility(View.VISIBLE);
				m_fundButton.setEnabled(false);
			}
		});
		
		return view;
	}
}
