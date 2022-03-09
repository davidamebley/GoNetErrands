package Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Akwasi on ${9/27/2018}.
 */
public class DeliveryLocation {
    @SerializedName("location_id")
    private int locationId;
    @SerializedName("location_name")
    private String locationName;
    @SerializedName("charge_amount")
    private String deliveryCharge;

    public DeliveryLocation(int locationId, String locationName, String deliveryCharge) {
        this.locationId = locationId;
        this.locationName = locationName;
        this.deliveryCharge = deliveryCharge;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(String deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }
}
