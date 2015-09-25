package com.bbk.utils;

import java.util.List;

import com.bbk.data.DateInfo;
import com.bbk.data.Holiday;

import android.app.Activity;
import android.util.DisplayMetrics;

public class DataUtils {
	public static void checkLastSeven(List<DateInfo> list) {
		if (list.size() < 42)
			return;
		for (int i = 35; i < list.size(); i++) {
			if (list.get(i).getDate() != -1)
				return;
		}
		int j = 41;
		while (j >= 35) {
			list.remove(list.size() - 1);
			j--;
		}
	}
	
	public static int getFirstIndexOf(List<DateInfo> list) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getDate() != -1)
				return i;
		}
		return -1;
	}
	
	public static int getLastIndexOf(List<DateInfo> list) {
		for (int i = list.size() - 1; i >= 0; i--) {
			if (list.get(i).getDate() != -1)
				return i;
		}
		return -1;
	}
	
	public static boolean isHoliday(String lunarDate) {
		for (int i = 0; i < Holiday.solarHolidayName.length; i++) {
			if (lunarDate.equals(Holiday.solarHolidayName[i]))
				return true;
		}
		for (int i = 0; i < Holiday.lunarHolidayName.length; i++) {
			if (lunarDate.equals(Holiday.lunarHolidayName[i]))
				return true;
		}
		return false;
	}
	
	public static int getDayFlag(List<DateInfo> list, int day) {
		int i;
		for (i = 0; i < list.size(); i++) {
			if (list.get(i).getDate() == day && list.get(i).isThisMonth()) {
				return i;
			}
		}
		for (i = list.size() - 1; i >= 0; i--) {
			if (list.get(i).isThisMonth()) {
				break;
			}
		}
		return i;
	}
	
	public static int getScreenWidth(Activity activity) {
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
		int width = mDisplayMetrics.widthPixels;
		return width;
	}
	
	public static int getScreenHeight(Activity activity) {
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
		int height = mDisplayMetrics.heightPixels;
		return height;
	}
	
}
