package com.example.user.isby_order.RequestClass;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class Order_History_Supervision_Request extends StringRequest {
   // final static private String URL = "http://kjg123kg.cafe24.com/ISBY_OrderHistory_Register.php";
    private Map<String , String> parameters;

    public Order_History_Supervision_Request(String URL , String time, String orderMan, String orderList , String approve , Response.Listener<String> listener){
        super(Method.POST , URL , listener , null);
        parameters = new HashMap<>();
        parameters.put("Time" , time);
        parameters.put("OrderMan" , orderMan);
        parameters.put("OrderList" , orderList);
        parameters.put("Approve" , approve);
    }
    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
