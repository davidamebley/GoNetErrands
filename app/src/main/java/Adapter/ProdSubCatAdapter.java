package Adapter;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import Model.ProductCat;
import Model.ProductSubCat;
import somame.amebleysystems.com.somame.ProductListActivity;
import somame.amebleysystems.com.somame.R;

/**
 * Created by Akwasi on ${9/27/2018}.
 */

public class ProdSubCatAdapter extends RecyclerView.Adapter<ProdSubCatAdapter.ViewHolder>{
    private Context context;
    private List<ProductSubCat> productSubCatList;

    public ProdSubCatAdapter(Context context, List<ProductSubCat> productSubCatList) {
        this.context = context;
        this.productSubCatList = productSubCatList;
    }

    @NonNull
    @Override
    public ProdSubCatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_subcat_item,parent,false);
        return new ProdSubCatAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProdSubCatAdapter.ViewHolder holder, int position) {
        ProductSubCat productSubCatItem = productSubCatList.get(position);
        holder.textViewProdSubCatDesc.setText(productSubCatItem.getSubCatName());
        context.getTheme().resolveAttribute(android.R.attr.actionBarSize,holder.tv,true);
        int actionBarSize = context.getResources().getDimensionPixelSize(holder.tv.resourceId);
        holder.cardViewSubCatItem.getLayoutParams().height =actionBarSize;
        holder.cardViewSubCatItem.getLayoutParams().width = actionBarSize;

        RequestOptions requestOptions = new RequestOptions();
        //loading the image
        Glide.with(context)
                .load(productSubCatItem.getSubCatImage())
                .apply(requestOptions)
                .into(holder.imageViewProdSubCatImage);
        holder.imageViewProdSubCatImage.getLayoutParams().height=actionBarSize;
        holder.imageViewProdSubCatImage.getLayoutParams().width=actionBarSize;
    }

    @Override
    public int getItemCount() {
        return productSubCatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AppCompatTextView textViewProdSubCatDesc;
        private ImageView imageViewProdSubCatImage;
        CardView cardViewSubCatItem;
        LinearLayout linearLayout;
        TypedValue tv;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewProdSubCatDesc = itemView.findViewById(R.id.textview_product_subcat_item_name);
            imageViewProdSubCatImage = itemView.findViewById(R.id.imageview_product_subcat_item);
            cardViewSubCatItem = itemView.findViewById(R.id.cardView_prod_subcat_item);
            linearLayout = itemView.findViewById(R.id.layout_prod_subcat_item);
            tv = new TypedValue();
//            Set OnClickListener
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

//            Picking the Model item Product and setting its clickListener
            ProductSubCat productSubCat = productSubCatList.get(getAdapterPosition());

//            GET Menu ID and Send To New Activity
            Intent productListIntent = new Intent(context, ProductListActivity.class);
//            We use the 'GetCatId' method to get the menu Id as a KEY value and send along
            productListIntent.putExtra("subCatId", productSubCat.getSubCatId());
            productListIntent.putExtra("subCatName", productSubCat.getSubCatName());
//            Toast.makeText(context, "ID "+productSubCat.getSubCatId(), Toast.LENGTH_SHORT).show();
            context.startActivity(productListIntent);
        }
    }
}
