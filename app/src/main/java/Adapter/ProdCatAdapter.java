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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import Model.ProductCat;
import Model.ProductSubCat;
import somame.amebleysystems.com.somame.ProductCatActivity;
import somame.amebleysystems.com.somame.ProductSubCatActivity;
import somame.amebleysystems.com.somame.R;
import somame.amebleysystems.com.somame.RestaurantMenuActivity;
import somame.amebleysystems.com.somame.SelectRestaurantActivity;

/**
 * Created by Akwasi on 9/23/2018.
 */

public class ProdCatAdapter extends RecyclerView.Adapter<ProdCatAdapter.ViewHolder>{
    private Context context;
    private List<ProductCat> productCatList;

    public ProdCatAdapter(Context ctx, List<ProductCat> productCats) {
        this.context = ctx;
        this.productCatList = productCats;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.prod_cat_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductCat productCatItem = productCatList.get(position);
        holder.textViewProdCatDesc.setText(productCatItem.getProdCatDesc());

        RequestOptions requestOptions = new RequestOptions();
        //loading the image
        Glide.with(context)
                .load(productCatItem.getProdCatImage())
                .apply(requestOptions)
                .into(holder.imageViewProdCatImage);
    }

    @Override
    public int getItemCount() {
        return productCatList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private AppCompatTextView textViewProdCatDesc;
        private ImageView imageViewProdCatImage;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewProdCatDesc = itemView.findViewById(R.id.textview_prod_cat_desc);
            imageViewProdCatImage = itemView.findViewById(R.id.imageview_prod_cat);
//            Set OnClickListener
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

//            Picking the Model item Product and setting its clickListener
            ProductCat productCatItem = productCatList.get(getAdapterPosition());

//            Get item id when clicked
//            Toast.makeText(context,(productCatItem.getProdCatDesc())+" clicked",Toast.LENGTH_SHORT).show();

            //Open Restaurant Activity
            if (productCatItem.getProdCatId()==9){
                Intent restaurantIntent = new Intent(context,
                        SelectRestaurantActivity.class);
                context.startActivity(restaurantIntent);
            }else{
                Intent productListIntent = new Intent(context, ProductSubCatActivity.class);
//            We get the Id as a KEY value and send along
                productListIntent.putExtra("catId", productCatItem.getProdCatId());
                productListIntent.putExtra("catName", productCatItem.getProdCatDesc());
                context.startActivity(productListIntent);

            }
        }
    }
}
