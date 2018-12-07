package com.example.user.isby_order.RequestClass;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class Order_Product_Supervision_Request extends StringRequest {
    //final static private String URL = "http://kjg123kg.cafe24.com/ISBY_ProductList_Register.php";
    private Map<String , String> parameters;

    public Order_Product_Supervision_Request(String URL , String productName , String productPrice, Response.Listener<String> listener){
        super(Method.POST , URL , listener , null);
        parameters = new HashMap<>();
        parameters.put("Product" , productName);
        parameters.put("Price" , productPrice);
    }

    public Order_Product_Supervision_Request(String URL , String productName , Response.Listener<String> listener){
        super(Method.POST , URL , listener , null);
        parameters = new HashMap<>();
        parameters.put("Product" , productName);
    }

    public Order_Product_Supervision_Request(String URL , String productName , String productPrice ,  String productName1 , Response.Listener<String> listener){
        super(Method.POST , URL , listener , null);
        parameters = new HashMap<>();
        parameters.put("Product" , productName);
        parameters.put("Price" , productPrice);
        parameters.put("Product1" , productName1);
    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
