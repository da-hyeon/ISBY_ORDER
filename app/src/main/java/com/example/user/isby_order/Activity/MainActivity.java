package com.example.user.isby_order.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.user.isby_order.Fragment.Order_Go_Fragment;
import com.example.user.isby_order.Fragment.Order_History_Fragment;
import com.example.user.isby_order.R;
import com.example.user.isby_order.publicClass.CustomProgressDialog;
import com.example.user.isby_order.publicClass.DateUtil;
import com.example.user.isby_order.publicClass.Product;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static boolean checkDialog;

    public static Context CONTEXT;
    public static CustomProgressDialog customProgressDialog;
    
    public FragmentManager manager;

    MainViewHolder viewHolder = new MainViewHolder();

    class MainViewHolder {
        public TextView yearText;
        public TextView monthText;
        public TextView dayText;
        public TextView dotwText;

        public TextView orderList;

        public LinearLayout view_main;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        checkDialog = false;
        CONTEXT = this;

        manager = getSupportFragmentManager();
        
        viewHolder.yearText = (TextView) findViewById(R.id.yearText) ;
        viewHolder.monthText = (TextView) findViewById(R.id.monthText) ;
        viewHolder.dayText = (TextView) findViewById(R.id.dayText) ;
        viewHolder.dotwText = (TextView) findViewById(R.id.dotwText) ;
        viewHolder.orderList =  (TextView) findViewById(R.id.orderCheck) ;

        viewHolder.view_main  = (LinearLayout) findViewById(R.id.view_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //new BackGroundTask().execute();
        SelectStore.customProgressDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        viewHolder.view_main.setVisibility(View.VISIBLE);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.order_go) {
            customProgressDialog = new CustomProgressDialog(this, "발주 품목을 불러오는중...");
            customProgressDialog.getWindow().setBackgroundDrawable(null);
            customProgressDialog.show();

            viewHolder.view_main.setVisibility(View.GONE);
            manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            manager.beginTransaction().replace(R.id.content_main, new Order_Go_Fragment()).addToBackStack(null).commit();
        } else if (id == R.id.order_before) {
            customProgressDialog = new CustomProgressDialog(this, "발주 내역을 불러오는중...");
            customProgressDialog.getWindow().setBackgroundDrawable(null);
            customProgressDialog.show();
            viewHolder.view_main.setVisibility(View.GONE);
            manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            manager.beginTransaction().replace(R.id.content_main, new Order_History_Fragment()).addToBackStack(null).commit();
        } else if (id == R.id.back) {
            SelectStore.customProgressDialog = new CustomProgressDialog(this, "가게선택 메뉴로 이동하는중...");
            SelectStore.customProgressDialog.getWindow().setBackgroundDrawable(null);
            SelectStore.customProgressDialog.show();
            checkDialog = true;
            Intent intent = new Intent(MainActivity.this, SelectStore.class);    //LoginActivity에서 RegisterActivity로 화면전환
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);


        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkDialog = false;
        new BackGroundTask().execute();
    }

    public class BackGroundTask extends AsyncTask<Void, Void, String> {
        String target;

        @Override
        protected void onPreExecute() {
            try {
                target = "http://kjg123kg.cafe24.com/ISBY_OrderList_Lately_Request.php";
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

                while (count < jsonArray.length()) {

                    JSONObject object = jsonArray.getJSONObject(count);

                    String time = object.getString("Time");
                    String orderList = object.getString("OrderList");

                    String DOTWDate = time.substring(0, 4) + "년 " +
                            time.substring(4, 6) + "월 " +
                            time.substring(6, 8) + "일";

                    DateUtil dateUtil = new DateUtil();

                    try {
                        DOTWDate = dateUtil.getDateDay(DOTWDate, "yyyy년 MM월 dd일");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    viewHolder.yearText.setText(time.substring(0,4)+"년");
                    viewHolder.monthText.setText(time.substring(4,6)+"월");
                    viewHolder.dayText.setText(time.substring(6,8)+"일");
                    viewHolder.dotwText.setText(DOTWDate);
                    viewHolder.orderList.setText(orderList);

                    count++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
