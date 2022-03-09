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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Adapter.ProductBasketItemAdapter;
import Adapter.ProductWishListAdapter;
import Model.Customer;
import Model.ProductBasketItem;
import Model.ProductOrderItem;
import Model.ProductWishListItem;
import SharedPref.SharedPrefManager;
import Util.RetrofitApiClient;
import Util.RetrofitApiClient2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class ProductWishListActivity extends AppCompatActivity{
    public static RetrofitApiInterfaceLoadWishList apiInterfaceLoadWishList;
    public static RetrofitApiInterfaceDeleteWishList apiInterfaceDeleteWishList;
    public static RetrofitApiInterfaceProductWishListLoadBasketSize apiInterfaceProductWishListLoadBasketSize;
    RecyclerView recyclerView;
    ProductWishListAdapter recyclerAdapter;
    TextView textViewTopName;
    int itemTotal, customerId, basketSize=0;
    List<ProductWishListItem> productWishListItemList;
    List<ProductBasketItem> basketItemList;
    ProgressBar progressBar;
    Intent newIntent;
    Toolbar toolbar;
    LinearLayout linearLayout, linearLayoutRecycler;
    private Customer customer;
    private Menu menu;
    MenuItem basketMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Retrofit api
        apiInterfaceLoadWishList = RetrofitApiClient2.getApiClient().create(RetrofitApiInterfaceLoadWishList.class);
        apiInterfaceDeleteWishList = RetrofitApiClient.getApiClient().create(RetrofitApiInterfaceDeleteWishList.class);
        apiInterfaceProductWishListLoadBasketSize = RetrofitApiClient2.getApiClient().create(RetrofitApiInterfaceProductWishListLoadBasketSize.class);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_wish_list);

        //View Init
        textViewTopName = findViewById(R.id.textview_product_wishlist_top_name);
        linearLayout = findViewById(R.id.layout_prod_basket_item_summary);
        linearLayoutRecycler = findViewById(R.id.layout_recycler_prod_basket_item_summary);

        //Customer
        customer = SharedPrefManager.getInstance(getApplicationContext()).getCustomer();
        customerId = customer.getCustomerId();

        //Check if user is logged in
        if (!SharedPrefManager.getInstance(this).isCustomerLoggedIn()) {
            finish();
            startActivity(new Intent(this, SignInActivity.class));
        }

        //      Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_product_wishlist);

        toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        toolbar.setSubtitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        //      Progressbar
        progressBar = findViewById(R.id.progress_product_wishlist);
        progressBar.setVisibility(View.VISIBLE);

        recyclerView = findViewById(R.id.recycler_product_wishlist);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        registerForContextMenu(recyclerView);

        //If Everything fine, Load Wish list Items
        loadWishListItems();

    }

    //Back Button Pressed
    @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();
    }

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
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.product_wishlist_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //Needed for refresh toolbar items
        basketMenuItem = menu.findItem(R.id.action_product_wishlist_goto_basket);
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
            case R.id.action_product_wishlist_share:
                //Share App
                Utilities.shareApp(ProductWishListActivity.this);
                return true;
            case R.id.action_product_wishlist_goto_basket:
                //Do something
                Utilities.gotoProductBasket(ProductWishListActivity.this);
                return true;
            case R.id.action_product_wishlist_my_account:
                //Do something
                Utilities.openBackOfficeAccount(ProductWishListActivity.this);
                return true;
            case R.id.action_product_wishlist_sign_out:
                //Function to perform SignOut
                SharedPrefManager.getInstance(ProductWishListActivity.this).logoutCustomer();
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //----------------RETROFIT-----------------(Load Wish list)--------------RETROFIT-------------------------------------
    private void loadWishListItems() {
        Call<List<ProductWishListItem>> call = apiInterfaceLoadWishList.loadWishListItems(String.valueOf(customerId));
        call.enqueue(new Callback<List<ProductWishListItem>>() {

            @Override
            public void onResponse(Call<List<ProductWishListItem>> call, retrofit2.Response<List<ProductWishListItem>> response) {
                Log.e("WishList_TAG", "response: "+new Gson().toJson(response.body()) );
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.e("onSuccess", response.body().toString());
                        List<ProductWishListItem> wishListItemList = response.body();
                        //Creating a String array for the List
                        loadWishListResponse(wishListItemList);
                    } else {
                        Log.e("onEmptyResponse", "Returned empty response");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ProductWishListItem>> call, Throwable t) {
                //display error message
                Log.d("retrofit_log", "Failed! Error = " + t.getMessage());
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ProductWishListActivity.this, "Error. Check connection and try again", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void loadWishListResponse(List<ProductWishListItem> response) {
        //getting the whole json object from the response
        productWishListItemList = new ArrayList<>();
        itemTotal=0;

        //JSONObject productBasketItem
        ProductWishListItem productWishListItem;

        //traversing through all the objects
        for (int i = 0; i < response.size(); i++) {
            //Getting item total
            itemTotal++;
            //getting product object from json array
            productWishListItem = response.get(i);
            //adding the items to Basket List
            productWishListItemList.add(new ProductWishListItem(
                    productWishListItem.getId(),
                    productWishListItem.getProductId(),
                    productWishListItem.getSubCatId(),
                    productWishListItem.getCustomerId(),
                    productWishListItem.getName(),
                    productWishListItem.getPrice(),
                    productWishListItem.getDesc(),
                    productWishListItem.getManufacturer(),
                    productWishListItem.getLogo()
            ));
        }
        //Setting the Top title plus Item total
        textViewTopName.setText(R.string.wish_list);
        //Assign Basket Item total in Utilities
        Utilities.setProdBasketItemTotal(itemTotal);
        StringBuilder textTopName = new StringBuilder();
        textTopName.append(textViewTopName.getText()).append(" (").append(itemTotal).append(")");
        textViewTopName.setText(textTopName);

        if(itemTotal < 1) {
            Toast.makeText(getApplicationContext(), "Your wish list is empty", Toast.LENGTH_SHORT).show();
        }
        //creating adapter object and setting it to recyclerview
        recyclerAdapter = new ProductWishListAdapter(getApplicationContext(),productWishListItemList);

        recyclerView.setAdapter(recyclerAdapter);
        progressBar.setVisibility(View.GONE);
    }

    private void loadBasketSize(){
        Call<List<ProductBasketItem>> call = apiInterfaceProductWishListLoadBasketSize.getBasketSize(String.valueOf(customerId));
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
            basketMenuItem.setIcon(BadgeCounterIconConverter.convertLayoutToImage(ProductWishListActivity.this,basketSize,R.drawable.ic_product_basket));
        }else{
            basketMenuItem.setIcon(getResources().getDrawable(R.drawable.ic_product_basket));
        }
    }

    //Context(Right click) Menu
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = -1;
        try {
            position = ((ProductWishListAdapter)recyclerView.getAdapter()).getPosition();
        } catch (Exception e) {
            Log.d("Context Menu item", String.valueOf(position));
            return super.onContextItemSelected(item);
        }

        ProductWishListItem wishListItem = productWishListItemList.get(position);
        Intent productDetailIntent = new Intent(getApplicationContext(), ProductDetailActivity.class);
//            We get the Id as a KEY value and send along
        productDetailIntent.putExtra("productId", wishListItem.getProductId());
        productDetailIntent.putExtra("productName", wishListItem.getName());
        productDetailIntent.putExtra("productPhoto", wishListItem.getLogo());
        productDetailIntent.putExtra("productDesc", wishListItem.getDesc());
        productDetailIntent.putExtra("productPrice", wishListItem.getPrice());
        productDetailIntent.putExtra("manufacturer", wishListItem.getManufacturer());
        productDetailIntent.putExtra("subCatId", wishListItem.getSubCatId());
        productDetailIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);

        if(item.getTitle().equals(Utilities.DELETE)){
//            getApplicationContext().startActivity(productDetailIntent);
            int itemId = wishListItem.getProductId();
            //Delete Item from WishList
            deleteFromWishList(itemId);
//            Toast.makeText(getApplicationContext(),"Delete not ready",Toast.LENGTH_SHORT).show();
        }
        if(item.getTitle().equals(Utilities.VIEW_PRODUCT)) {
//            Toast.makeText(getApplicationContext(), "View Product", Toast.LENGTH_SHORT).show();
            getApplicationContext().startActivity(productDetailIntent);
            this.finish();
//        return true;
        }
        return super.onContextItemSelected(item);
    }

    //FUnction to delete item from WishList
    private void deleteFromWishList(final int itemId) {
        /*
        Retrofit Network Connection
        * */
        Call<String> call = apiInterfaceDeleteWishList.deleteWishListItem(String.valueOf(itemId), String.valueOf(customerId));
        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                Log.e("Del_WishList_TAG", "response: "+new Gson().toJson(response.body()) );
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.e("onSuccess", response.body());
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(response.body());
                            //Success sign in
                            if(obj.optString("item_removed").equals("true")){
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                //After remove success, reload
                                loadWishListItems();
                            } else {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                //After remove fail, reload
                                loadWishListItems();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Log.e("onEmptyResponse", "Returned empty response");
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                //display error message
                Log.d("retrofit_log", "Failed! Error = " + t.getMessage());
                Toast.makeText(ProductWishListActivity.this, "Error. Check connection and try again", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
//Retrofit API Interface
interface RetrofitApiInterfaceLoadWishList {
    @POST(URLs.URL_PRODUCT_WISHLIST)
    @FormUrlEncoded
    Call<List<ProductWishListItem>> loadWishListItems(@Field("customerId") String customerId);
}
//Retrofit API Interface
interface RetrofitApiInterfaceDeleteWishList {
    @POST(URLs.URL_DELETE_PRODUCT_FROM_WISHLIST)
    @FormUrlEncoded
    Call<String> deleteWishListItem(@Field("productId") String itemId, @Field("customerId") String customerId);
}
//Retrofit API Interface
interface RetrofitApiInterfaceProductWishListLoadBasketSize {
    @POST(URLs.URL_PRODUCT_BASKET_ITEM_LIST)
    @FormUrlEncoded
    Call<List<ProductBasketItem>> getBasketSize(@Field("customerId") String customerId);
}