package com.example.user.isby_order.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.user.isby_order.R;
import com.example.user.isby_order.publicClass.CustomBitmapPool;
import com.example.user.isby_order.publicClass.CustomProgressDialog;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class SelectStore extends AppCompatActivity {

    public static CustomProgressDialog customProgressDialog;

    //true = 이즈비 오스테리아 , false = 이즈비 포자
    public static boolean isby_Select = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_store);



        ImageView isby_Osteria = (ImageView) findViewById(R.id.isby_osteria);
        ImageView isby_Pocha = (ImageView) findViewById(R.id.isby_Pocha);

        Glide.with(SelectStore.this).load(R.drawable.isby_osteria).bitmapTransform(new CropCircleTransformation(new CustomBitmapPool())).into(isby_Osteria);
        Glide.with(SelectStore.this).load(R.drawable.isby_pocha).bitmapTransform(new CropCircleTransformation(new CustomBitmapPool())).into(isby_Pocha);

//        Glide.with(SelectStore.this).load(R.drawable.isby_osteria)
//                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
//                .into(isby_Osteria);
//
//        Glide.with(SelectStore.this).load(R.drawable.isby_pocha)
//                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
//                .into(isby_Pocha);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.isby_osteria:
                        isby_Select = true;
                        customProgressDialog = new CustomProgressDialog(SelectStore.this, "ISBY Osteria로 이동하는중...");
                        customProgressDialog.getWindow().setBackgroundDrawable(null);
                        customProgressDialog.show();
                        Intent intent = new Intent(SelectStore.this, MainActivity.class);    //LoginActivity에서 RegisterActivity로 화면전환
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.isby_Pocha:
                        isby_Select = false;
                        Toast.makeText(SelectStore.this, "미정", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        isby_Osteria.setOnClickListener(listener);
        isby_Pocha.setOnClickListener(listener);

        if(MainActivity.checkDialog)
            customProgressDialog.dismiss();
    }
}
