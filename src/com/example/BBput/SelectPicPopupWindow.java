package com.example.BBput;

import com.halfish.banban.R;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.PopupWindow;

public class SelectPicPopupWindow extends PopupWindow {
	 private Button btn_take_photo, btn_pick_photo, btn_cancel;
	 private View mMenuView;
	 @SuppressLint("InflateParams") @SuppressWarnings("deprecation")
	public SelectPicPopupWindow(Activity context,OnClickListener itemsOnClick){
		 super(context);
		 LayoutInflater inflater = (LayoutInflater) context  
	                .getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		 mMenuView = inflater.inflate(R.layout.popup, null); 
		 btn_take_photo=(Button)mMenuView.findViewById(R.id.btn_take_photo);
		 btn_pick_photo = (Button) mMenuView.findViewById(R.id.btn_pick_photo);  
	     btn_cancel = (Button) mMenuView.findViewById(R.id.btn_cancel);
	     //取消按钮  
	     btn_cancel.setOnClickListener(new OnClickListener() {  
	  
	            public void onClick(View v) {  
	                //销毁弹出框  
	                dismiss();  
	            }  
	     });  
	     //去相册读取按钮 
	     btn_pick_photo.setOnClickListener(itemsOnClick);  
	     btn_take_photo.setOnClickListener(itemsOnClick);  
	   //设置SelectPicPopupWindow的View  
	   this.setContentView(mMenuView); 
	 //设置SelectPicPopupWindow弹出窗体的宽  
       this.setWidth(LayoutParams.FILL_PARENT);  
       //设置SelectPicPopupWindow弹出窗体的高  
       this.setHeight(LayoutParams.WRAP_CONTENT);  
       //设置SelectPicPopupWindow弹出窗体可点击  
       this.setFocusable(true);  
       //设置SelectPicPopupWindow弹出窗体动画效果  
       this.setAnimationStyle(R.style.AnimBottom);  
       //实例化一个ColorDrawable颜色为半透明  
       ColorDrawable dw = new ColorDrawable(0xb0000000);
       this.setBackgroundDrawable(dw);
	     
       mMenuView.setOnTouchListener(new OnTouchListener(){
    	   public boolean onTouch(View v, MotionEvent event) {  
               
               int height = mMenuView.findViewById(R.id.pop_layout).getTop();  
               int y=(int) event.getY();  
               if(event.getAction()==MotionEvent.ACTION_UP){  
                   if(y<height){  
                       dismiss();  
                   }  
               }                 
               return true;  
           }  
       
       });
	 }
}
