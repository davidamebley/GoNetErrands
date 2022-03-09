package ViewHolder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

import Model.RestaurantSubMenuItem;
import somame.amebleysystems.com.somame.R;

/**
 * Created by Akwasi on ${9/27/2018}.
 */

public class RestaurantSubMenuViewHolder extends ChildViewHolder {
    private TextView subMenuItemName;
    Context context;

    public RestaurantSubMenuViewHolder(View itemView, final Context context) {
        super(itemView);
        this.context = context;
        subMenuItemName = itemView.findViewById(R.id.textview_restaurant_submenu_name);
        //Set OnClick Listener for Sub menu items
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, "Child clicked",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void bind(RestaurantSubMenuItem restaurantSubMenuItem){
        subMenuItemName.setText(restaurantSubMenuItem.getSubMenuItemDesc());
    }

}
