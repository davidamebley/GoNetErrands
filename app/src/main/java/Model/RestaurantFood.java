package Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Akwasi on ${9/27/2018}.
 */

public class RestaurantFood {
    @SerializedName("food_id")
    private int foodId;
    @SerializedName("submenu_id")
    private int subMenuId;
    @SerializedName("food_name")
    private String foodName;
    @SerializedName("food_price")
    private String foodPrice;
    @SerializedName("food_desc")
    private String foodDesc;
    @SerializedName("food_photo")
    private String foodPhoto;

    public RestaurantFood(int foodId, String foodName, String foodPrice, String foodPhoto) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.foodPhoto = foodPhoto;
    }

    public RestaurantFood(int foodId, int subMenuId, String foodName, String foodPrice, String foodPhoto) {
        this.foodId = foodId;
        this.subMenuId = subMenuId;
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.foodPhoto = foodPhoto;
    }


    public RestaurantFood(int foodId, int subMenuId, String foodName, String foodPrice, String foodDesc, String foodPhoto) {
        this.foodId = foodId;
        this.subMenuId = subMenuId;
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.foodDesc = foodDesc;
        this.foodPhoto = foodPhoto;
    }

    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public int getSubMenuId() {
        return subMenuId;
    }

    public void setSubMenuId(int subMenuId) {
        this.subMenuId = subMenuId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(String foodPrice) {
        this.foodPrice = foodPrice;
    }

    public String getFoodDesc() {
        return foodDesc;
    }

    public void setFoodDesc(String foodDesc) {
        this.foodDesc = foodDesc;
    }

    public String getFoodPhoto() {
        return foodPhoto;
    }

    public void setFoodPhoto(String foodPhoto) {
        this.foodPhoto = foodPhoto;
    }
}
