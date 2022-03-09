package Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Akwasi on ${9/27/2018}.
 */

public class MyOrderDetailsItem {
    @SerializedName("product_id")
    String productId;
    @SerializedName("order_no")
    String orderNo;
    @SerializedName("product_name")
    String productName;
    @SerializedName("product_quantity")
    String productQuantity;
    @SerializedName("product_price")
    Double price;

    public MyOrderDetailsItem(String productId, String orderNo, String productName, Double price, String productQuantity) {
        this.productId = productId;
        this.orderNo = orderNo;
        this.productName = productName;
        this.productQuantity = productQuantity;
        this.price = price;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }
}
