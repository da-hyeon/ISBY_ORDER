package com.example.user.isby_order.publicClass;

public class OrderHistory {
    int orderID;
    String date;
    String dotw;
    String orderList;
    String orderMan;
    String orderApprove;

    public OrderHistory(int orderID , String date, String dotw, String orderList,  String orderMan, String orderApprove) {
        this.orderID = orderID;
        this.date = date;
        this.dotw = dotw;
        this.orderList = orderList;
        this.orderMan = orderMan;
        this.orderApprove = orderApprove;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDotw() {
        return dotw;
    }

    public void setDotw(String dotw) {
        this.dotw = dotw;
    }

    public String getOrderList() {
        return orderList;
    }

    public void setOrderList(String orderList) {
        this.orderList = orderList;
    }

    public String getOrderMan() {
        return orderMan;
    }

    public void setOrderMan(String orderMan) {
        this.orderMan = orderMan;
    }

    public String getOrderApprove() {
        return orderApprove;
    }

    public void setOrderApprove(String orderApprove) {
        this.orderApprove = orderApprove;
    }
}
