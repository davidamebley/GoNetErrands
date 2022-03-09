package Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import Model.RestaurantBasketItem;
import somame.amebleysystems.com.somame.R;
import somame.amebleysystems.com.somame.Utilities;

/**
 * Created by Akwasi on ${9/27/2018}.
 */

public class RestaurantBasketItemAdapter extends RecyclerView.Adapter<Adapter.RestaurantBasketItemAdapter.ViewHolder>{
        private List<RestaurantBasketItem> basketItemList;
        Context context;
        //    RecyclerViewLongClickListener recyclerViewLongClickListener;
        private int position;

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public RestaurantBasketItemAdapter(List<RestaurantBasketItem> basketItemList, Context context) {
            this.basketItemList = basketItemList;
            this.context = context;
        }

        @NonNull
        @Override
        public Adapter.RestaurantBasketItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_basket_item,parent,false);
            return new Adapter.RestaurantBasketItemAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final Adapter.RestaurantBasketItemAdapter.ViewHolder holder, int position) {
            RestaurantBasketItem basketItem = basketItemList.get(position);
            holder.textViewFoodName.setText(basketItem.getName());
            holder.textViewQuantity.setText(String.valueOf(basketItem.getQuantity()));
            //Setting Currency for Price Text
            Locale locale = new Locale("en", "GH");
            NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
            holder.textViewUnitPrice.setText(fmt.format(Double.parseDouble(basketItem.getPrice())));

            RequestOptions requestOptions = new RequestOptions();
            //loading the image
            Glide.with(context)
                    .load(basketItem.getLogo())
                    .apply(requestOptions)
                    .into(holder.imageViewFoodLogo);

            //For context menu
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    setPosition(holder.getAdapterPosition());
                    return false;
                }
            });
        }
        //Also for Context Menu
        @Override
        public void onViewRecycled(@NonNull Adapter.RestaurantBasketItemAdapter.ViewHolder holder) {
            holder.itemView.setOnLongClickListener(null);
            super.onViewRecycled(holder);
        }

        @Override
        public int getItemCount() {
            return basketItemList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
            TextView textViewFoodName, textViewUnitPrice, textViewQuantity;
            ImageView imageViewFoodLogo, imageViewDelete, imageViewUpdate;
            public ViewHolder(View itemView) {
                super(itemView);
                textViewFoodName = itemView.findViewById(R.id.textview_restaurant_basket_item_name);
                textViewUnitPrice = itemView.findViewById(R.id.textview_restaurant_basket_item_price);
                textViewQuantity = itemView.findViewById(R.id.textview_restaurant_basket_item_qty);
                imageViewFoodLogo = itemView.findViewById(R.id.imageview_restaurant_basket_item);
                //Context Menu Init
                itemView.setOnCreateContextMenuListener(this);
            }

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                // For the Context Menu
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                menu.setHeaderTitle("Select action");
                menu.add(0,v.getId(),0, Utilities.DELETE);
                menu.add(0,v.getId(),0, Utilities.UPDATE_PRODUCT);
            }
        }

}
