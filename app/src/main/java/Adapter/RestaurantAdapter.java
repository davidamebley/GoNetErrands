package Adapter;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
//import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import Model.CurrentRestaurantClass;
import Model.ProductCat;
import Model.Restaurant;
import somame.amebleysystems.com.somame.R;
import somame.amebleysystems.com.somame.RestaurantMenuActivity;

/**
 * Created by Akwasi on ${9/27/2018}.
 */

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {
    private Context context;
    private List<Restaurant> restaurantList;

    public RestaurantAdapter(Context context, List<Restaurant> restaurantList) {
        this.context = context;
        this.restaurantList = restaurantList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.restaurant_item,parent,false);
        return new RestaurantAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Restaurant restaurant = restaurantList.get(position);
        StringBuilder start_end_time = new StringBuilder();
        holder.txtRestaurantName.setText(restaurant.getName());
        holder.txtStartClosingTime.setText(start_end_time.append(restaurant.getStartTime()).append(" ").append(context.getString(R.string.dash_separator)).append(" ").append(restaurant.getClosingTime()));
//        holder.txtClosingTime.setText(restaurant.getClosingTime());

        RequestOptions requestOptions = new RequestOptions();
        //loading the image
        Glide.with(context)
                .load(restaurant.getLogo())
                .apply(requestOptions)
                .into(holder.imageViewRestaurantLogo);
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private AppCompatTextView txtRestaurantName, txtStartClosingTime, txtClosingTime;
        private ImageView imageViewRestaurantLogo;
        public ViewHolder(View itemView) {
            super(itemView);
            txtRestaurantName = itemView.findViewById(R.id.textview_restaurant_name);
            txtStartClosingTime = itemView.findViewById(R.id.textview_restaurant_start_end_time);
//            txtClosingTime = itemView.findViewById(R.id.textview_restaurant_closing_time);
            imageViewRestaurantLogo = itemView.findViewById(R.id.imageview_restaurant_logo);
//            Set Onclick Listener
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
//            GET Restaurant ID and Send To New Activity
//            Toast.makeText(context,restaurantList.get(getAdapterPosition()).getName()+"Clicked",Toast.LENGTH_LONG).show();
            Intent restaurantMenuIntent = new Intent(context, RestaurantMenuActivity.class);
            Restaurant restaurant = restaurantList.get(getAdapterPosition());
//            We use the 'GetId' method to get the restaurant Id as a KEY value and send along
            restaurantMenuIntent.putExtra("restaurantId", CurrentRestaurantClass.restaurantId= restaurant.getId());
            restaurantMenuIntent.putExtra("restaurantName", CurrentRestaurantClass.restaurantName=restaurant.getName());
            context.startActivity(restaurantMenuIntent);
        }
    }
}
