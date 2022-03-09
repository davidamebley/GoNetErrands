package somame.amebleysystems.com.somame;

import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Resources;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import Adapter.ProdSubCatAdapter;
import Model.Customer;
import Model.ProductBasketItem;
import Model.ProductSubCat;
import SharedPref.SharedPrefManager;
import Util.RetrofitApiClient2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class ProductSubCatActivity extends AppCompatActivity {
    public static RetrofitApiInterfaceLoadSubCat apiInterfaceLoadSubCat;
    public static RetrofitApiInterfaceProductSubCatLoadBasketSize apiInterfaceProductSubCatLoadBasketSize;
    RecyclerView recyclerView;
    ProdSubCatAdapter recyclerAdapter;
    List<ProductSubCat> productSubCatList;
    List<ProductBasketItem> productBasketItemList;
    List<ProductBasketItem> basketItemList;
    ProgressBar progressBar;
    private Bundle extras;
    private int catId=0, basketSize=0;
    private String catName;
    private AppCompatTextView txtSubCatName;
    Intent ProductIntent;
    int customerId;
    Customer customer;
    private Menu menu;
    MenuItem basketMenuItem;

    @Override
    protected void onResume() {
        super.onResume();
        //Refresh Menu Items
        invalidateOptionsMenu();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Retrofit api
        apiInterfaceLoadSubCat = RetrofitApiClient2.getApiClient().create(RetrofitApiInterfaceLoadSubCat.class);
        apiInterfaceProductSubCatLoadBasketSize = RetrofitApiClient2.getApiClient().create(RetrofitApiInterfaceProductSubCatLoadBasketSize.class);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_sub_cat);

        txtSubCatName = findViewById(R.id.textview_product_sub_cat_top_name);
        txtSubCatName.setTextColor(getResources().getColor(R.color.colorMyVioletDark));

        //GET Intent Here
        extras = getIntent().getExtras();

        //        Toolbar Declare
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_product_sub_cat);
        if (extras != null) {
            catId = extras.getInt("catId");
            catName = extras.getString("catName");

//            toolbar.setSubtitle();
            txtSubCatName.setText(catName);
        }

        toolbar.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        toolbar.setSubtitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

//      Progressbar
        progressBar = findViewById(R.id.progress_product_sub_cat);
        progressBar.setVisibility(View.VISIBLE);

        //        More On Toolbar
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_back);
        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                        finish();
                    }
                }
        );

        //Customer
        customer = SharedPrefManager.getInstance(getApplicationContext()).getCustomer();
        customerId = customer.getCustomerId();

        recyclerView = findViewById(R.id.recycler_product_sub_cat);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,4));

        //My function to Set Margin
        int height = 18;
        setMargins(recyclerView, ViewGroup.LayoutParams.WRAP_CONTENT,height,ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        recyclerView.requestLayout();

        //Extras Go on...
        if (catId != 0) {
            //Load Volley Data
            loadProductSubCatList();
        }
    }

    //Function to set margins of view
    private void setMargins(View view, int left, int top, int right, int bottom) {
        //First Convert dp value to px
        Resources resources = getApplicationContext().getResources();
        left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,left,resources.getDisplayMetrics());
        top = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,top,resources.getDisplayMetrics());
        right = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,right,resources.getDisplayMetrics());
        bottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,bottom,resources.getDisplayMetrics());
        //Now we set Margin
        if(view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams){
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left,top,right,bottom);
            view.requestLayout();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.product_subcat_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //Needed for refresh toolbar items
        basketMenuItem = menu.findItem(R.id.action_product_sub_cat_goto_basket);
        loadBasketSize();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_product_sub_cat_share:
                //Share App
                Utilities.shareApp(getApplicationContext());
                return true;
            case R.id.action_product_sub_cat_goto_basket:
                //Do something
                Utilities.gotoRestaurantBasket(getApplicationContext());
                return true;
            case R.id.action_product_sub_cat_my_account:
                //Do something
                Utilities.openBackOfficeAccount(getApplicationContext());
                return true;
            case R.id.action_product_sub_cat_sign_out:
                //Function to perform SignOut
                SharedPrefManager.getInstance(getApplicationContext()).logoutCustomer();
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //    ----------------RETROFIT-----------------(Load Product List)---------------------RETROFIT
    private void loadProductSubCatList() {
        Call<List<ProductSubCat>> call = apiInterfaceLoadSubCat.loadSubCat(String.valueOf(catId));
        call.enqueue(new Callback<List<ProductSubCat>>() {

            @Override
            public void onResponse(Call<List<ProductSubCat>> call, retrofit2.Response<List<ProductSubCat>> response) {
                Log.e("SubCat_TAG", "response: "+new Gson().toJson(response.body()) );
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.e("onSuccess", response.body().toString());
                        List<ProductSubCat> productSubCatList = response.body();
                        //Creating a String array for the List
                        loadSubCatResponse(productSubCatList);

                    } else {
                        Log.e("onEmptyResponse", "Returned empty response");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ProductSubCat>> call, Throwable t) {
                //display error message
                Log.d("retrofit_log", "Failed! Error = " + t.getMessage());
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ProductSubCatActivity.this, "Error. Check connection and try again", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void loadSubCatResponse(List<ProductSubCat> response) {
        //getting the whole json object from the response
        productSubCatList = new ArrayList<>();

        //JSONObject productBasketItem
        ProductSubCat productBasketItem;

        //traversing through all the objects
        for (int i = 0; i < response.size(); i++) {
            //getting product object from json array
            productBasketItem = response.get(i);
            //adding the product to Subcat List
            productSubCatList.add(new ProductSubCat(
                    productBasketItem.getSubCatId(),
                    productBasketItem.getCatId(),
                    productBasketItem.getSubCatName(),
                    productBasketItem.getSubCatImage()
            ));
        }
        //creating adapter object and setting it to recyclerview
        recyclerAdapter = new ProdSubCatAdapter(ProductSubCatActivity.this,productSubCatList);

        recyclerView.setAdapter(recyclerAdapter);

        progressBar.setVisibility(View.GONE);
    }

    private void loadBasketSize(){
        Call<List<ProductBasketItem>> call = apiInterfaceProductSubCatLoadBasketSize.getBasketSize(String.valueOf(customerId));
        call.enqueue(new Callback<List<ProductBasketItem>>() {

            @Override
            public void onResponse(Call<List<ProductBasketItem>> call, retrofit2.Response<List<ProductBasketItem>> response) {
                Log.e("Basket_TAG", "response: "+new Gson().toJson(response.body()) );
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.e("onSuccess", response.body().toString());
                        List<ProductBasketItem> basketItemList = response.body();
                        //Creating a String array for the List
                        loadBasketResponse(basketItemList);

                    } else {
                        Log.e("onEmptyResponse", "Returned empty response");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ProductBasketItem>> call, Throwable t) {
                //display error message
                Log.d("retrofit_log", "Failed! Error = " + t.getMessage());
            }
        });
    }
    private void loadBasketResponse(List<ProductBasketItem> response) {
        //getting the whole json object from the response
        basketItemList = new ArrayList<>();
        int addCount = 0;
//                JSONObject productCat;
        ProductBasketItem basketItem;
        //traversing through all the object
        for (int i = 0; i < response.size(); i++) {
            //getting product object from json array
            addCount++;
        }
        basketSize = addCount;
        //Converting BasketIcon to BadgeIcon
        if (basketSize > 0) {
            basketMenuItem.setIcon(BadgeCounterIconConverter.convertLayoutToImage(ProductSubCatActivity.this,basketSize,R.drawable.ic_product_basket));
        }else{
            basketMenuItem.setIcon(getResources().getDrawable(R.drawable.ic_product_basket));
        }
    }
}
//Retrofit API Interface
interface RetrofitApiInterfaceLoadSubCat {
    @POST(URLs.URL_PRODUCT_SUBCAT_LIST)
    @FormUrlEncoded
    Call<List<ProductSubCat>> loadSubCat(@Field("catId") String catId);
}
//Retrofit API Interface
interface RetrofitApiInterfaceProductSubCatLoadBasketSize {
    @POST(URLs.URL_PRODUCT_BASKET_ITEM_LIST)
    @FormUrlEncoded
    Call<List<ProductBasketItem>> getBasketSize(@Field("customerId") String customerId);
}