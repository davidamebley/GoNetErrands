package somame.amebleysystems.com.somame;

import android.content.Intent;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import Adapter.ProdCatAdapter;
import Model.Customer;
import Model.ProductBasketItem;
import Model.ProductCat;
import Util.RetrofitApiClient2;
import SharedPref.SharedPrefManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class ProductCatActivity extends AppCompatActivity {
    public static RetrofitApiInterfaceProdCat apiInterface;
    public static RetrofitApiInterfaceLoadBasketSize apiInterfaceloadBasketSize;
    RecyclerView recyclerView;
    RecyclerView.Adapter recyclerAdapter;
    List<ProductCat> productCatList;
    List<ProductBasketItem> basketItemList;
    ProgressBar progressBar;
    int customerId, basketSize=0;
    private Menu menu;
    MenuItem basketMenuItem;

    //    Back to Exit
    @Override
    public void onBackPressed() {
            super.onBackPressed();
            finish();
        }

    @Override
    protected void onResume() {

        //Reload product wish list status
        super.onResume();
        //Refresh Menu Items
        invalidateOptionsMenu();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
    }

    protected void onCreate(Bundle savedInstanceState) {
        //Retrofit api
        apiInterface = RetrofitApiClient2.getApiClient().create(RetrofitApiInterfaceProdCat.class);
        apiInterfaceloadBasketSize = RetrofitApiClient2.getApiClient().create(RetrofitApiInterfaceLoadBasketSize.class);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_cat);
//        Toolbar Declare
        Toolbar toolbar = findViewById(R.id.toolbar_prod_cat);
        toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        toolbar.setSubtitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        Customer customer = SharedPrefManager.getInstance(getApplicationContext()).getCustomer();
        customerId = customer.getCustomerId();

        //Check if user is logged in
        if (!SharedPrefManager.getInstance(this).isCustomerLoggedIn()) {
            finish();
            startActivity(new Intent(this, SignInActivity.class));
        }

//          ProgressBar
        progressBar = findViewById(R.id.progress_prod_cat);
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


        recyclerView = findViewById(R.id.recycler_prod_cat);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));

//        ====================================Loading Product Category List==========================================
        loadProductCat();
    }

//RETROFIT Connection...........RETROFIT Connection...........RETROFIT Connection.................
    private void loadProductCat() {
        progressBar.setVisibility(View.VISIBLE);

            Call<List<ProductCat>> call =apiInterface.getProdCat();
            call.enqueue(new Callback<List<ProductCat>>() {

                @Override
                public void onResponse(Call<List<ProductCat>> call, retrofit2.Response<List<ProductCat>> response) {
                    Log.e("TAG", "response: "+new Gson().toJson(response.body()) );
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            Log.e("onSuccess", response.body().toString());
                            List<ProductCat> productCatList = response.body();
                            //Creating an String array for the ListView
                            loadResponse(productCatList);

                        } else {
                            Log.e("onEmptyResponse", "Returned empty response");
                        }
                    }
                }

                @Override
            public void onFailure(Call<List<ProductCat>> call, Throwable t) {
                //display error message
                Log.d("retrofit_log", "Failed! Error = " + t.getMessage());
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ProductCatActivity.this, "Error. Check connection and try again", Toast.LENGTH_SHORT).show();
            }

        });

    }

    //Load JSON data from response
    private void loadResponse(List<ProductCat> response) {
        //getting the whole json object from the response
        productCatList = new ArrayList<>();
//                JSONObject productCat;
        ProductCat productCat;
        //traversing through all the object
        for (int i = 0; i < response.size(); i++) {
            //getting product object from json array
            productCat = response.get(i);
            //adding the product to product CatList
            productCatList.add(new ProductCat(
                    productCat.getProdCatId(),
                    productCat.getProdCatDesc(),
                    productCat.getProdCatImage()
            ));

        }
        //creating adapter object and setting it to recyclerview
        recyclerAdapter = new ProdCatAdapter(ProductCatActivity.this,productCatList);

        recyclerView.setAdapter(recyclerAdapter);
        progressBar.setVisibility(View.GONE);

    }

    //Load JSON data from response
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
            basketMenuItem.setIcon(BadgeCounterIconConverter.convertLayoutToImage(ProductCatActivity.this,basketSize,R.drawable.ic_product_basket));
        }else{
            basketMenuItem.setIcon(getResources().getDrawable(R.drawable.ic_product_basket));
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        //Load Basket Item Size
//        loadBasketFromDB();

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //Needed for refresh toolbar items
        basketMenuItem = menu.findItem(R.id.action_main_menu_view_basket);
        loadBasketFromDB();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_main_menu_share:
                //Function to Share App
                Utilities.shareApp(this);
//                Toast.makeText(getApplicationContext(),"Share",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_main_menu_view_basket:
                //Open Product Basket
                Utilities.gotoProductBasket(getApplicationContext());
                return true;
            case R.id.action_main_menu_view_wish_list:
                //View WishList
                Utilities.viewProductWishlist(getApplicationContext());
                return true;
            case R.id.action_main_menu_my_orders:
                Utilities.gotoMyOrders(getApplicationContext());
                return true;
            case R.id.action_main_menu_my_account:
                //Open Back Office Account
                Utilities.openBackOfficeAccount(getApplicationContext());
                return true;
            case R.id.action_main_menu_sign_out:
                //Function to perform SignOut
                SharedPrefManager.getInstance(getApplicationContext()).logoutCustomer();
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void loadBasketFromDB(){
        Call<List<ProductBasketItem>> call = apiInterfaceloadBasketSize.getBasketSize(String.valueOf(customerId));
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
}

//Retrofit API Interface
interface RetrofitApiInterfaceProdCat {
    @POST(URLs.URL_PRODUCT_CAT)
    Call<List<ProductCat>> getProdCat();
}
//Retrofit API Interface
interface RetrofitApiInterfaceLoadBasketSize {
    @POST(URLs.URL_PRODUCT_BASKET_ITEM_LIST)
    @FormUrlEncoded
    Call<List<ProductBasketItem>> getBasketSize(@Field("customerId") String customerId);
}