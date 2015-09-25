package com.bbk.galendardemo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bbk.utils.TimeUtils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bbk.bean.HistoryBean;
import com.bbk.galendardemo.KCalendar.OnCalendarClickListener;
import com.bbk.galendardemo.KCalendar.OnCalendarDateChangedListener;
import com.eebbk.timepickview.dialog.ScrollDatePickerDialog;
import com.eebbk.timepickview.dialog.ScrollDatePickerDialog.OnDateSetListener;
import com.eebbk.timepickview.scroll.ScrollDatePicker;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class MainActivity extends Activity implements OnDateSetListener {
    // ?mobileCode=stri&userID=
    private String url = "http://api.juheapi.com/japi/toh?key=562d9e6978df961aa77d8506edb3bf13&v=1.0";
    protected static final String TAG = "MainActivity";

    private String date = null;// 设置默认选中的日期 格式为 “2014-04-05” 标准DATE格式
    private String today = null;
    private boolean nextMonth = false;
    private Date thisday = new Date();
    private KCalendar mCalendar = null;
    Button bt;
    private ScrollDatePickerDialog mScrollDatePickerDlg;
    private TextView popupwindow_calendar_month = null;
    private TextView popupwindow_calendar_history = null;
    private Toast mToast = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        bt = (Button) findViewById(R.id.bt);
        mScrollDatePickerDlg = new ScrollDatePickerDialog(this, com.eebbk.timepickview.R.style.TpvStpStyleMetalDate);
        mScrollDatePickerDlg.setSize(540, 480);
        mScrollDatePickerDlg.setOnDateSetListener(this);

        bt.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                new PopupWindows(MainActivity.this, bt);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (null != mToast) {
            mToast.cancel();
        }
    }

    public class PopupWindows extends PopupWindow implements OnClickListener {
        public PopupWindows(Context mContext, View parent) {

            View view = View.inflate(mContext, R.layout.popupwindow_calendar, null);
            final KCalendar calendar = (KCalendar) view.findViewById(R.id.popupwindow_calendar);
            mCalendar = calendar;
            view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
            LinearLayout ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
            ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_bottom_in_1));

            setWidth(LayoutParams.FILL_PARENT);
            setHeight(LayoutParams.FILL_PARENT);
            setBackgroundDrawable(new BitmapDrawable());
            setFocusable(true);
            setOutsideTouchable(true);
            setContentView(view);
            showAtLocation(parent, Gravity.BOTTOM, 0, 0);
            // dateIsNull(calendar); //是否需要显示退出popwindow之前的日历
            update();
            popupwindow_calendar_month = (TextView) view.findViewById(R.id.popupwindow_calendar_month);
            popupwindow_calendar_history = (TextView) view.findViewById(R.id.popupwindow_calendar_history);
            popupwindow_calendar_month.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    onBtnDpd();
                }
            });

            Button popupwindow_calendar_bt_enter = (Button) view.findViewById(R.id.popupwindow_calendar_bt_enter);

            popupwindow_calendar_month.setText(calendar.getCalendarYear() + "年" + calendar.getCalendarMonth() + "月");

            List<String> list = new ArrayList<String>(); // 设置标记列表
            today = String.format("%04d-%02d-%02d", (thisday.getYear() + 1900), (thisday.getMonth() + 1), thisday.getDate());
            list.add(today);
            list.add("2014-04-02");
            calendar.addMarks(list, 0);
            date = today;
            dateIsNull(calendar);

            // 监听所选中的日期
            calendar.setOnCalendarClickListener(new OnCalendarClickListener() {

                public void onCalendarClick(int row, int col, String dateFormat) {
                    int month = Integer.parseInt(dateFormat.substring(dateFormat.indexOf("-") + 1, dateFormat.lastIndexOf("-")));

                    if (calendar.getCalendarMonth() - month == 1// 跨年跳转
                            || calendar.getCalendarMonth() - month == -11) {
                        calendar.removeAllBgColor();
                        calendar.lastMonth();
                        date = dateFormat;
                        dateIsNull(calendar);

                    } else if (month - calendar.getCalendarMonth() == 1 // 跨年跳转
                            || month - calendar.getCalendarMonth() == -11) {
                        calendar.removeAllBgColor();
                        calendar.nextMonth();
                        date = dateFormat;
                        dateIsNull(calendar);
                    } else {
                        calendar.removeAllBgColor();
                        calendar.setCalendarDayBgColor(dateFormat, R.drawable.calendar_date_focused);
                        date = dateFormat;// 最后返回给全局 date
                        Log.d("ss", "date f" + date);
                        dateIsNull(calendar);

                    }
                }
            });

            // 监听当前月份
            calendar.setOnCalendarDateChangedListener(new OnCalendarDateChangedListener() {
                public void onCalendarDateChanged(int year, int month) {
                    popupwindow_calendar_month.setText(year + "年" + month + "月");
                    calendar.removeAllBgColor();

                    int days = Integer.parseInt(date.substring(date.lastIndexOf("-") + 1, (date.length())));
                    Log.d("ssss", "months" + month + ".nextMonth" + nextMonth + ".days" + days + "date" + date);

                    // Log.d("ssss",
                    // "months"+month+".nextMonth"+nextMonth+".days"+days+".lastDays"+lastDays+"date"+date);

                    if (nextMonth) {
                        int lastDays = TimeUtils.getDaysOfMonth(year, month - 1);
                        Log.d("bbk", "jinlia" + days + "..." + lastDays + "month" + month);

                        if (days == lastDays) {
                            Log.d("bbk", "jinliall" + days + "..." + lastDays + "month" + month);
                            if (month == 4 || month == 6 || month == 9 || month == 11) {
                                days = lastDays - 1;
                            } else if (month == 8 || month == 1) {
                                days = lastDays;
                            } else if (month == 2) {
                                if (TimeUtils.isLeapYear(year)) {
                                    days = 29;
                                } else {
                                    days = 28;
                                }
                            } else {
                                days = lastDays;

                            }
                        } else {
                            if (days == (lastDays - 1) & month == 2) {
                                if (TimeUtils.isLeapYear(year)) {
                                    days = 29;
                                } else {
                                    days = 28;
                                }

                            }
                        }

                        Log.d("ssss", "months" + month + ".nextMonth" + nextMonth + ".days" + days + ".last" + lastDays);
                    } else if (nextMonth == false) {
                        int lastDays = TimeUtils.getDaysOfMonth(year, month + 1);

                        if (days == lastDays) {
                            if (month == 4 || month == 6 || month == 9 || month == 11) {
                                days = lastDays - 1;
                            } else if (month == 7 || month == 12) {
                                days = lastDays;
                            } else if (month == 2) {
                                if (TimeUtils.isLeapYear(year)) {
                                    days = 29;
                                } else {
                                    days = 28;
                                }
                            } else {
                                days = lastDays;
                            }
                        } else {
                            if (days == (lastDays - 1) & month == 2) {
                                if (TimeUtils.isLeapYear(year)) {
                                    days = 29;
                                } else {
                                    days = 28;
                                }

                            }
                        }

                        Log.d("ssss", "months" + month + ".nextMonth" + nextMonth + ".days" + days);

                    }

                    //
                    date = String.format("%04d-%02d-%02d", year, month, days);
                    initData(month, days);
                    calendar.showCalendar(year, month);
                    calendar.setCalendarDayBgColor(date, R.drawable.calendar_date_focused);
                    Log.d("ssss", "这个月是" + month + "月，最后一天是" + days + "日." + "date:" + date + ".date2:" + date);

                }
            });

            // 上月监听按钮
            RelativeLayout popupwindow_calendar_last_month = (RelativeLayout) view.findViewById(R.id.popupwindow_calendar_last_month);
            popupwindow_calendar_last_month.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    Log.d("sss", "按左建月 份改变了" + date);
                    calendar.removeAllBgColor();
                    calendar.lastMonth();
                    nextMonth = false;
                }

            });

            // 下月监听按钮
            RelativeLayout popupwindow_calendar_next_month = (RelativeLayout) view.findViewById(R.id.popupwindow_calendar_next_month);
            popupwindow_calendar_next_month.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    Log.d("sss", "按右建月 份改变了" + date);
                    calendar.removeAllBgColor();
                    calendar.nextMonth();
                    nextMonth = true;
                }
            });

            // 关闭窗口
            popupwindow_calendar_bt_enter.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    dismiss();
                }
            });
        }

        @Override
        public void onClick(View v) {
            if (null == mScrollDatePickerDlg) {
                return;
            }

            mScrollDatePickerDlg.show();

        }
    }

    @Override
    public void onDateSet(ScrollDatePicker picker, int year, int month, int day) {
        if (null == popupwindow_calendar_month) {
            return;
        }

        String info = String.format("%04d-%02d-%02d", year, month, day);
        date = info;
        Log.d("ss", "date info" + date);
        dateIsNull(mCalendar);
        mToast = Toast.makeText(this, info, Toast.LENGTH_SHORT);
        mToast.show();

    }

    private void onBtnDpd() {
        if (null == mScrollDatePickerDlg) {
            return;
        }

        mScrollDatePickerDlg.show();
    }

    @SuppressWarnings("unused")
    private void showTip(String info) {
        if (null != mToast) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, info, 0);
        mToast.show();
    }

    private void dateIsNull(KCalendar calendar) {
        calendar.removeAllBgColor();
        if (null != date) {
            calendar.removeAllBgColor();
            int years = Integer.parseInt(date.substring(0, date.indexOf("-")));
            int month = Integer.parseInt(date.substring(date.indexOf("-") + 1, date.lastIndexOf("-")));
            int days = Integer.parseInt(date.substring(date.lastIndexOf("-") + 1, (date.length())));

            initData(month, days);
            popupwindow_calendar_month.setText(years + "年" + month + "月");

            calendar.showCalendar(years, month);
            calendar.setCalendarDayBgColor(date, R.drawable.calendar_date_focused);
            Log.d("sss", "点击了一下,应该选中" + month + "月" + date + "日");
        }

    }

    private void initData(int calendarMonth, int calendarDay) {
        RequestParams pa = new RequestParams();
        pa.addBodyParameter("month", calendarMonth + "");
        pa.addBodyParameter("day",calendarDay + "");
        HttpUtils http = new HttpUtils();
        HttpHandler<String> send = http.send(HttpMethod.POST, url, pa, new RequestCallBack<String>() {
            @Override
            public void onFailure(HttpException arg0, String arg1) {
                // TODO Auto-generated method stub
                Log.d(TAG, arg1);
                Log.d(TAG, arg0.toString());
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                StringBuffer buffer = new StringBuffer(result);
                buffer.replace(0, 43, "");
                int start = buffer.indexOf("}");
                int end = buffer.length();
                buffer.delete(start + 1, end);
                String result2 = buffer.toString();
                Log.d(TAG, result2);
                Gson gson = new Gson();
                HistoryBean bean = gson.fromJson(result2, HistoryBean.class);
                if(null != bean){
                    popupwindow_calendar_history.setText(bean.getDes()); 
                    Log.d(TAG, bean.toString());
                }


            }

        });


    }

}
