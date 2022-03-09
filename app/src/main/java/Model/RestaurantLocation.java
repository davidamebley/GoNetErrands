package Model;

/**
 * Created by Akwasi on ${9/27/2018}.
 */

public class RestaurantLocation {
    int id;
    private String locationName;

    public RestaurantLocation(int id, String locationName) {
        this.id = id;
        this.locationName = locationName;
    }

    public RestaurantLocation(int id) {
        this.id = id;
    }

    public RestaurantLocation(String locationName) {
        this.locationName = locationName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
}
