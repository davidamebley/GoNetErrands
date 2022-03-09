package Model;

/**
 * Created by Akwasi on ${9/27/2018}.
 */

public class ProductOrderItem {
    private int itemId;
    private String productName;
    private String price;
    private String customerId;
    private String comment;
    private String deliveryOption;
    private int quantity;

    public ProductOrderItem(int itemId, String customerId, String name, String price, int quantity) {
        this.itemId = itemId;
        this.productName = name;
        this.price = price;
        this.customerId = customerId;
        this.quantity = quantity;
    }

    public ProductOrderItem(int itemId, String customerId, String price, int quantity) {
        this.itemId = itemId;
        this.price = price;
        this.customerId = customerId;
        this.quantity = quantity;
    }

    public ProductOrderItem(int itemId, String customerId) {
        this.itemId = itemId;
        this.customerId = customerId;
        this.comment = comment;
        this.deliveryOption = deliveryOption;
    }

    public ProductOrderItem(int itemId, String customerId, int quantity) {
        this.itemId = itemId;
        this.customerId = customerId;
        this.quantity = quantity;
    }

    public ProductOrderItem(int itemId, int quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }
}
