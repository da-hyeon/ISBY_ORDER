package com.example.user.isby_order.Activity_Popup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.user.isby_order.Activity.SelectStore;
import com.example.user.isby_order.R;
import com.example.user.isby_order.RequestClass.Order_History_Supervision_Request;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.TextTemplate;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.helper.log.Logger;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Pop_Check_Order extends Activity {

    private Intent intent;
    private String goOrderArray;
    private int pageValue;
    private Calendar cal;

    private String year , month, day , date;

    class Product_Add_ViewHolder {
        public TextView title;
        public TextView orderCheck;
        public Button ok;
        public Button cancel;
        public LinearLayout okLayout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pop_check_order);
//키 해시 가져오는 부분
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.user.isby_order", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        cal = Calendar.getInstance();
        goOrderArray = "";
        //현재 년도, 월, 일
        date = cal.get ( cal.YEAR ) + "" + (cal.get ( cal.MONTH ) + 1) + "" + cal.get ( cal.DATE ) +"" ;

        year = String.valueOf(cal.get ( cal.YEAR ));
        if (String.valueOf(cal.get ( cal.MONTH ) + 1).length() == 1) {
            month = "0" + String.valueOf(cal.get ( cal.MONTH ) + 1);
        } else {
            month = String.valueOf(cal.get ( cal.MONTH ) + 1);
        }
        if (String.valueOf(cal.get ( cal.DATE )).length() == 1)
            day = "0" + String.valueOf(cal.get ( cal.DATE ));
        else {
            day = String.valueOf(cal.get ( cal.DATE ));
        }

        date = year + month + day;

        intent = getIntent();
        pageValue = intent.getIntExtra("pageValue" , 0);

        if(pageValue == 1) {
            if (SelectStore.isby_Select) {
                goOrderArray = "---ISBY OSTERIA --- \n\n";
            } else {
                goOrderArray = "---ISBY POCHA --- \n\n";
            }
        }

        goOrderArray += intent.getStringExtra("goOrderArray");


        final Product_Add_ViewHolder viewHolder = new Product_Add_ViewHolder();
        viewHolder.title = findViewById(R.id.title);
        viewHolder.orderCheck = (TextView) findViewById(R.id.orderCheck);
        viewHolder.ok = (Button) findViewById(R.id.ok);
        viewHolder.cancel = (Button) findViewById(R.id.cancel);
        viewHolder.okLayout = (LinearLayout) findViewById(R.id.okLayout);

        viewHolder.orderCheck.setText(goOrderArray);

        if(pageValue == 0){
            viewHolder.title.setText("발주 확인");
            viewHolder.okLayout.setVisibility(View.GONE);
            viewHolder.cancel.setText("확인");
        }

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.ok:
                        Log.d("StringLength " ,goOrderArray.length()+"" );
                        //발주기록에 저장

                        if(goOrderArray.length() <= 200) {

                            TextTemplate params = TextTemplate.newBuilder(goOrderArray, LinkObject.newBuilder().setWebUrl("https://developers.kakao.com").setMobileWebUrl("https://developers.kakao.com").build()).setButtonTitle("발주 완료").build();

                            Map<String, String> serverCallbackArgs = new HashMap<String, String>();
                            serverCallbackArgs.put("user_id", "${current_user_id}");
                            serverCallbackArgs.put("product_id", "${shared_product_id}");

                            KakaoLinkService.getInstance().sendDefault(Pop_Check_Order.this, params, serverCallbackArgs, new ResponseCallback<KakaoLinkResponse>() {
                                @Override
                                public void onFailure(ErrorResult errorResult) {
                                    Logger.e(errorResult.toString());
                                }

                                @Override
                                public void onSuccess(KakaoLinkResponse result) {
                                    // 템플릿 밸리데이션과 쿼터 체크가 성공적으로 끝남. 톡에서 정상적으로 보내졌는지 보장은 할 수 없다. 전송 성공 유무는 서버콜백 기능을 이용하여야 한다.
                                    AlertDialog.Builder builder = new AlertDialog.Builder(Pop_Check_Order.this);
                                    builder.setMessage("발주 완료!!")
                                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    OrderGo();
                                                }
                                            })
                                            .create();
                                    builder.show();
                                }
                            });
                            //finish();
                        } else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(Pop_Check_Order.this);
                            builder.setMessage("발주량이 많아 카카오톡으로 전송할 수 없습니다. \n 카카오톡에서 붙여넣기로 보내세요.")
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //OrderGo();
                                            finish();
                                        }
                                    })
                                    .create();
                            builder.show();
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

    @Override
    protected void onPause() {
        super.onPause();

    }

    public void OrderGo(){
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
        Order_History_Supervision_Request order_history_supervision_request = new Order_History_Supervision_Request("http://kjg123kg.cafe24.com/ISBY_OrderHistory_Register.php" , date, "황다현", goOrderArray, 0 +"", responseListener);
        RequestQueue queue = Volley.newRequestQueue(Pop_Check_Order.this);
        queue.add(order_history_supervision_request);

    }
}
