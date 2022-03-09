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

import Interface.RecyclerViewLongClickListener;
import Model.ProductWishListItem;
import somame.amebleysystems.com.somame.R;
import somame.amebleysystems.com.somame.Utilities;

/**
 * Created by Akwasi on ${9/27/2018}.
 */

public class ProductWishListAdapter extends RecyclerView.Adapter<ProductWishListAdapter.ViewHolder> {
    Context context;
    List<ProductWishListItem> productWishListItemList;
//    RecyclerViewLongClickListener recyclerViewLongClickListener;
    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public ProductWishListAdapter(Context context, List<ProductWishListItem> productWishListItemList) {
        this.context = context;
        this.productWishListItemList = productWishListItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_wish_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        ProductWishListItem wishListItem = productWishListItemList.get(position);
        holder.textViewProductName.setText(wishListItem.getName());
        holder.textViewUnitPrice.setText(wishListItem.getPrice());
        holder.textViewManufacturer.setText(wishListItem.getManufacturer());
        //Setting Currency for Price Text
        Locale locale = new Locale("en", "GH");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        holder.textViewUnitPrice.setText(fmt.format(Double.parseDouble(wishListItem.getPrice())));

        RequestOptions requestOptions = new RequestOptions();
        //loading the image
        Glide.with(context)
                .load(wishListItem.getLogo())
                .apply(requestOptions)
                .into(holder.imageViewProductLogo);

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
    public void onViewRecycled(ViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return productWishListItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        TextView textViewProductName, textViewUnitPrice, textViewManufacturer;
        ImageView imageViewProductLogo;
        public ViewHolder(View itemView) {
            super(itemView);
            textViewManufacturer = itemView.findViewById(R.id.textview_product_wishlist_item_manufacturer);
            textViewUnitPrice = itemView.findViewById(R.id.textview_product_wishlist_item_price);
            textViewProductName = itemView.findViewById(R.id.textview_product_wishlist_item_name);
            imageViewProductLogo = itemView.findViewById(R.id.imageview_product_wishlist_item);
            //Context Menu Init
            itemView.setOnCreateContextMenuListener(this);
//            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            // For the Context Menu
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle("Select action");
            menu.add(0,v.getId(),0, Utilities.DELETE);
            menu.add(0,v.getId(),0, Utilities.VIEW_PRODUCT);
        }


        /*@Override
        public boolean onLongClick(View v) {
            recyclerViewLongClickListener.recyclerViewListLongClicked(getAdapterPosition());
            return true;
        }*/
    }
}
