package com.example.user.isby_order.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.user.isby_order.Adapter.Order_History_List_Adapter;
import com.example.user.isby_order.Activity.MainActivity;
import com.example.user.isby_order.Activity_Popup.Pop_Check_Order;
import com.example.user.isby_order.R;
import com.example.user.isby_order.publicClass.*;
import com.example.user.isby_order.decorators.*;
import com.prolificinteractive.materialcalendarview.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executors;


public class Order_History_Fragment extends Fragment {

    public String year;
    public String month;
    public String day;
    public String selectDate;

    public ArrayList<String> timeArray;
    public ArrayList<Integer> timetempArray;

    private Order_History_List_Adapter adapter;
    private List<OrderHistory> historyList;
    public MaterialCalendarView calendar;


    private boolean nullCheck = false;


    class historyViewHolder {

        public ListView historyListView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_history, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



        final historyViewHolder viewHolder = new historyViewHolder();
        calendar = (MaterialCalendarView) getView().findViewById(R.id.calendar);
        viewHolder.historyListView = (ListView) getView().findViewById(R.id.historyListView);

        historyList = new ArrayList<>();
        timeArray = new ArrayList<>();
        timetempArray = new ArrayList<>();
        adapter = new Order_History_List_Adapter(getContext().getApplicationContext(), historyList, this);

        viewHolder.historyListView.setAdapter(adapter);



        calendar.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2018, 0, 1))
                .setMaximumDate(CalendarDay.from(2030, 11, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        calendar.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                new OneDayDecorator());



        calendar.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                //Toast.makeText(getContext(), date.getYear()+ "년 " + (date.getMonth()+1) + "월 " + date.getDay() + "일" , Toast.LENGTH_SHORT).show();
                year = String.valueOf(date.getYear());
                if (String.valueOf(date.getMonth() + 1).length() == 1) {
                    month = "0" + String.valueOf(date.getMonth() + 1);
                } else {
                    month = String.valueOf(date.getMonth() + 1);
                }
                if (String.valueOf(date.getDay()).length() == 1)
                    day = "0" + String.valueOf(date.getDay());
                else {
                    day = String.valueOf(date.getDay());
                }

                selectDate = year + month + day;
                new BackGroundTask(0).execute();
            }
        });



        viewHolder.historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //변경 팝업창이 나와야함.
                Intent intent_1 = new Intent(getContext(), Pop_Check_Order.class);
                intent_1.putExtra("goOrderArray", historyList.get(position).getOrderList());
                intent_1.putExtra("pageValue" , 0);
                startActivity(intent_1);
            }
        });

        new BackGroundTask(1).execute();
        new ApiSimulator(timeArray).executeOnExecutor(Executors.newSingleThreadExecutor());

        setListViewHeightBasedOnItems(viewHolder.historyListView);
    }

    public void setListViewHeightBasedOnItems(ListView listView) {

        // Get list adpter of listview;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) return;

        int numberOfItems = listAdapter.getCount();

        // Get total height of all items.
        int totalItemsHeight = 0;
        for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
            View item = listAdapter.getView(itemPos, null, listView);
            item.measure(0, 0);
            totalItemsHeight += item.getMeasuredHeight() + 10;
        }

        // Get total height of all item dividers.
        int totalDividersHeight = listView.getDividerHeight() * (numberOfItems - 1);

        // Set list height.
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalItemsHeight + totalDividersHeight;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    class BackGroundTask extends AsyncTask<Void, Void, String> {
        String target;
        int num;

        public BackGroundTask(int num) {
            this.num = num;
        }

        @Override
        protected void onPreExecute() {
            try {
                switch (num) {
                    case 0:
                        target = "http://kjg123kg.cafe24.com/ISBY_OrderHistory_Request.php?Time=" + URLEncoder.encode(selectDate, "UTF-8");
                        break;
                    case 1:
                        target = "http://kjg123kg.cafe24.com/ISBY_OrderTime_Request.php";
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                while ((temp = bufferedReader.readLine()) != null) {
                    stringBuilder.append(temp + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return stringBuilder.toString().trim();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        public void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        public void onPostExecute(String result) {

            try {

                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                int count = 0;
                historyList.clear();
                timetempArray.clear();
                timeArray.clear();

                switch (num) {
                    case 0:
                        while (count < jsonArray.length()) {
                            JSONObject object = jsonArray.getJSONObject(count);

                            int orderID = object.getInt("orderID");
                            String time = object.getString("Time");
                            String orderMan = object.getString("OrderMan");
                            String orderList = object.getString("OrderList");
                            int approve = object.getInt("Approve");

                            String DOTWDate = time.substring(0, 4) + "년 " +
                                    time.substring(4, 6) + "월 " +
                                    time.substring(6, 8) + "일";

                            DateUtil dateUtil = new DateUtil();

                            try {
                                DOTWDate = dateUtil.getDateDay(DOTWDate, "yyyy년 MM월 dd일");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (approve == 0) {
                                historyList.add(new OrderHistory(orderID, time, DOTWDate, orderList, orderMan, "미승인"));
                            } else {
                                historyList.add(new OrderHistory(orderID, time, DOTWDate, orderList, orderMan, "승인"));
                            }

                            count++;
                        }
                        adapter.notifyDataSetChanged();
                        break;

                    case 1:
                        while (count < jsonArray.length()) {
                            JSONObject object = jsonArray.getJSONObject(count);
                            String time = object.getString("Time");

                            timeArray.add(time);

                            count++;
                        }

                        //중복제거
                        HashSet hs = new HashSet();
                        hs.addAll(timeArray);
                        timeArray.clear();
                        timeArray.addAll(hs);

                        for (int i = 0; i < timeArray.size(); i++) {
                            timetempArray.add(Integer.parseInt(timeArray.get(i)));
                        }
                        Ascending ascending = new Ascending();
                        Collections.sort(timetempArray, ascending);
                        timeArray.clear();
                        for (int i = 0; i < timetempArray.size(); i++) {
                            timeArray.add(timetempArray.get(i) + "");
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {

        List<String> Time_Result;

        ApiSimulator(List<String> Time_Result) {
            this.Time_Result = Time_Result;
        }

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            try {

                Thread.sleep(1000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            ArrayList<CalendarDay> dates = new ArrayList<>();
            CalendarDay day;

            int month, dayy, year = 0;

            /*특정날짜 달력에 점표시해주는곳*/
            /*월은 0이 1월 년,일은 그대로*/
            for (int i = 0; i < Time_Result.size(); i++) {
                day = CalendarDay.from(calendar);
                year = Integer.parseInt(Time_Result.get(i).substring(0, 4));

                if (Time_Result.get(i).charAt(4) == 0) {
                    month = Time_Result.get(i).charAt(5);
                } else {
                    month = Integer.parseInt(Time_Result.get(i).substring(4, 6));
                }
                if (Time_Result.get(i).charAt(6) == 0) {
                    dayy = Time_Result.get(i).charAt(7);
                } else {
                    dayy = Integer.parseInt(Time_Result.get(i).substring(6, 8));
                }

                calendar.set(year, month - 1, dayy);
                dates.add(day);
                nullCheck = true;
            }
            if (nullCheck) {
                day = CalendarDay.from(calendar);
                Integer.parseInt(Time_Result.get(Time_Result.size() - 1).substring(0, 4));

                if (Time_Result.get(Time_Result.size() - 1).charAt(4) == 0) {
                    month = Time_Result.get(Time_Result.size() - 1).charAt(5);
                } else {
                    month = Integer.parseInt(Time_Result.get(Time_Result.size() - 1).substring(4, 6));
                }
                if (Time_Result.get(Time_Result.size() - 1).charAt(6) == 0) {
                    dayy = Time_Result.get(Time_Result.size() - 1).charAt(7);
                } else {
                    dayy = Integer.parseInt(Time_Result.get(Time_Result.size() - 1).substring(6, 8));
                }
                calendar.set(year, month - 1, dayy);
                dates.add(day);
            }
            MainActivity.customProgressDialog.dismiss();
            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

            if (getActivity().isFinishing()) {
                return;
            }
            calendar.addDecorator(new EventDecorator(Color.RED, calendarDays, getActivity()));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        nullCheck = false;
    }


}
