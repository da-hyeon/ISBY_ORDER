package com.example.user.isby_order.Fragment;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.user.isby_order.Adapter.Order_Go_List_Adapter;
import com.example.user.isby_order.Activity.MainActivity;
import com.example.user.isby_order.Activity_Popup.Pop_Check_Order;
import com.example.user.isby_order.Activity_Popup.Pop_Product_Add;
import com.example.user.isby_order.Activity_Popup.Pop_Product_Change;
import com.example.user.isby_order.R;
import com.example.user.isby_order.RequestClass.Order_Product_Quantity_Request;
import com.example.user.isby_order.publicClass.Ascending;
import com.example.user.isby_order.publicClass.Product;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class Order_Go_Fragment extends Fragment {


    private Order_Go_List_Adapter adapter;
    private List<Product> productList;
    private List<Product> saveList;
    private String goOrderArray;

    class productViewHolder {
        public ListView product_ListView;
        public Button addProduct;
        public Button reset;
        public Button goOrder;
        public EditText searchProduct;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_go, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final productViewHolder viewHolder = new productViewHolder();
        viewHolder.product_ListView = (ListView) getView().findViewById(R.id.product_ListView);
        viewHolder.addProduct = (Button) getView().findViewById(R.id.addProduct);
        viewHolder.reset = (Button) getView().findViewById(R.id.reset);
        viewHolder.goOrder = (Button) getView().findViewById(R.id.goOrder);
        viewHolder.searchProduct = (EditText) getView().findViewById(R.id.searchProduct);

        productList = new ArrayList<>();
        saveList = new ArrayList<>();

        adapter = new Order_Go_List_Adapter(getContext().getApplicationContext(), productList , this);
        viewHolder.product_ListView.setAdapter(adapter);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.addProduct:
                        SaveData();
                        Intent intent = new Intent(getContext(), Pop_Product_Add.class);
                        startActivity(intent);
                        break;
                    case R.id.reset:
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("정말 리셋하시겠습니까?")
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                try {
                                                    JSONObject jsonResponse = new JSONObject(response);
                                                    boolean success = jsonResponse.getBoolean("success");
                                                    if (success) {
                                                    } else {
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        };
                                        for (int i = 0; i < productList.size(); i++) {
                                            if (productList.get(i).getQuantity() == 0)
                                                continue;
                                            Order_Product_Quantity_Request product_Quantity_request = new Order_Product_Quantity_Request(0+"", productList.get(i).getName(), responseListener);
                                            RequestQueue queue = Volley.newRequestQueue(getContext());
                                            queue.add(product_Quantity_request);
                                            productList.get(i).setQuantity(0);
                                        }
                                        adapter.changeCheckArray.clear();
                                        adapter.notifyDataSetChanged();
                                    }
                                })
                                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .create();
                        builder.show();
                        break;

                    case R.id.goOrder:
                        SaveData();
                        goOrderArray = "";

                        for (int i = 0; i < productList.size(); i++) {
                            if (productList.get(i).getQuantity() == 0)
                                continue;
                            goOrderArray += productList.get(i).getName() + " " + productList.get(i).getQuantity() + "\n";
                        }
                        if( !goOrderArray.equals("")) {
                            Intent intent_1 = new Intent(getContext(), Pop_Check_Order.class);
                            intent_1.putExtra("goOrderArray", goOrderArray);
                            intent_1.putExtra("pageValue", 1);
                            startActivity(intent_1);

                            ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(getContext().CLIPBOARD_SERVICE);
                            ClipData clipData = ClipData.newPlainText("label", goOrderArray);
                            clipboardManager.setPrimaryClip(clipData);
                            Toast.makeText(getContext(), "복사되었습니다.", Toast.LENGTH_SHORT).show();
                            //adapter.changeCheckArray.clear();
                        } else{
                            AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(getContext());
                            builder1.setMessage("발주 할 품목이 없습니다.")
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .create();
                            builder1.show();
                        }
                        break;
                }

            }
        };

        viewHolder.addProduct.setOnClickListener(listener);
        viewHolder.reset.setOnClickListener(listener);
        viewHolder.goOrder.setOnClickListener(listener);

        viewHolder.searchProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchProduct(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        viewHolder.product_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SaveData();
                //변경 팝업창이 나와야함.
                Intent intent = new Intent(getContext(), Pop_Product_Change.class);
                intent.putExtra("getName" , productList.get(position).getName());
                intent.putExtra("getPrice" , productList.get(position).getPrice()+"");
                startActivity(intent);
            }
        });


    }

    public void searchProduct(String search) {
        productList.clear();
        for (int i = 0; i < saveList.size(); i++) {
            if (saveList.get(i).getName().contains(search)) {
                productList.add(saveList.get(i));
            }
        }
        adapter.notifyDataSetChanged();
    }

    public class BackGroundTask extends AsyncTask<Void, Void, String> {
        String target;

        @Override
        protected void onPreExecute() {
            try {
                target = "http://kjg123kg.cafe24.com/ISBY_ProductList_Request.php";
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
                productList.clear();
                saveList.clear();
                int count = 0;
                while (count < jsonArray.length()) {

                    JSONObject object = jsonArray.getJSONObject(count);

                    String product = object.getString("Product");
                    int price = object.getInt("Price");
                    int quantity = object.getInt("Quantity");


                    productList.add(new Product(product, price, quantity));
                    saveList.add(new Product(product, price, quantity));
                    count++;
                }
                adapter.notifyDataSetChanged();
                MainActivity.customProgressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        new BackGroundTask().execute();
    }

    @Override
    public void onStop() {
        super.onStop();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {

                    } else {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        //중복제거
        HashSet hs = new HashSet();
        hs.addAll(adapter.changeCheckArray);
        adapter.changeCheckArray.clear();
        adapter.changeCheckArray.addAll(hs);
        hs.clear();

        //오름차순정렬
        Ascending ascending = new Ascending();
        Collections.sort(adapter.changeCheckArray, ascending);

        for (int i = 0; i < productList.size(); i++) {
            for (int j = 0; j < adapter.changeCheckArray.size(); j++) {
                if (i != adapter.changeCheckArray.get(j)) {
                    continue;
                }
                Order_Product_Quantity_Request product_Quantity_request = new Order_Product_Quantity_Request(productList.get(i).getQuantity() + "", productList.get(i).getName(), responseListener);
                RequestQueue queue = Volley.newRequestQueue(getContext());
                queue.add(product_Quantity_request);
            }
        }
        adapter.changeCheckArray.clear();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void SaveData(){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {

                    } else {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        //중복제거
        HashSet hs = new HashSet();
        hs.addAll(adapter.changeCheckArray);
        adapter.changeCheckArray.clear();
        adapter.changeCheckArray.addAll(hs);
        hs.clear();

        //오름차순정렬
        Ascending ascending = new Ascending();
        Collections.sort(adapter.changeCheckArray, ascending);

        for (int i = 0; i < productList.size(); i++) {
            for (int j = 0; j < adapter.changeCheckArray.size(); j++) {
                if (i != adapter.changeCheckArray.get(j)) {
                    continue;
                }
                Order_Product_Quantity_Request product_Quantity_request = new Order_Product_Quantity_Request(productList.get(i).getQuantity() + "", productList.get(i).getName(), responseListener);
                RequestQueue queue = Volley.newRequestQueue(getContext());
                queue.add(product_Quantity_request);
            }
        }
        adapter.changeCheckArray.clear();
    }
}
