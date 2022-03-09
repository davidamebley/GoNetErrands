package Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Akwasi on ${9/27/2018}.
 */

public class MyOrder {
    @SerializedName("order_no")
    String orderNo;
    @SerializedName("customer_name")
    String customerName;
    @SerializedName("momo_number")
    String momoNumber;
    @SerializedName("order_status")
    String orderStatus;
    @SerializedName("payment_status")
    String paymentStatus;
    @SerializedName("order_created_on")
    String orderDate;
    @SerializedName("order_total")
    Double orderTotal;

    public MyOrder(String orderNo, String customerName, String momoNumber, String orderStatus, String paymentStatus, String orderDate, Double orderTotal) {
        this.orderNo = orderNo;
        this.customerName = customerName;
        this.momoNumber = momoNumber;
        this.orderStatus = orderStatus;
        this.paymentStatus = paymentStatus;
        this.orderDate = orderDate;
        this.orderTotal = orderTotal;
    }

    public MyOrder(String orderNo, String customerName, String momoNumber, Double orderTotal, String orderStatus, String paymentStatus) {
        this.orderNo = orderNo;
        this.orderTotal = orderTotal;
        this.orderStatus = orderStatus;
        this.paymentStatus = paymentStatus;
        this.customerName = customerName;
        this.momoNumber = momoNumber;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getMomoNumber() {
        return momoNumber;
    }

    public void setMomoNumber(String momoNumber) {
        this.momoNumber = momoNumber;
    }

    public Double getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(Double orderTotal) {
        this.orderTotal = orderTotal;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
