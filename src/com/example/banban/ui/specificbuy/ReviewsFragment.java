package com.example.banban.ui.specificbuy;

/*
 * @author: BruceZhang
 * @description: 商家页面 第三个Tab选项 对该商家的评价，
 * 可以跳到评价界面
 */

import com.example.banban.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.Toast;

public class ReviewsFragment extends Fragment implements
		OnRatingBarChangeListener, OnClickListener {

	RatingBar m_ratingBar;
	Button m_reviewButton;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.bb_fragment_specificbuy_reviews, container, false);
		m_ratingBar = (RatingBar) rootView.findViewById(R.id.ratingBar_review);
		m_ratingBar.setOnRatingBarChangeListener(this);
		m_reviewButton = (Button)rootView.findViewById(R.id.btn_write_reviews);
		m_reviewButton.setOnClickListener(this);
		
		return rootView;
	}

	@Override
	public void onRatingChanged(RatingBar ratingBar, float rating,
			boolean fromUser) {
		Toast.makeText(getActivity(), "rating is " + rating, Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(getActivity(), WritingReviewActivity.class);
		startActivity(intent);
	}

}
