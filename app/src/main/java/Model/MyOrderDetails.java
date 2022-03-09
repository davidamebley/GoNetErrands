package Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Akwasi on ${9/27/2018}.
 */

public class MyOrderDetails {
    @SerializedName("order_no")
    String orderNo;
    @SerializedName("customer_name")
    String customerName;
    @SerializedName("address")
    String address;
    @SerializedName("gh_post_address")
    String ghPostAddress;
    @SerializedName("delivery_location")
    String deliveryLocation;
    @SerializedName("delivery_period")
    String deliveryPeriod;
    @SerializedName("payment_method")
    String paymentMethod;
    @SerializedName("momo_number")
    String momoNumber;
    @SerializedName("payment_status")
    String paymentStatus;
    @SerializedName("order_status")
    String orderStatus;
    @SerializedName("subtotal")
    Double subtotal;
    @SerializedName("order_total")
    Double orderTotal;
//    @SerializedName("order_no")
    List itemList;

    public MyOrderDetails(String orderNo, String customerName, String address, String ghPostAddress, String deliveryLocation, String deliveryPeriod, String paymentMethod, String momoNumber, String paymentStatus, String orderStatus, Double subtotal, Double orderTotal) {
        this.orderNo = orderNo;
        this.customerName = customerName;
        this.address = address;
        this.ghPostAddress = ghPostAddress;
        this.deliveryLocation = deliveryLocation;
        this.deliveryPeriod = deliveryPeriod;
        this.paymentMethod = paymentMethod;
        this.momoNumber = momoNumber;
        this.paymentStatus = paymentStatus;
        this.orderStatus = orderStatus;
        this.subtotal = subtotal;
        this.orderTotal = orderTotal;
    }

    public MyOrderDetails(String orderNo, String customerName, String address, String ghPostAddress, String deliveryLocation, String deliveryPeriod, String paymentMethod, String momoNumber, String paymentStatus, String orderStatus, Double subtotal, Double orderTotal, List itemList) {
        this.orderNo = orderNo;
        this.customerName = customerName;
        this.address = address;
        this.ghPostAddress = ghPostAddress;
        this.deliveryLocation = deliveryLocation;
        this.deliveryPeriod = deliveryPeriod;
        this.paymentMethod = paymentMethod;
        this.momoNumber = momoNumber;
        this.paymentStatus = paymentStatus;
        this.orderStatus = orderStatus;
        this.subtotal = subtotal;
        this.orderTotal = orderTotal;
        this.itemList = itemList;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGhPostAddress() {
        return ghPostAddress;
    }

    public void setGhPostAddress(String ghPostAddress) {
        this.ghPostAddress = ghPostAddress;
    }

    public String getDeliveryLocation() {
        return deliveryLocation;
    }

    public void setDeliveryLocation(String deliveryLocation) {
        this.deliveryLocation = deliveryLocation;
    }

    public String getDeliveryPeriod() {
        return deliveryPeriod;
    }

    public void setDeliveryPeriod(String deliveryPeriod) {
        this.deliveryPeriod = deliveryPeriod;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getMomoNumber() {
        return momoNumber;
    }

    public void setMomoNumber(String momoNumber) {
        this.momoNumber = momoNumber;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public Double getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(Double orderTotal) {
        this.orderTotal = orderTotal;
    }
}
