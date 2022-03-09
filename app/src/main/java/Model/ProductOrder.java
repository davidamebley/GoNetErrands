package Model;

import java.util.List;

/**
 * Created by Akwasi on ${9/27/2018}.
 */

public class ProductOrder {
    private String customerId,firstName, lastName, phone, address, ghPostAddress, deliveryLocation, deliveryPeriod, paymentMethod,
            momoNumber;
    private String priceSubtotal, priceTotal, priceDelivery;
    private List<ProductOrderItem> productOrderList;

    public ProductOrder(String customerId, String firstName, String lastName, String phone, String address, String ghPostAddress,
                        String deliveryLocation, String deliveryPeriod, String paymentMethod,
                        String momoNumber, String priceSubtotal, String priceTotal,
                        String priceDelivery, List<ProductOrderItem> productOrderList) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.address = address;
        this.ghPostAddress = ghPostAddress;
        this.deliveryLocation = deliveryLocation;
        this.deliveryPeriod = deliveryPeriod;
        this.paymentMethod = paymentMethod;
        this.momoNumber = momoNumber;
        this.priceSubtotal = priceSubtotal;
        this.priceTotal = priceTotal;
        this.priceDelivery = priceDelivery;
        this.productOrderList = productOrderList;
    }

}
