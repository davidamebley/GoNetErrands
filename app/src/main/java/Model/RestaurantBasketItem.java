package Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Akwasi on ${9/27/2018}.
 */

public class RestaurantBasketItem {
    @SerializedName("id")
        private int basketItemId;
    @SerializedName("item_id")
        private int productId;
    @SerializedName("food_name")
        private String name;
    @SerializedName("food_photo")
        private String logo;
    @SerializedName("price")
        private String price;
    @SerializedName("customer_id")
        private String customerId;
    @SerializedName("comment")
        private String comment;
    @SerializedName("food_desc")
    private String description;
    @SerializedName("restaurant_name")
    private String restaurantName;
    @SerializedName("quantity")
        private int quantity;

        public RestaurantBasketItem(int basketItemId, int productId, String customerId, String name, String price, String logo, int quantity, String comment) {
            this.basketItemId = basketItemId;
            this.productId = productId;
            this.name = name;
            this.logo = logo;
            this.price = price;
            this.customerId = customerId;
            this.comment = comment;
            this.quantity = quantity;
        }

        public RestaurantBasketItem(int basketItemId, int productId, String customerId, String name, String price, String logo, int quantity) {
            this.basketItemId = basketItemId;
            this.productId = productId;
            this.name = name;
            this.logo = logo;
            this.price = price;
            this.customerId = customerId;
//        this.comment = comment;
            this.quantity = quantity;
        }
    public RestaurantBasketItem(int basketItemId, int productId, String customerId, String name, String price, String logo, String description, String restaurantName, int quantity) {
        this.basketItemId = basketItemId;
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.restaurantName = restaurantName;
        this.logo = logo;
        this.price = price;
        this.customerId = customerId;
//        this.comment = comment;
        this.quantity = quantity;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getBasketItemId() {
            return basketItemId;
        }

        public void setBasketItemId(int basketItemId) {
            this.basketItemId = basketItemId;
        }

        public int getProductId() {
            return productId;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }

        public String getCustomerId() {
            return customerId;
        }

        public void setCustomerId(String customerId) {
            this.customerId = customerId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }

