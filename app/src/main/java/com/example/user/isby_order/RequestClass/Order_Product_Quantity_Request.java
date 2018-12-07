package com.example.user.isby_order.RequestClass;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class Order_Product_Quantity_Request extends StringRequest {
    final static private String URL = "http://kjg123kg.cafe24.com/ISBY_ProductList_Quantity_Register.php";
    private Map<String , String> parameters;

    public Order_Product_Quantity_Request(String quantity, String productName ,  Response.Listener<String> listener){
        super(Method.POST , URL , listener , null);
        parameters = new HashMap<>();
        parameters.put("Quantity" , quantity);
        parameters.put("Product" , productName);
    }
    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
