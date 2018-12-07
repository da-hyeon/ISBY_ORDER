package com.example.user.isby_order.publicClass;

import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.TextView;

import com.example.user.isby_order.R;

public class CustomProgressDialog extends Dialog {

    private TextView textView;

    public CustomProgressDialog(Context context , String text) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 지저분한(?) 다이얼 로그 제목을 날림
        setContentView(R.layout.custom_dialog); // 다이얼로그에 박을 레이아웃

        textView = (TextView) findViewById(R.id.titleText);
        textView.setText(text);
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
