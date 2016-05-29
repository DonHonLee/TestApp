package com.example.mobilphonesafe.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.example.mobilphonesafe.R;
import com.example.mobilphonesafe.engine.MyPageAdapter;
import com.example.mobilphonesafe.utils.PageUtil;

import java.util.List;




public class GuidePageActivity extends Activity implements OnPageChangeListener {
	private ViewPager vp_guide;
	private List<View> pageList;
	/** 界面底部的指示圆点容器 */
	private LinearLayout layout_dotView;
	private LinearLayout layout_buttonView;
	private ImageView[] imgDots;
	/** 统计页卡个数 */
	private int dotCount;
	private Button goin;
	private ImageView iconImg;
	SharedPreferences.Editor editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences preferences = getSharedPreferences("IS_USERD",Activity.MODE_PRIVATE);
		editor = preferences.edit();
		Boolean is_used = preferences.getBoolean("IS_USERD", false);
		if(is_used == false){
			setContentView(R.layout.activity_guide_page);
			initView();
			initDots();
			setPage();
			// 设置跳动图标
			//iconImg = (ImageView) findViewById(R.id.icon);
			//Jumper jumper = new Jumper(666, 166);
			//jumper.attachToView(iconImg, iconImg);
		}else{
			Intent intent = new Intent();
			intent.setClass(GuidePageActivity.this, SplashActivity.class);
			startActivity(intent);
			finish();
		}
			
	}

	private void initView() {
		layout_dotView = (LinearLayout) findViewById(R.id.layout_dotView);
		goin = (Button) findViewById(R.id.goin);
		layout_buttonView = (LinearLayout) findViewById(R.id.layout_buttonView);
		layout_buttonView.setVisibility(View.INVISIBLE);
		vp_guide = (ViewPager) findViewById(R.id.vp_guide);
		vp_guide.setOnPageChangeListener(this);
		pageList = PageUtil.getPageList(this);
		goin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(GuidePageActivity.this, SplashActivity.class);
				startActivity(intent);
				editor.putBoolean("IS_USERD", true);
				editor.commit();
				finish();
				
			}
		});
		dotCount = pageList.size();
	}

	/** 设置底部圆点 */
	private void initDots() {
		imgDots = new ImageView[dotCount];
		for (int i = 0; i < dotCount; i++) {
			ImageView dotView = new ImageView(this);
			if (i == 0) {
				dotView.setBackgroundResource(R.drawable.dot_white);
			} else {
				dotView.setBackgroundResource(R.drawable.dot_gray);
			}
			imgDots[i] = dotView;

			// 设置圆点布局参数
			LayoutParams params = new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.setMargins(7, 0, 7, 0);
			dotView.setLayoutParams(params);
			layout_dotView.addView(dotView);
		}
	}

	private void setPage() {
		vp_guide.setAdapter(new MyPageAdapter(pageList));
		if (PageUtil.isCycle) {
			/*
			 * 此处设置当前页的显示位置,设置在100(随便什么数,稍微大点就行)就 可以实现向左循环,当然是有限制的,不过一般情况下没啥问题
			 */
			vp_guide.setCurrentItem(100);
		}
	}

	@Override
	public void onPageSelected(int arg0) {
		if (PageUtil.isCycle) {
			arg0 = arg0 % dotCount;
		}
		for (int i = 0; i < dotCount; i++) {
			if (i == arg0) {
				imgDots[i].setBackgroundResource(R.drawable.dot_white);
			} else {
				imgDots[i].setBackgroundResource(R.drawable.dot_gray);
			}
		}
		if (arg0 == 3){
			layout_buttonView.setVisibility(View.VISIBLE);
			//iconImg.setVisibility(View.VISIBLE);
		}
		else{
//			iconImg.setVisibility(View.INVISIBLE);
			layout_buttonView.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}
	
}
