package Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.listeners.OnGroupClickListener;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

import Model.Restaurant;
import Model.RestaurantMainMenuItem;
import Model.RestaurantSubMenuItem;
import ViewHolder.RestaurantMainMenuItemViewHolder;
import ViewHolder.RestaurantSubMenuViewHolder;
import somame.amebleysystems.com.somame.R;
import somame.amebleysystems.com.somame.RestaurantMenuActivity;
import somame.amebleysystems.com.somame.RestaurantSubmenuActivity;

/**
 * Created by Akwasi on ${9/27/2018}.
 */

public class RestaurantSubMenuItemAdapter extends ExpandableRecyclerViewAdapter<RestaurantMainMenuItemViewHolder, RestaurantSubMenuViewHolder> {
    private Context context;
    private final int requestCode=1;
    public RestaurantSubMenuItemAdapter(Context context, List<? extends ExpandableGroup> groups) {
        super(groups);
        this.context = context;
    }

    @Override
    public RestaurantMainMenuItemViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_menu_item, parent, false);
        return new RestaurantMainMenuItemViewHolder(context,view);
    }

    @Override
    public RestaurantSubMenuViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_submenu_item, parent, false);
        return new RestaurantSubMenuViewHolder(view, context);
    }

    @Override
    public void onBindChildViewHolder(RestaurantSubMenuViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        final RestaurantSubMenuItem restaurantSubMenuItem = (RestaurantSubMenuItem) group.getItems().get(childIndex);
        holder.bind(restaurantSubMenuItem);

        //Set OnCLick Listener for Submenu Item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context,restaurantSubMenuItem.getSubMenuItemDesc(),Toast.LENGTH_SHORT).show();

                Intent restaurantSubMenuIntent = new Intent(context, RestaurantSubmenuActivity.class);

//            We use the 'GetId' method to get the Submenu Id as a KEY value and send along
                restaurantSubMenuIntent.putExtra("submenuId", String.valueOf(restaurantSubMenuItem.getSubMenuItemId()));
                restaurantSubMenuIntent.putExtra("submenuName", restaurantSubMenuItem.getSubMenuItemDesc());
                ((Activity)context).startActivityForResult(restaurantSubMenuIntent,requestCode);
            }
        });
    }

    @Override
    public void onBindGroupViewHolder(RestaurantMainMenuItemViewHolder holder, int flatPosition, ExpandableGroup group) {
        final RestaurantMainMenuItem restaurantMainMenuItem = (RestaurantMainMenuItem) group;
        holder.bind(restaurantMainMenuItem);
    }

}
