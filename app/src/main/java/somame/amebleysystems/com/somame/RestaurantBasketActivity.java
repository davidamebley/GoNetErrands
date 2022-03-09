package somame.amebleysystems.com.somame;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import Adapter.ProductBasketItemAdapter;
import Adapter.RestaurantBasketItemAdapter;
import Model.Customer;
import Model.ProductBasketItem;
import Model.ProductOrderItem;
import Model.RestaurantBasketItem;
import SharedPref.SharedPrefManager;
import Util.RetrofitApiClient;
import Util.RetrofitApiClient2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class RestaurantBasketActivity extends AppCompatActivity {
    public static RetrofitApiInterfaceLoadRestaurantBasket apiInterfaceLoadBasket;
    public static RetrofitApiInterfaceDeleteRestaurantBasket apiInterfaceDeleteBasket;
    public static RetrofitApiInterfaceRestaurantClearBasket apiInterfaceClearBasket;
    RecyclerView recyclerView;
    RestaurantBasketItemAdapter recyclerAdapter;
    TextView textViewItemsCost, textViewDelivery, textViewSubTotal, textViewBasketTopName, textViewServiceCharge;
    Button buttonCheckout;
    String subTotal="", delivery="", total="";
    int itemTotal;
    List<RestaurantBasketItem> restaurantBasketItemList;
    ProgressBar progressBar;
    Intent newIntent;
    Toolbar toolbar;
    Double priceItemsCost, priceSubTotal, serviceCharge, priceDelivery;
    LinearLayout linearLayout, linearLayoutRecycler;
    int customerId;
    Customer customer;
    //Setting Currency for Price Text
    Locale locale = new Locale("en", "GH");
    NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
    private Menu menu;
    MenuItem basketMenuItem;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Retrofit api
        apiInterfaceLoadBasket = RetrofitApiClient2.getApiClient().create(RetrofitApiInterfaceLoadRestaurantBasket.class);
        apiInterfaceDeleteBasket = RetrofitApiClient.getApiClient().create(RetrofitApiInterfaceDeleteRestaurantBasket.class);
        apiInterfaceClearBasket = RetrofitApiClient.getApiClient().create(RetrofitApiInterfaceRestaurantClearBasket.class);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_basket);
        //View Init
        textViewServiceCharge = findViewById(R.id.textview_restaurant_basket_service_charge);
        textViewItemsCost = findViewById(R.id.textView_restaurant_basket_items_cost);
        textViewBasketTopName = findViewById(R.id.textview_restaurant_basket_top_name);
        textViewDelivery = findViewById(R.id.textview_restaurant_basket_delivery);
        textViewSubTotal = findViewById(R.id.textview_restaurant_basket_subtotal);
        buttonCheckout = findViewById(R.id.button_restaurant_basket_checkout);
//      imageButtonCheckout.getLayoutParams().width= (int)Math.round(Utilities.getScreenWidth(this)* 0.4);
        linearLayout = findViewById(R.id.layout_restaurant_basket_item_summary);
        linearLayoutRecycler = findViewById(R.id.layout_recycler_restaurant_basket_item_summary);
        priceSubTotal = Utilities.getBasketPriceTotal();
        //Customer
        customer = SharedPrefManager.getInstance(getApplicationContext()).getCustomer();
        customerId = customer.getCustomerId();
        //Check if user is logged in
        if (!SharedPrefManager.getInstance(this).isCustomerLoggedIn()) {
            finish();
            startActivity(new Intent(this, SignInActivity.class));
        }

//      Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_restaurant_basket);
        toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        toolbar.setSubtitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //      Progressbar
        progressBar = findViewById(R.id.progress_restaurant_basket);
        progressBar.setVisibility(View.VISIBLE);
        //Hide Summary
        linearLayout.setVisibility(View.INVISIBLE);

        recyclerView = findViewById(R.id.recycler_restaurant_basket);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        registerForContextMenu(recyclerView);
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.actionBarSize,typedValue,true);

        //Height of Screen
        adjustLayoutsHeightScreen();

        //CheckOut Button Click
        buttonCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (priceItemsCost< Utilities.ITEMS_MIN_COST){
                    Toast.makeText(RestaurantBasketActivity.this, "Value of selected items must be GHC "+Utilities.ITEMS_MIN_COST+" or more", Toast.LENGTH_LONG).show();
                }else{
                    startActivity(new Intent(RestaurantBasketActivity.this, CheckOutActivity.class));
                    finish();
                }
            }
        });

        //If Everything fine, Load Basket Items
        loadBasketItems();
    }

    // RETROFIT CONNECTION Clear Cart
    private void clearBasket(int customerId){
        /*
        Retrofit Network Connection
        * */
        Call<String> call = apiInterfaceClearBasket.clearBasket(String.valueOf(customerId));
        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                Log.e("ClearBasket_TAG", "response: "+new Gson().toJson(response.body()) );
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.e("onSuccess", response.body());
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(response.body());
                            //Success sign in
                            if(obj.optString("success").equals("true")){
                                //Clear Cart ========== ========

                            } else {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
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
                Toast.makeText(RestaurantBasketActivity.this, "Error. Check connection and try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showConfirmClearBasketDialog(){
        //      Use layout created in xml and inflate as our AlertDialog EditText
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm");
        builder.setMessage("Are you sure?");
//        builder.setView(view);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
        //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                clearBasket(customerId);
                dialog.dismiss();
                loadBasketItems();
            }
        });

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.restaurant_basket_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //Needed for refresh toolbar items
//        basketMenuItem = menu.findItem(R.id.action_product_wishlist_goto_basket);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_restaurant_basket_share:
                //Share App
                Utilities.shareApp(RestaurantBasketActivity.this);
                return true;
            case R.id.action_restaurant_basket_clear:
                //Do something
                showConfirmClearBasketDialog();
                return true;
            case R.id.action_restaurant_basket_my_account:
                //Do something
                Utilities.openBackOfficeAccount(RestaurantBasketActivity.this);
                return true;
            case R.id.action_restaurant_basket_sign_out:
                //Function to perform SignOut
                SharedPrefManager.getInstance(RestaurantBasketActivity.this).logoutCustomer();
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Function to Set Layout Heights to Fit Screen
    private void adjustLayoutsHeightScreen() {
        float screenHeight = Utilities.getScreenHeight(this);
        float screenWidth = Utilities.getScreenWidth(this);
        buttonCheckout.getLayoutParams().height = (int) Math.round(screenHeight*0.05);
        buttonCheckout.getLayoutParams().width = (int) Math.round(screenWidth*0.4);
        linearLayoutRecycler.getLayoutParams().height = (int) Math.round(screenHeight*0.62);
        linearLayout.getLayoutParams().height = (int) Math.round(screenHeight*0.374);
    }
    //Context(Right click) Menu
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = -1;
        try {
            position = ((RestaurantBasketItemAdapter)recyclerView.getAdapter()).getPosition();
        } catch (Exception e) {
            Log.d("Context Menu item", String.valueOf(position));
            return super.onContextItemSelected(item);
        }

        RestaurantBasketItem restaurantBasketItem = restaurantBasketItemList.get(position);
        Intent foodDetailIntent = new Intent(getApplicationContext(), RestaurantFoodDetailUpdateActivity.class);
//            We get the Id as a KEY value and send along
        foodDetailIntent.putExtra("itemId", restaurantBasketItem.getBasketItemId());
        foodDetailIntent.putExtra("productWishListId", restaurantBasketItem.getProductId());
        foodDetailIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);

        if(item.getTitle().equals(Utilities.DELETE)){
            int foodId = restaurantBasketItem.getBasketItemId();
            //Delete Item from WishList
            deleteFromRestaurantBasket(foodId);
        }
        if(item.getTitle().equals(Utilities.UPDATE_PRODUCT)) {
            getApplicationContext().startActivity(foodDetailIntent);
            this.finish();
        }
        return super.onContextItemSelected(item);
    }

    //Function to remove product from basket
    private void deleteFromRestaurantBasket(final int foodId) {
        /*
        Retrofit Network Connection
        * */
        Call<String> call = apiInterfaceDeleteBasket.deleteBasketItem(String.valueOf(foodId), String.valueOf(customerId));
        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                Log.e("Del_Basket_TAG", "response: "+new Gson().toJson(response.body()) );
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
                                loadBasketItems();
                            } else {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                //After remove fail, reload
                                loadBasketItems();
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
                Toast.makeText(RestaurantBasketActivity.this, "Error. Check connection and try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //    ---------------------RETROFIT--------------(LOAD BASKET)----------------RETROFIT--------------------RETROFIT

    private void loadBasketItems(){
        Call<List<RestaurantBasketItem>> call = apiInterfaceLoadBasket.loadBasketItems(String.valueOf(customerId));
        call.enqueue(new Callback<List<RestaurantBasketItem>>() {

            @Override
            public void onResponse(Call<List<RestaurantBasketItem>> call, retrofit2.Response<List<RestaurantBasketItem>> response) {
                Log.e("Basket_TAG", "response: "+new Gson().toJson(response.body()) );
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
                //Show Summary
//                linearLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(RestaurantBasketActivity.this, "Error. Check connection and try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBasketResponse(List<RestaurantBasketItem> response) {
        //getting the whole json object from the response
        restaurantBasketItemList = new ArrayList<>();
        Utilities.productOrderList = new ArrayList<>();
        priceItemsCost =0.0;
        itemTotal = 0;
        //JSONObject productBasketItem
        RestaurantBasketItem restaurantBasketItem;
        //traversing through all the objects
        for (int i = 0; i < response.size(); i++) {
            //Accumulating items
            itemTotal++;
            //getting product object from json array
            restaurantBasketItem = response.get(i);
            //Accumulating prices of items
            priceItemsCost = priceItemsCost + (Double.parseDouble(restaurantBasketItem.getPrice()) * restaurantBasketItem.getQuantity());
            //adding the items to Basket List
            restaurantBasketItemList.add(new RestaurantBasketItem(
                    restaurantBasketItem.getBasketItemId(),
                    restaurantBasketItem.getProductId(),
                    restaurantBasketItem.getCustomerId(),
                    restaurantBasketItem.getName(),
                    restaurantBasketItem.getPrice(),
                    restaurantBasketItem.getLogo(),
                    restaurantBasketItem.getQuantity()
            ));
            //Adding to Product order List
            /*Utilities.productOrderList.add(new ProductOrderItem(restaurantBasketItem.getBasketItemId(),
                    String.valueOf(restaurantBasketItem.getCustomerId()),
                    restaurantBasketItem.getName(),
                    restaurantBasketItem.getPrice(),
                    restaurantBasketItem.getQuantity()));*/
        }
        //Service charge function
        serviceCharge = ((Utilities.getServiceCharge(priceItemsCost)));
        textViewServiceCharge.setText(fmt.format(serviceCharge));

        Utilities.setItemsCost(priceItemsCost);
        Utilities.setPriceSubTotal(priceItemsCost+ serviceCharge);
        priceSubTotal = Utilities.getBasketPriceSubtotal();
//                            //Setting the Top title plus Item total
        textViewBasketTopName.setText(getString(R.string.restaurant_basket));
        //Assign Basket Item total in Utilities
        Utilities.setProdBasketItemTotal(itemTotal);

        StringBuilder textViewTopName = new StringBuilder();
        textViewTopName.append(textViewBasketTopName.getText()).append(" (").append(itemTotal).append(")");
        textViewBasketTopName.setText(textViewTopName);
        if(itemTotal<1){
            Toast.makeText(getApplicationContext(),"Your basket is empty. Add items to continue",Toast.LENGTH_SHORT).show();
        }
        //creating adapter object and setting it to recyclerview
        recyclerAdapter = new RestaurantBasketItemAdapter(restaurantBasketItemList,getApplicationContext());

        recyclerView.setAdapter(recyclerAdapter);
        textViewItemsCost.setText(fmt.format(priceItemsCost));

        textViewSubTotal.setText(fmt.format(priceSubTotal));
        textViewServiceCharge.setText(fmt.format(serviceCharge));

        //Show Summary
        linearLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }
}

//Retrofit API Interface
interface RetrofitApiInterfaceLoadRestaurantBasket {
    @POST(URLs.URL_RESTAURANT_BASKET_ITEM_LIST)
    @FormUrlEncoded
    Call<List<RestaurantBasketItem>> loadBasketItems(@Field("customerId") String customerId);
}
//Retrofit API Interface
interface RetrofitApiInterfaceDeleteRestaurantBasket {
    @POST(URLs.URL_DELETE_FOOD_FROM_RESTAURANT_BASKET)
    @FormUrlEncoded
    Call<String> deleteBasketItem(@Field("itemId") String productId, @Field("customerId") String customerId);
}
//Retrofit API Interface
interface RetrofitApiInterfaceRestaurantClearBasket {
    @POST(URLs.URL_CLEAR_RESTAURANT_BASKET)
    @FormUrlEncoded
    Call<String> clearBasket(@Field("customerId") String customerId);
}