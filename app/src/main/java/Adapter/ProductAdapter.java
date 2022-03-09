package Adapter;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Model.Product;
import Model.ProductSubCat;
import somame.amebleysystems.com.somame.ProductDetailActivity;
import somame.amebleysystems.com.somame.R;

/**
 * Created by Akwasi on ${9/27/2018}.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    List<Product> productList;
    Context context;

    public ProductAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item,parent,false);
        return new ProductAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder holder, int position) {
        Product productItem = productList.get(position);
        holder.textViewProductName.setText(productItem.getProductName());
        holder.textViewManufacturer.setText(productItem.getProductManufacturer());
        //Tweaking Width and Heights
        context.getTheme().resolveAttribute(android.R.attr.actionBarSize,holder.typedValue,true);
        int actionBarSize = context.getResources().getDimensionPixelSize(holder.typedValue.resourceId);
        holder.textViewProductPrice.getLayoutParams().width = actionBarSize+(actionBarSize/3);
//        holder.textViewProductName.getLayoutParams().width = holder.textViewProductName.getMaxWidth()-holder.textViewProductPrice.getLayoutParams().width;
        holder.linearLayout.getLayoutParams().width = (actionBarSize*3);
        //Setting Currency for Price Text
        Locale locale = new Locale("en", "GH");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        holder.textViewProductPrice.setText(fmt.format(Double.parseDouble(productItem.getUnitPrice())));

        RequestOptions requestOptions = new RequestOptions();
        //loading the image
        Glide.with(context)
                .load(productItem.getProductImage())
                .apply(requestOptions)
                .into(holder.imageViewProduct);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageViewProduct;
        TextView textViewProductName, textViewManufacturer, textViewProductPrice;
        LinearLayout linearLayout;
        TypedValue typedValue;
        public ViewHolder(View itemView) {
            super(itemView);
            imageViewProduct = itemView.findViewById(R.id.imageview_product_item);
            textViewProductName = itemView.findViewById(R.id.textview_product_name);
            textViewManufacturer = itemView.findViewById(R.id.textview_product_manufacturer);
            textViewProductPrice = itemView.findViewById(R.id.textview_product_item_price);
            linearLayout = itemView.findViewById(R.id.lay_pro_item_name);
            typedValue = new TypedValue();

            //            Set OnClickListener
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

//            Picking the Model item Product and setting its clickListener
            Product productItem = productList.get(getAdapterPosition());
//            GET Menu ID and Send To New Activity
            Intent productDetailIntent = new Intent(context, ProductDetailActivity.class);
//            We get the Id as a KEY value and send along
            productDetailIntent.putExtra("productId", productItem.getProductId());
            productDetailIntent.putExtra("productName", productItem.getProductName());
            productDetailIntent.putExtra("productPhoto", productItem.getProductImage());
            productDetailIntent.putExtra("productDesc", productItem.getProductDesc());
            productDetailIntent.putExtra("productPrice", productItem.getUnitPrice());
            productDetailIntent.putExtra("manufacturer", productItem.getProductManufacturer());
            productDetailIntent.putExtra("subCatId", productItem.getSubCatId());
            context.startActivity(productDetailIntent);
        }
    }


    //SearchView Stuff
    public void updateList(List<Product> lstFound){
        productList = new ArrayList<>();
        productList.addAll(lstFound);
        notifyDataSetChanged();
    }
}
