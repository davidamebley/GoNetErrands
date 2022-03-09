package Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Akwasi on ${9/27/2018}.
 */

public class Product {
    @SerializedName("product_id")
    private int productId;
    @SerializedName("product_subcat_id")
    private int subCatId;
    @SerializedName("product_name")
    private String productName;
    @SerializedName("unit_price")
    private String unitPrice;
    @SerializedName("product_desc")
    private String productDesc;
    @SerializedName("product_manufacturer")
    private String productManufacturer;
    @SerializedName("product_image")
    private String productImage;

    public Product(int productId, int subCatId, String productName, String unitPrice, String productDesc, String productManufacturer, String productImage) {
        this.productId = productId;
        this.subCatId = subCatId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.productDesc = productDesc;
        this.productManufacturer = productManufacturer;
        this.productImage = productImage;
    }

    public Product(int productId, String productName, String unitPrice, String productDesc, String productManufacturer, String productImage) {
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.productDesc = productDesc;
        this.productManufacturer = productManufacturer;
        this.productImage = productImage;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getSubCatId() {
        return subCatId;
    }

    public void setSubCatId(int subCatId) {
        this.subCatId = subCatId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public String getProductManufacturer() {
        return productManufacturer;
    }

    public void setProductManufacturer(String productManufacturer) {
        this.productManufacturer = productManufacturer;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }
}
