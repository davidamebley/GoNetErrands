package Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Akwasi on 9/23/2018.
 */

public class ProductCat {
    @SerializedName("cat_id")
    private int prodCatId;
    @SerializedName("cat_desc")
    private String prodCatDesc;
    @SerializedName("cat_image")
    private String prodCatImage;

    public ProductCat(int prodCatId, String prodCatDesc, String prodCatImage) {
        this.prodCatId = prodCatId;
        this.prodCatDesc = prodCatDesc;
        this.prodCatImage = prodCatImage;
    }

    public ProductCat(int prodCatId, String prodCatDesc) {
        this.prodCatId = prodCatId;
        this.prodCatDesc = prodCatDesc;
    }

    public int getProdCatId() {
        return prodCatId;
    }

    public void setProdCatId(int prodCatId) {
        this.prodCatId = prodCatId;
    }

    public String getProdCatDesc() {
        return prodCatDesc;
    }

    public void setProdCatDesc(String prodCatDesc) {
        this.prodCatDesc = prodCatDesc;
    }

    public String getProdCatImage() {
        return prodCatImage;
    }

    public void setProdCatImage(String prodCatImage) {
        this.prodCatImage = prodCatImage;
    }
}
