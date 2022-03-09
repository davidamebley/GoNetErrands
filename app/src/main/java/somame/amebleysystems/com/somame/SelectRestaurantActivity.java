package somame.amebleysystems.com.somame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

import Adapter.ProdCatAdapter;
import Adapter.RestaurantAdapter;
import Model.Customer;
import Model.ProductBasketItem;
import Model.Restaurant;
import Model.RestaurantBasketItem;
import Model.RestaurantLocation;
import SharedPref.SharedPrefManager;
import Util.RetrofitApiClient2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class SelectRestaurantActivity extends AppCompatActivity {
    public static RetrofitApiInterfaceSelectRestauLoadBasketSize apiInterfaceSelectRestauLoadBasketSize;
    public static RetrofitApiInterfaceRestaurantList apiInterfaceRestaurantList;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    LinearLayout linearLayout;
//    Spinner spinnerSelectRestaurant;
    RestaurantAdapter recyclerAdapter;
    List<String> restaurantLocations;
    List<String> tempLocations;
    List<Integer> tempLocationIds;
    List<Restaurant> restaurantList;
    int selectedLocationId=0;
    boolean isResultFound=false;
    private Menu menu;
    private int  basketSize=0, customerId;
    Toolbar toolbar;
    MenuItem basketMenuItem;
    List<RestaurantBasketItem> basketItemList;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //Needed for refresh toolbar items
        basketMenuItem = menu.findItem(R.id.action_select_restaurant_goto_basket);
        loadBasketFromDB();
        return true;
    }

    @Override
    protected void onResume() {

        //Reload product wish list status
        super.onResume();
        //Refresh Menu Items
        loadBasketFromDB();
        invalidateOptionsMenu();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Retrofit Api interface
        apiInterfaceSelectRestauLoadBasketSize = RetrofitApiClient2.getApiClient().create(RetrofitApiInterfaceSelectRestauLoadBasketSize.class);
        apiInterfaceRestaurantList = RetrofitApiClient2.getApiClient().create(RetrofitApiInterfaceRestaurantList.class);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_restaurant);
        //        Toolbar Declare
        toolbar = (Toolbar) findViewById(R.id.toolbar_select_restaurant);
        toolbar.setSubtitle("Your Restaurant");
        toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        toolbar.setSubtitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

//      Progressbar
        progressBar = findViewById(R.id.progress_select_restaurant);

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
        Customer customer = SharedPrefManager.getInstance(getApplicationContext()).getCustomer();
        customerId = customer.getCustomerId();

        linearLayout = findViewById(R.id.layout_select_restaurant_main);
//        Recycler view init
        recyclerView = findViewById(R.id.recycler_select_restaurant);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Load Restaurants
        loadRestaurantList();
    }

    //    RETROFIT CONNECTION----------(For Restaurant Location List)--------------RETROFIT CONNECTION
    private void loadRestaurantList(){
        showProgress(true);
        Call<List<Restaurant>> call = apiInterfaceRestaurantList.loadRestaurantList();
        call.enqueue(new Callback<List<Restaurant>>() {
            @Override
            public void onResponse(Call<List<Restaurant>> call, retrofit2.Response<List<Restaurant>> response) {
                Log.e("Restaurant_list", "response: "+new Gson().toJson(response.body()) );
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.e("onSuccess", response.body().toString());
                        List<Restaurant> restaurantList = response.body();
                        //Creating a String array for the List
                        loadRestaurantResponse(restaurantList);
                    } else {
                        Log.e("onEmptyResponse", "Returned empty response");
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Restaurant>> call, Throwable t) {
                //display error message
                Log.d("retrofit_log", "Failed! Error = " + t.getMessage());
                showProgress(false);
                Toast.makeText(SelectRestaurantActivity.this, "Error. Check connection and try again", Toast.LENGTH_SHORT).show();
            }
        });
    }
    //Load JSON data from response
    private void loadRestaurantResponse(List<Restaurant> response) {
        //getting the whole json object from the response
        JSONArray array = new JSONArray(response);
        restaurantList = new ArrayList<>();

        Restaurant restaurant;
        //Check if result was found
        isResultFound = array.length() > 0; //Simplified ternary
        //traversing through all the object
        for (int i = 0; i < array.length(); i++) {

            //getting restaurant object from json array
            restaurant = response.get(i);

            //adding the product to product CatList
            restaurantList.add(new Restaurant(
                    restaurant.getId(),
                    restaurant.getName(),
                    restaurant.getStartTime(),
                    restaurant.getClosingTime(),
                    restaurant.getLogo()
            ));
        }
        //creating adapter object and setting it to recyclerview
        recyclerAdapter = new RestaurantAdapter(SelectRestaurantActivity.this,restaurantList);

        recyclerView.setAdapter(recyclerAdapter);
        showProgress(false);
    }

    //Function to Load Basket Item Size
    private void loadBasketFromDB() {
        Call<List<RestaurantBasketItem>> call = apiInterfaceSelectRestauLoadBasketSize.getBasketSize(String.valueOf(customerId));
        call.enqueue(new Callback<List<RestaurantBasketItem>>() {
            @Override
            public void onResponse(Call<List<RestaurantBasketItem>> call, retrofit2.Response<List<RestaurantBasketItem>> response) {
                Log.e("Basket_Size", "response: "+new Gson().toJson(response.body()) );
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.e("onSuccess", response.body().toString());
                        List<RestaurantBasketItem> basketItemList = response.body();
                        //Creating a String array for the List
                        loadBasketResponse(basketItemList);
                    } else {
                        Log.e("onEmptyResponse", "Returned empty response");
                    }
                }
            }
            @Override
            public void onFailure(Call<List<RestaurantBasketItem>> call, Throwable t) {
                //display error message
                Log.d("retrofit_log", "Failed! Error = " + t.getMessage());
            }
        });
    }
    //Load JSON data from response
    private void loadBasketResponse(List<RestaurantBasketItem> response) {
        //getting the whole json object from the response
        basketItemList = new ArrayList<>();
        int addCount = 0;
//                JSONObject productCat;
        RestaurantBasketItem basketItem;
        //traversing through all the object
        for (int i = 0; i < response.size(); i++) {
            //getting product object from json array
            addCount++;

        }
        basketSize = addCount;
        //Converting BasketIcon to BadgeIcon
        if (basketSize > 0) {
            basketMenuItem.setIcon(BadgeCounterIconConverter.convertLayoutToImage(SelectRestaurantActivity.this,basketSize,R.drawable.ic_product_basket));
        }else{
            basketMenuItem.setIcon(getResources().getDrawable(R.drawable.ic_product_basket));
        }

    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            linearLayout.setVisibility(show ? View.GONE : View.VISIBLE);
            linearLayout.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    linearLayout.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            linearLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    //Menu On Toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.select_restaurant_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_select_restaurant_share:
                //Share App
                Utilities.shareApp(SelectRestaurantActivity.this);
                return true;
            case R.id.action_select_restaurant_goto_basket:
                //Do something
                Utilities.gotoRestaurantBasket(SelectRestaurantActivity.this);
                return true;
            case R.id.action_select_restaurant_my_account:
                //Do something
                Utilities.openBackOfficeAccount(SelectRestaurantActivity.this);
                return true;
            case R.id.action_select_restaurant_sign_out:
                //Function to perform SignOut
                SharedPrefManager.getInstance(SelectRestaurantActivity.this).logoutCustomer();
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
//Retrofit API Interface
interface RetrofitApiInterfaceSelectRestauLoadBasketSize {
    @POST(URLs.URL_RESTAURANT_BASKET_ITEM_LIST)
    @FormUrlEncoded
    Call<List<RestaurantBasketItem>> getBasketSize(@Field("customerId") String customerId);
}
//Retrofit API Interface
interface RetrofitApiInterfaceRestaurantList {
    @POST(URLs.URL_RESTAURANT_LIST)
//    @FormUrlEncoded
    Call<List<Restaurant>> loadRestaurantList();
}