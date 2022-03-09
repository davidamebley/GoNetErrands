package Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Akwasi on ${9/27/2018}.
 */

public class Restaurant {
    @SerializedName("restaurant_id")
    private int id;
    @SerializedName("restaurant_name")
    private String name;
    @SerializedName("restaurant_location")
    private String location;
    @SerializedName("start_time")
    private String startTime;
    @SerializedName("closing_time")
    private String closingTime;
    @SerializedName("restaurant_logo")
    private String logo;

    public Restaurant(int id, String name, String location, String startTime, String closingTime, String logo) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.startTime = startTime;
        this.closingTime = closingTime;
        this.logo = logo;
    }

    public Restaurant(int id, String name, String startTime, String closingTime) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.startTime = startTime;
        this.closingTime = closingTime;
    }

    public Restaurant(int id, String name, String startTime, String closingTime, String logo) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.closingTime = closingTime;
        this.logo = logo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(String closingTime) {
        this.closingTime = closingTime;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
