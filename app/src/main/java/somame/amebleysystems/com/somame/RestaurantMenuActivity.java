package somame.amebleysystems.com.somame;

import android.content.Intent;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
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

import Adapter.RestaurantSubMenuItemAdapter;
import Interface.RetrofitInterfaceLoadRestaurantBasketSize;
import Model.Customer;
import Model.RestaurantBasketItem;
import Model.RestaurantMainMenuItem;
import Model.RestaurantMenuItemClass;
import Model.RestaurantSubMenuItem;
import SharedPref.SharedPrefManager;
import Util.RetrofitApiClient;
import Util.RetrofitApiClient2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

//import static Model.RestaurantMenuItemClass.setSubMenuItems;

public class RestaurantMenuActivity extends AppCompatActivity {
    public static RetrofitApiInterfaceRestaurantSubMenu apiInterfaceRestaurantSubMenu;
    public static RetrofitApiInterfaceRestaurantMenu apiInterfaceRestaurantMenu;
    public static RetrofitApiInterfaceRestauMenuLoadBasketSize apiInterfaceLoadRestaurantBasketSize;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    private Bundle extras;
    private Integer restaurantId;
    private String restaurantName;
    private ArrayList<JSONObject> mainMenuItems;
    private ArrayList<JSONObject> subMenuItems;
    Toolbar toolbar;
    private ArrayList<JSONObject> mainMenu_Items;
    private ArrayList<JSONObject> subMenu_Items;
    private int customerId;
    private Menu menu;
    private int  basketSize=0;
    MenuItem basketMenuItem;
    List<RestaurantBasketItem> basketItemList;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //Needed for refresh toolbar items
        basketMenuItem = menu.findItem(R.id.action_restaurant_menu_goto_basket);
        loadBasketSize();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Refresh Menu Items
//        loadBasketSize();
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
        //Retrofit api
        apiInterfaceRestaurantSubMenu = RetrofitApiClient.getApiClient().create(RetrofitApiInterfaceRestaurantSubMenu.class);
        apiInterfaceRestaurantMenu = RetrofitApiClient.getApiClient().create(RetrofitApiInterfaceRestaurantMenu.class);
        apiInterfaceLoadRestaurantBasketSize = RetrofitApiClient2.getApiClient().create(RetrofitApiInterfaceRestauMenuLoadBasketSize.class);
        //Assign URL to apiInterface
//        apiInterfaceLoadRestaurantBasketSize.url = "";

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_menu);
        //GET Intent Here
        extras = getIntent().getExtras();

//        Toolbar Declare
        toolbar = (Toolbar) findViewById(R.id.toolbar_restaurant_menu);
        if (extras != null) {
            restaurantName = extras.getString("restaurantName");
            toolbar.setSubtitle(restaurantName);
        }

        toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        toolbar.setSubtitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        mainMenu_Items = new ArrayList<>();
        subMenu_Items = new ArrayList<>();

        //Customer
        Customer customer = SharedPrefManager.getInstance(getApplicationContext()).getCustomer();
        customerId = customer.getCustomerId();

//      Progressbar
        progressBar = findViewById(R.id.progress_restaurant_menu);
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

        //Extras Go on...
        if (extras != null) {
            restaurantId = extras.getInt("restaurantId");
        }
        if (restaurantId != null) {
            //Load Menu Functions
            loadRestaurantMenu();
            loadRestaurantSubMenu();

        }
    }
    //When Return Back Press data received
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {

            }
        }
    }

    //Function to Perform Operations
    private void prepareMenu() {
        ArrayList<RestaurantMainMenuItem> newMainMenuItems = new ArrayList<>();
        ArrayList<RestaurantSubMenuItem> newSubMenuItems;

        this.mainMenu_Items = RestaurantMenuItemClass.getMainMenuItems();
        this.subMenu_Items = RestaurantMenuItemClass.getSubMenuItems();

        for (JSONObject mainItem : mainMenu_Items) {
            try {
                newSubMenuItems = new ArrayList<>();
                for (JSONObject subItem : subMenu_Items) {
                    if (subItem.getInt("menu_item_id") == mainItem.getInt("menu_item_id")) {
                        int tempMenuId = subItem.getInt("menu_item_id");
                        int tempSubMenuId = subItem.getInt("submenu_item_id");
                        String tempSubMenuDesc = subItem.getString("submenu_item_desc");
                        String tempSubMenuPhoto = subItem.getString("submenu_item_photo");
                        //Adding to each Submenu
                        newSubMenuItems.add(new RestaurantSubMenuItem(tempMenuId, tempSubMenuId, tempSubMenuDesc, tempSubMenuPhoto));
                    }
                }
                //Adding SubMenuList to Main Menu Item
                RestaurantMainMenuItem mainMenuItem = new RestaurantMainMenuItem(mainItem.getString("menu_item_desc"), newSubMenuItems);
                newMainMenuItems.add(mainMenuItem);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        recyclerView = findViewById(R.id.recycler_restaurant_menu);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Adapter Calling
        RestaurantSubMenuItemAdapter adapter = new RestaurantSubMenuItemAdapter(RestaurantMenuActivity.this, newMainMenuItems);
        //Open First Menu By default
        recyclerView.setAdapter(adapter);
//

    }

    //Function to Load Basket Item Size
    private void loadBasketSize() {
        Call<List<RestaurantBasketItem>> call = apiInterfaceLoadRestaurantBasketSize.getBasketSize(String.valueOf(customerId));
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
            basketMenuItem.setIcon(BadgeCounterIconConverter.convertLayoutToImage(RestaurantMenuActivity.this,basketSize,R.drawable.ic_product_basket));
        }else{
            basketMenuItem.setIcon(getResources().getDrawable(R.drawable.ic_product_basket));
        }

    }


    //    RETROFIT---------------------------(For Loading Restaurant Menu---------------------------RETROFIT)
    private void loadRestaurantMenu() {
        /*
        Retrofit Network Connection
        * */
        Call<String> call = apiInterfaceRestaurantMenu.loadMenu(String.valueOf(restaurantId));
        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                Log.e("Menu_TAG", "response: "+new Gson().toJson(response.body()) );
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.e("onSuccess", response.body());
                        JSONObject obj = null;
                        try {
                            JSONArray array = new JSONArray(response.body());
                            //Main menu
                            mainMenuItems = new ArrayList<>();
                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {
                                //getting restaurant object from json array
                                JSONObject restaurant = array.getJSONObject(i);
                                //adding the Main Menu list
                                mainMenuItems.add(restaurant);
                            }
                            //Custom function to Set Main Menu Items to Global Variable
                            RestaurantMenuItemClass.setMainMenuItems(mainMenuItems);
                            //Function To Perform Main Operation
                            prepareMenu();
                            progressBar.setVisibility(View.GONE);
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
                progressBar.setVisibility(View.GONE);
                Toast.makeText(RestaurantMenuActivity.this, "Error. Check connection and try again", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadRestaurantSubMenu(){
        /*
        Retrofit Network Connection
        * */
        Call<String> call = apiInterfaceRestaurantSubMenu.loadSubMenu(String.valueOf(restaurantId));
        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                Log.e("SubMenu_TAG", "response: "+new Gson().toJson(response.body()) );
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.e("onSuccess", response.body());
                        JSONObject obj = null;
                        try {
                            JSONArray array = new JSONArray(response.body());
                            subMenuItems = new ArrayList<>();
                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {
                                //getting restaurant object from json array
                                JSONObject subMenu = array.getJSONObject(i);
                                //adding the Main Menu list
                                subMenuItems.add(subMenu);
                            }
                            //Custom function to Set Sub Menu Items to Global Variable
                            RestaurantMenuItemClass.setSubMenuItems(subMenuItems);
                            prepareMenu();

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
                Toast.makeText(RestaurantMenuActivity.this, "Error. Check connection and try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.restaurant_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_restaurant_menu_share:
                //Share App
                Utilities.shareApp(RestaurantMenuActivity.this);
                return true;
            case R.id.action_restaurant_menu_goto_basket:
                //Do something
                Utilities.gotoRestaurantBasket(RestaurantMenuActivity.this);
                return true;
            case R.id.action_restaurant_menu_my_account:
                //Do something
                Utilities.openBackOfficeAccount(RestaurantMenuActivity.this);
                return true;
            case R.id.action_restaurant_menu_sign_out:
                //Function to perform SignOut
                SharedPrefManager.getInstance(RestaurantMenuActivity.this).logoutCustomer();
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
//Retrofit API Interface
interface RetrofitApiInterfaceRestaurantSubMenu {
    @POST(URLs.URL_RESTAURANT_SUBMENU_LIST)
    @FormUrlEncoded
    Call<String> loadSubMenu(@Field("restaurantId") String restaurantId);
}
//Retrofit API Interface
interface RetrofitApiInterfaceRestaurantMenu {
    @POST(URLs.URL_RESTAURANT_MENU_LIST)
    @FormUrlEncoded
    Call<String> loadMenu(@Field("restaurantId") String restaurantId);
}

//Retrofit API Interface
interface RetrofitApiInterfaceRestauMenuLoadBasketSize {
    @POST(URLs.URL_RESTAURANT_BASKET_ITEM_LIST)
    @FormUrlEncoded
    Call<List<RestaurantBasketItem>> getBasketSize(@Field("customerId") String customerId);
}