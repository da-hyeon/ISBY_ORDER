package com.example.user.isby_order.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.user.isby_order.R;
import com.example.user.isby_order.RequestClass.Order_Product_Supervision_Request;
import com.example.user.isby_order.publicClass.Product;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Order_Go_List_Adapter extends ArrayAdapter<Product> {

    private Context context;
    private List productList;
    private Fragment parent;
    public ArrayList<Integer> changeCheckArray;


    class productViewHolder {
        public TextView productName;
        public TextView quantity;
        public Button deleteObject;
        public Button subButton;
        public Button addButton;
        public int quantityValue[];
    }


    public Order_Go_List_Adapter(Context context, List<Product> productList , Fragment parent) {
        super(context, 0, productList);
        this.context = context;
        this.productList = productList;
        changeCheckArray = new ArrayList<>();
        this.parent = parent;
    }

    // ListView의  한 줄(row)이 렌더링(rendering)될 때 호출되는 메소드로 row를 위한 view를 리턴합니다.
    // 한 줄(Row)를 위한 뷰(View)를 재사용하여 ListIView의 효율성을 올립니다.
    @NonNull
    @Override
    public View getView(final int i,       // LIstView에 보여지게 되는 데이터인 product 객체 리스트의 인덱스
                        View convertView,   // 주어진 데이터를 보여주기 위해 사용될 한 줄(row)을 위한 뷰(View)
                        // 값이 null인 경우에만 새로 생성하고 그 외에는 재사용됩니다.
                        @NonNull ViewGroup parentViewGroup  // XML 레이아웃 파일을 View 객체로 변환하기 위해 사용되는 부모 뷰그룹
    ) {
        View rowView = convertView; // 코드 가독성을 위해서 rowView 변수를 사용합니다.
        final productViewHolder viewHolder;


        // 기존에 생성했던 뷰라면 재사용하고 그렇지 않으면 XML 파일을 새로 view 객체로 변환합니다.
        if (rowView == null) {
            rowView = LayoutInflater.from(getContext()).inflate(R.layout.listview_order_go, parentViewGroup, false);

            viewHolder = new productViewHolder();
            viewHolder.productName = (TextView) rowView.findViewById(R.id.productName );
            viewHolder.quantity = (TextView) rowView.findViewById(R.id.quantity );
            viewHolder.deleteObject = (Button) rowView.findViewById(R.id.deleteObject);
            viewHolder.subButton = (Button) rowView.findViewById(R.id.subButton);
            viewHolder.addButton = (Button) rowView.findViewById(R.id.addButton);
            viewHolder.quantityValue = new int[productList.size()];

            rowView.setTag(viewHolder);
        } else {
            viewHolder = (productViewHolder) rowView.getTag();
        }


        final Product product = (Product) productList.get(i);
        viewHolder.productName.setText(product.getName());
        viewHolder.quantity.setText(product.getQuantity() + "");

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.deleteObject:
                        AlertDialog.Builder builder = new AlertDialog.Builder(parent.getActivity());
                        builder.setTitle("주의");
                        builder.setMessage("정말 삭제하시겠습니까?");
                        builder.setPositiveButton("확인",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                try {
                                                    JSONObject jsonResponse = new JSONObject(response);
                                                    boolean success = jsonResponse.getBoolean("success");
                                                    if (success) {
                                                        Toast.makeText(getContext(), product.getName() + " 삭제 성공", Toast.LENGTH_SHORT).show();
                                                        productList.remove(i);
                                                        notifyDataSetChanged();
                                                    } else {
                                                        Toast.makeText(getContext(), product.getName() + " 삭제 실패", Toast.LENGTH_SHORT).show();
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        };
                                        Order_Product_Supervision_Request product_add_request = new Order_Product_Supervision_Request("http://kjg123kg.cafe24.com/ISBY_ProductList_Delete_Request.php" , product.getName() , responseListener);
                                        RequestQueue queue = Volley.newRequestQueue(getContext());
                                        queue.add(product_add_request);

                                    }
                                });
                        builder.setNegativeButton("취소",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                        builder.show();
                        break;
                    case R.id.subButton:
                        if(product.getQuantity() > 0) {
                            changeCheckArray.add(i);
                            product.setQuantity(product.getQuantity() - 1);
                            notifyDataSetChanged();
                        }
                        break;
                    case R.id.addButton:
                        changeCheckArray.add(i);
                        product.setQuantity(product.getQuantity() + 1);
                        notifyDataSetChanged();
                        break;
                }
            }
        };
        viewHolder.subButton.setOnClickListener(listener);
        viewHolder.addButton.setOnClickListener(listener);
        viewHolder.deleteObject.setOnClickListener(listener);

        return rowView;
    }
}
