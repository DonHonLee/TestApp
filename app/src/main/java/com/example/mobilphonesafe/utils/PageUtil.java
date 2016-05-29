package com.example.mobilphonesafe.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.example.mobilphonesafe.R;

import java.util.ArrayList;
import java.util.List;



public class PageUtil {

	public static final boolean isCycle = false;

	/**
	 *
	 * 
	 * @param context
	 * @return
	 */
	public static List<View> getPageList(Context context) {
		List<View> pageList = new ArrayList<View>();
		pageList.add(getPageView(context, R.drawable.guide1));
		pageList.add(getPageView(context, R.drawable.guide2));
		pageList.add(getPageView(context, R.drawable.guide3));
		pageList.add(getPageView(context, R.drawable.guide4));
		return pageList;
	}

	/**
	 *
	 * 
	 * @param context
	 * @param imgResId
	 * @return
	 */
	private static View getPageView(Context context, int imgResId) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View pageView = inflater.inflate(R.layout.page_item, null);
		ImageView imgPage = (ImageView) pageView.findViewById(R.id.imgPage);

		imgPage.setBackgroundResource(imgResId);
		return pageView;
	}
}
