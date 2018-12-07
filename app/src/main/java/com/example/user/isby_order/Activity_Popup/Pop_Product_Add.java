package com.example.user.isby_order.Activity_Popup;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.user.isby_order.R;
import com.example.user.isby_order.RequestClass.Order_Product_Supervision_Request;

import org.json.JSONObject;

public class Pop_Product_Add extends Activity {

    class Product_Add_ViewHolder {
        public EditText productName;
        public EditText productPrice;
        public Button ok;
        public Button cancel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pop_product_add);

        final Product_Add_ViewHolder viewHolder = new Product_Add_ViewHolder();
        viewHolder.productName = (EditText) findViewById(R.id.productName);
        viewHolder.productPrice = (EditText) findViewById(R.id.productPrice);
        viewHolder.ok = (Button) findViewById(R.id.ok);
        viewHolder.cancel = (Button) findViewById(R.id.cancel);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.ok:
                        if (!viewHolder.productName.getText().toString().equals("") && !viewHolder.productPrice.getText().toString().equals("")) {
                            Response.Listener<String> responseListener = new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonResponse = new JSONObject(response);
                                        boolean success = jsonResponse.getBoolean("success");
                                        if (success) {
                                            Toast.makeText(Pop_Product_Add.this, "물품 등록 성공", Toast.LENGTH_SHORT).show();
                                           // ((MainActivity) MainActivity.CONTEXT).recreate();
//                                            Order_Go_Fragment order_go_fragment = new Order_Go_Fragment();
//                                            Order_Go_Fragment.BackGroundTask backGroundTask = order_go_fragment.new BackGroundTask();
//                                            backGroundTask.execute();
                                            finish();
                                        } else {
                                            Toast.makeText(Pop_Product_Add.this, "물품 등록 실패", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            Order_Product_Supervision_Request product_add_request = new Order_Product_Supervision_Request("http://kjg123kg.cafe24.com/ISBY_ProductList_Register.php" , viewHolder.productName.getText().toString(), viewHolder.productPrice.getText().toString(), responseListener);
                            RequestQueue queue = Volley.newRequestQueue(Pop_Product_Add.this);
                            queue.add(product_add_request);
                        } else {
                            Toast.makeText(Pop_Product_Add.this, "빈칸을 채워주세요.", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.cancel:
                        finish();
                        break;
                }
            }
        };
        viewHolder.ok.setOnClickListener(listener);
        viewHolder.cancel.setOnClickListener(listener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
}
