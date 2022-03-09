package Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Akwasi on ${9/27/2018}.
 */

public class ProductWishListItem {
    @SerializedName("id")
    private int id;
    @SerializedName("product_id")
    private int productId;
    @SerializedName("product_subcat_id")
    private int subCatId;
    @SerializedName("customer_id")
    private String customerId;
    @SerializedName("unit_price")
    private String price;
    @SerializedName("product_name")
    private String name;
    @SerializedName("product_desc")
    private String desc;
    @SerializedName("product_manufacturer")
    private String manufacturer;
    @SerializedName("product_image")
    private String logo;

    public ProductWishListItem(int id, int productId, int subCatId, String customerId, String name, String price, String desc, String manufacturer, String logo) {
        this.id = id;
        this.productId = productId;
        this.customerId = customerId;
        this.price = price;
        this.name = name;
        this.manufacturer = manufacturer;
        this.logo = logo;
        this.desc = desc;
        this.subCatId = subCatId;
    }

    public int getSubCatId() {
        return subCatId;
    }

    public void setSubCatId(int subCatId) {
        this.subCatId = subCatId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
