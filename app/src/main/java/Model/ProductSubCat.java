package Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Akwasi on ${9/27/2018}.
 */

public class ProductSubCat {
    @SerializedName("subcat_id")
    private int subCatId;
    @SerializedName("cat_id")
    private int catId;
    @SerializedName("subcat_desc")
    private String subCatName;
    @SerializedName("subcat_image")
    private String subCatImage;

    public ProductSubCat(int subCatId, int catId, String subCatName, String subCatImage) {
        this.subCatId = subCatId;
        this.catId = catId;
        this.subCatName = subCatName;
        this.subCatImage = subCatImage;
    }

    public ProductSubCat(int subCatId, String subCatName, String subCatImage) {
        this.subCatId = subCatId;
        this.subCatName = subCatName;
        this.subCatImage = subCatImage;
    }

    public int getSubCatId() {
        return subCatId;
    }

    public void setSubCatId(int subCatId) {
        this.subCatId = subCatId;
    }

    public int getCatId() {
        return catId;
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }

    public String getSubCatName() {
        return subCatName;
    }

    public void setSubCatName(String subCatName) {
        this.subCatName = subCatName;
    }

    public String getSubCatImage() {
        return subCatImage;
    }

    public void setSubCatImage(String subCatImage) {
        this.subCatImage = subCatImage;
    }
}
