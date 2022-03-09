package Model;

import android.os.Parcel;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

/**
 * Created by Akwasi on ${9/27/2018}.
 */

public class RestaurantMainMenuItem extends ExpandableGroup<RestaurantSubMenuItem> {
private String menuItemDesc, menuItemId, restaurantId, menuItemPhoto;

    public RestaurantMainMenuItem(String title, List<RestaurantSubMenuItem> items) {
        super(title, items);
    }


    protected RestaurantMainMenuItem(Parcel in) {
        super(in);
    }
}
