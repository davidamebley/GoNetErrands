package ViewHolder;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thoughtbot.expandablerecyclerview.listeners.OnGroupClickListener;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import Model.RestaurantMainMenuItem;
import somame.amebleysystems.com.somame.R;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

/**
 * Created by Akwasi on ${9/27/2018}.
 */

public class RestaurantMainMenuItemViewHolder extends GroupViewHolder {
    private OnGroupClickListener listener;
    private TextView menuItemDesc;
    private ImageView arrow;
    Context context;
    LinearLayout linearLayout;


    public RestaurantMainMenuItemViewHolder(Context context, View itemView) {
        super(itemView);
        menuItemDesc = itemView.findViewById(R.id.textview_restaurant_menu_name);
        arrow = itemView.findViewById(R.id.imageview_restaurant_menu_item_arr);
//        linearLayout= itemView.findViewById(R.id.layout_restaurant_menu_item_arr);
        this.context = context;
    }

    public void bind(RestaurantMainMenuItem restaurantMainMenuItem){
        menuItemDesc.setText(restaurantMainMenuItem.getTitle());
    }

    @Override
    public void expand() {
        animateExpand();
//        Toast.makeText(context,"EXXXXXXXXXXPPPPAAANNNND",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void collapse() {
        animateCollapse();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
//        Toast.makeText(context,"COOONTEEEXXXXTT",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setOnGroupClickListener(OnGroupClickListener listener) {
        super.setOnGroupClickListener(listener);
        this.listener = listener;

    }


    private void animateExpand() {
        RotateAnimation rotateAnimation = new RotateAnimation(360,90, RELATIVE_TO_SELF,0.5f, RELATIVE_TO_SELF,0.5f);
        rotateAnimation.setDuration(300);
        rotateAnimation.setFillAfter(true);
        arrow.setAnimation(rotateAnimation);
//        linearLayout.setAnimation(rotateAnimation);
    }

    private void animateCollapse() {
        RotateAnimation rotateAnimation = new RotateAnimation(90,360, RELATIVE_TO_SELF,0.5f, RELATIVE_TO_SELF,0.5f);
        rotateAnimation.setDuration(300);
        rotateAnimation.setFillAfter(true);
        arrow.setAnimation(rotateAnimation);
//        arrow.setAnimation(rotateAnimation);
    }

}
