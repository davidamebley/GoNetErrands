package Adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import Interface.OnBottomReachedListener;
import Interface.OnMiddleReachedListener;
import Interface.OnTopReachedListener;
import Model.CurrentRestaurantClass;
import Model.CurrentRestaurantFood;
import Model.ProductCat;
import Model.RestaurantFood;
import somame.amebleysystems.com.somame.R;
import somame.amebleysystems.com.somame.RestaurantFoodDetailActivity;
import somame.amebleysystems.com.somame.RestaurantSubmenuActivity;

/**
 * Created by Akwasi on ${9/27/2018}.
 */

public class RestaurantSubmenuAdapter extends RecyclerView.Adapter<RestaurantSubmenuAdapter.ViewHolder> {
    //Listener For Bottom Scroll End Check
    private OnBottomReachedListener onBottomReachedListener;
    private OnTopReachedListener onTopReachedListener;
    private OnMiddleReachedListener onMiddleReachedListener;
    private Context context;
    private List<RestaurantFood> restaurantFoodList;

    public RestaurantSubmenuAdapter(Context context, List<RestaurantFood> restaurantFoodList) {
        this.context = context;
        this.restaurantFoodList = restaurantFoodList;
    }

    @NonNull
    @Override
    public RestaurantSubmenuAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.restaurant_food_item,parent,false);
        return new RestaurantSubmenuAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantSubmenuAdapter.ViewHolder holder, int position) {
        RestaurantFood restaurantFood = restaurantFoodList.get(position);
        holder.textViewFoodName.setText(restaurantFood.getFoodName());
        //Setting Currency for Price Text
        Locale locale = new Locale("en", "GH");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        holder.textViewFoodPrice.setText(fmt.format(Double.parseDouble(restaurantFood.getFoodPrice())));

//        holder.textViewFoodPrice.setText(restaurantFood.getFoodPrice());

        RequestOptions requestOptions = new RequestOptions();
        //loading the image
        Glide.with(context)
                .load(restaurantFood.getFoodPhoto())
                .apply(requestOptions)
                .into(holder.imageViewFoodImage);

        //Bottom Reached Operations
//        if (position == restaurantFoodList.size() - 1){
//
//            onBottomReachedListener.onBottomReached(position);
//
//        }

        //Top Reached Operations
        if (position == restaurantFoodList.size()-restaurantFoodList.size()){
//            if (position < restaurantFoodList.size()/2){

            onTopReachedListener.onTopReached(position);

        }

        //Middle Reached Operations
        if (position > restaurantFoodList.size()/2){

            onMiddleReachedListener.onMiddleReached(position);

        }
    }

    @Override
    public int getItemCount() {
        return restaurantFoodList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private AppCompatTextView textViewFoodName;
        private AppCompatTextView textViewFoodPrice;
        private ImageView imageViewFoodImage;
        public ViewHolder(View itemView) {
            super(itemView);
            textViewFoodName = itemView.findViewById(R.id.textview_restaurant_food_item_name);
            textViewFoodPrice = itemView.findViewById(R.id.textview_restaurant_food_item_price);
            imageViewFoodImage = itemView.findViewById(R.id.imageview_restaurant_food_item);
//            Set OnClickListener
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
//            Picking the Model item Product and setting its clickListener
            RestaurantFood restaurantFood = restaurantFoodList.get(getAdapterPosition());
            Intent foodDetailIntent = new Intent(context.getApplicationContext(), RestaurantFoodDetailActivity.class);
            foodDetailIntent.putExtra("foodId", CurrentRestaurantFood.foodId = restaurantFood.getFoodId());
            foodDetailIntent.putExtra("foodName", CurrentRestaurantFood.foodName = restaurantFood.getFoodName());
            foodDetailIntent.putExtra("foodPrice", CurrentRestaurantFood.foodPrice = restaurantFood.getFoodPrice());
            foodDetailIntent.putExtra("foodDesc", CurrentRestaurantFood.foodDesc = restaurantFood.getFoodDesc());
            foodDetailIntent.putExtra("foodPhoto", CurrentRestaurantFood.foodPhoto = restaurantFood.getFoodPhoto());
            foodDetailIntent.putExtra("currentRestaurant", CurrentRestaurantClass.restaurantName);
//            Toast.makeText(context,restaurantFood.getFoodName()+" clicked",Toast.LENGTH_SHORT).show();
            context.startActivity(foodDetailIntent);
        }

    }

    //Function for Setting Bottom Reached Listener
    public void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener){
        this.onBottomReachedListener = onBottomReachedListener;
    }

    //Function for Setting Top Reached Listener
    public void setOnTopReachedListener(OnTopReachedListener onTopReachedListener){
        this.onTopReachedListener = onTopReachedListener;
    }

    //Function for Setting Middle Reached Listener
    public void setOnMiddleReachedListener(OnMiddleReachedListener onMiddleReachedListener){
        this.onMiddleReachedListener = onMiddleReachedListener;
    }
}
