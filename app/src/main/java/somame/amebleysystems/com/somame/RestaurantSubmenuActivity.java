package somame.amebleysystems.com.somame;

import android.content.Intent;
import android.graphics.Color;
import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
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
import Adapter.RestaurantBasketItemAdapter;
import Adapter.RestaurantSubmenuAdapter;
import Interface.OnBottomReachedListener;
import Interface.OnMiddleReachedListener;
import Interface.OnTopReachedListener;
import Model.CurrentRestaurantClass;
import Model.Customer;
import Model.ProductCat;
import Model.RestaurantBasketItem;
import Model.RestaurantFood;
import Model.RestaurantMenuItemClass;
import SharedPref.SharedPrefManager;
import Util.RetrofitApiClient;
import Util.RetrofitApiClient2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class RestaurantSubmenuActivity extends AppCompatActivity {
    public static RetrofitApiInterfaceRestaurantSubMenu2 apiInterfaceRestaurantSubMenu2;
    public static RetrofitApiInterfaceRestauSubMenuLoadBasketSize apiInterfaceRestauSubMenuLoadBasketSize;
    RecyclerView recyclerView;
    RestaurantSubmenuAdapter recyclerAdapter;
    List<RestaurantFood> restaurantFoodList;
    ProgressBar progressBar;
    private Bundle extras;
    private String subMenuId;
    private String subMenuName;
    private AppCompatTextView txtSubMenuName;
    private int customerId;
    Intent mainMenuIntent;
    private Menu menu;
    private int  basketSize=0;
    MenuItem basketMenuItem;
    List<RestaurantBasketItem> basketItemList;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //Needed for refresh toolbar items
        basketMenuItem = menu.findItem(R.id.action_restaurant_sub_menu_goto_basket);
        loadBasketSize();
        return true;
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
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Retrofit api
        apiInterfaceRestaurantSubMenu2 = RetrofitApiClient2.getApiClient().create(RetrofitApiInterfaceRestaurantSubMenu2.class);
        apiInterfaceRestauSubMenuLoadBasketSize = RetrofitApiClient2.getApiClient().create(RetrofitApiInterfaceRestauSubMenuLoadBasketSize.class);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_submenu);
        txtSubMenuName = findViewById(R.id.textview_restaurant_submenu_topname);

        //GET Intent Here
        extras = getIntent().getExtras();

//        Toolbar Declare
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_restaurant_submenu);
        if (extras != null) {
            subMenuId = extras.getString("submenuId");
            subMenuName = extras.getString("submenuName");
            toolbar.setSubtitle(CurrentRestaurantClass.restaurantName);
            txtSubMenuName.setText(subMenuName);
        }

        toolbar.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        toolbar.setSubtitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        //Customer
        Customer customer = SharedPrefManager.getInstance(getApplicationContext()).getCustomer();
        customerId = customer.getCustomerId();

//      Progressbar
        progressBar = findViewById(R.id.progress_restaurant_submenu);
        progressBar.setVisibility(View.VISIBLE);

        //        More On Toolbar
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_back);
        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                }
        );

        recyclerView = findViewById(R.id.recycler_restaurant_submenu);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));

        //Extras Go on...
        if (subMenuId != null) {
            //Load Volley Data
            loadFoodList();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.restaurant_sub_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_restaurant_sub_menu_share:
                //Share App
                Utilities.shareApp(RestaurantSubmenuActivity.this);
                return true;
            case R.id.action_restaurant_sub_menu_goto_basket:
                //Do something
                Utilities.gotoRestaurantBasket(RestaurantSubmenuActivity.this);
                return true;
            case R.id.action_restaurant_sub_menu_my_account:
                //Do something
                Utilities.openBackOfficeAccount(RestaurantSubmenuActivity.this);
                return true;
            case R.id.action_restaurant_sub_menu_sign_out:
                //Function to perform SignOut
                SharedPrefManager.getInstance(RestaurantSubmenuActivity.this).logoutCustomer();
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //---------RETROFIT--------------------------(For Load Food List into Submenu)------------------------------RETROFIT
    private void loadFoodList() {
        Call<List<RestaurantFood>> call = apiInterfaceRestaurantSubMenu2.loadSubMenu(String.valueOf(subMenuId));
        call.enqueue(new Callback<List<RestaurantFood>>() {

            @Override
            public void onResponse(Call<List<RestaurantFood>> call, retrofit2.Response<List<RestaurantFood>> response) {
                Log.e("FoodList_TAG", "response: "+new Gson().toJson(response.body()) );
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.e("onSuccess", response.body().toString());
                        List<RestaurantFood> foodList = response.body();
                        //Creating a String array for the List
                        loadFoodListResponse(foodList);
                    } else {
                        Log.e("onEmptyResponse", "Returned empty response");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<RestaurantFood>> call, Throwable t) {
                //display error message
                Log.d("retrofit_log", "Failed! Error = " + t.getMessage());
                Toast.makeText(RestaurantSubmenuActivity.this, "Error. Check connection and try again", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void loadFoodListResponse(List<RestaurantFood> response) {
        //getting the whole json object from the response
        restaurantFoodList = new ArrayList<>();
        RestaurantFood food;
        //traversing through all the objects
        for (int i = 0; i < response.size(); i++) {
            //Accumulating items
            //getting product object from json array
            food = response.get(i);
            //adding the items to Basket List
            restaurantFoodList.add(new RestaurantFood(
                    food.getFoodId(),
                    food.getSubMenuId(),
                    food.getFoodName(),
                    food.getFoodPrice(),
                    food.getFoodDesc(),
                    food.getFoodPhoto()
            ));
        }
        //creating adapter object and setting it to recyclerview
        recyclerAdapter = new RestaurantSubmenuAdapter(RestaurantSubmenuActivity.this,restaurantFoodList);
        recyclerView.setAdapter(recyclerAdapter);
        //Bottom Reached Listener
//        recyclerAdapter.setOnBottomReachedListener(new OnBottomReachedListener() {
//            @Override
//            public void onBottomReached(int position) {
//                //your code goes here
//                Toast.makeText(getApplicationContext(),"No more data",Toast.LENGTH_SHORT).show();
//            }
//        });
        //Top reached listener
        recyclerAdapter.setOnTopReachedListener(new OnTopReachedListener() {
            @Override
            public void onTopReached(int position) {
                //your code goes here
                txtSubMenuName.setVisibility(View.VISIBLE);
            }
        });
        //Middle reached listener
        recyclerAdapter.setOnMiddleReachedListener(new OnMiddleReachedListener() {
            @Override
            public void onMiddleReached(int position) {
                //your code goes here
                txtSubMenuName.setVisibility(View.GONE);
            }
        });
        progressBar.setVisibility(View.GONE);
    }

    //Function to Load Basket Item Size
    private void loadBasketSize() {
        Call<List<RestaurantBasketItem>> call = apiInterfaceRestauSubMenuLoadBasketSize.getBasketSize(String.valueOf(customerId));
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
            basketMenuItem.setIcon(BadgeCounterIconConverter.convertLayoutToImage(RestaurantSubmenuActivity.this,basketSize,R.drawable.ic_product_basket));
        }else{
            basketMenuItem.setIcon(getResources().getDrawable(R.drawable.ic_product_basket));
        }

    }
}
//Retrofit API Interface
interface RetrofitApiInterfaceRestaurantSubMenu2 {
    @POST(URLs.URL_RESTAURANT_FOOD_LIST)
    @FormUrlEncoded
    Call<List<RestaurantFood>> loadSubMenu(@Field("submenuId") String subMenuId);
}
//Retrofit API Interface
interface RetrofitApiInterfaceRestauSubMenuLoadBasketSize {
    @POST(URLs.URL_RESTAURANT_BASKET_ITEM_LIST)
    @FormUrlEncoded
    Call<List<RestaurantBasketItem>> getBasketSize(@Field("customerId") String customerId);
}