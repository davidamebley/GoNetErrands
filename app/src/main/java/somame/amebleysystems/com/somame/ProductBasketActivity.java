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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import Adapter.ProductBasketItemAdapter;
import Model.Customer;
import Model.ProductBasketItem;
import Model.ProductOrderItem;
import SharedPref.SharedPrefManager;
import Util.RetrofitApiClient;
import Util.RetrofitApiClient2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class ProductBasketActivity extends AppCompatActivity {
    public static RetrofitApiInterfaceLoadBasket apiInterfaceLoadBasket;
    public static RetrofitApiInterfaceDeleteBasket apiInterfaceDeleteBasket;
    public static RetrofitApiInterfacePBClearBasket apiInterfaceClearBasket;
    RecyclerView recyclerView;
    ProductBasketItemAdapter recyclerAdapter;
    TextView textViewItemsCost, textViewDelivery, textViewSubTotal, textViewBasketTopName, textViewServiceCharge;
    Button buttonCheckout;
    String subTotal="", shipping="", total="";
    int itemTotal;
    List<ProductBasketItem> productBasketItemList;
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
        apiInterfaceLoadBasket = RetrofitApiClient2.getApiClient().create(RetrofitApiInterfaceLoadBasket.class);
        apiInterfaceDeleteBasket = RetrofitApiClient.getApiClient().create(RetrofitApiInterfaceDeleteBasket.class);
        apiInterfaceClearBasket = RetrofitApiClient.getApiClient().create(RetrofitApiInterfacePBClearBasket.class);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_basket);

        //View Init
        textViewServiceCharge = findViewById(R.id.textview_product_basket_service_charge);
        textViewItemsCost = findViewById(R.id.textView_product_basket_items_cost);
        textViewBasketTopName = findViewById(R.id.textview_product_basket_top_name);
        textViewDelivery = findViewById(R.id.textview_product_basket_delivery);
        textViewSubTotal = findViewById(R.id.textview_product_basket_subtotal);
        buttonCheckout = findViewById(R.id.button_product_basket_checkout);
        linearLayout = findViewById(R.id.layout_prod_basket_item_summary);
        linearLayoutRecycler = findViewById(R.id.layout_recycler_prod_basket_item_summary);
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
        toolbar = (Toolbar) findViewById(R.id.toolbar_product_basket);

//        toolbar.setSubtitle(CurrentRestaurantClass.restaurantName);
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
        progressBar = findViewById(R.id.progress_product_basket);
        progressBar.setVisibility(View.VISIBLE);
        //Hide Summary
        linearLayout.setVisibility(View.INVISIBLE);

        recyclerView = findViewById(R.id.recycler_product_basket);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        registerForContextMenu(recyclerView);
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.actionBarSize,typedValue,true);

        //Function to Set Layout Heights to Fit Screen
        adjustLayoutsHeightScreen();

        //CheckOut Button Click
        buttonCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (priceItemsCost< Utilities.ITEMS_MIN_COST){
                    Toast.makeText(ProductBasketActivity.this, "Value of selected items must be GHC "+Utilities.ITEMS_MIN_COST+" or more", Toast.LENGTH_LONG).show();
                }else{
                startActivity(new Intent(ProductBasketActivity.this, CheckOutActivity.class));
                finish();
                }
            }
        });

        //If Everything fine, Load Basket Items
        loadBasketItems();
    }

    //Context(Right click) Menu
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = -1;
        try {
            position = ((ProductBasketItemAdapter)recyclerView.getAdapter()).getPosition();
        } catch (Exception e) {
            Log.d("Context Menu item", String.valueOf(position));
            return super.onContextItemSelected(item);
        }

        ProductBasketItem productBasketItem = productBasketItemList.get(position);
        Intent productDetailIntent = new Intent(getApplicationContext(), ProductDetailUpdateActivity.class);
//            We get the Id as a KEY value and send along
        productDetailIntent.putExtra("productId", productBasketItem.getBasketItemId());
        productDetailIntent.putExtra("productWishListId", productBasketItem.getProductId());
        productDetailIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);

        if(item.getTitle().equals(Utilities.DELETE)){
//            getApplicationContext().startActivity(productDetailIntent);
            int productId = productBasketItem.getBasketItemId();
            //Delete Item from WishList
            deleteFromProductBasket(productId);
        }
        if(item.getTitle().equals(Utilities.UPDATE_PRODUCT)) {
//            Toast.makeText(getApplicationContext(), "Update Product", Toast.LENGTH_SHORT).show();
            getApplicationContext().startActivity(productDetailIntent);
            this.finish();
        }
        return super.onContextItemSelected(item);
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
        getMenuInflater().inflate(R.menu.product_basket_menu, menu);
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
            case R.id.action_product_basket_share:
                //Share App
                Utilities.shareApp(ProductBasketActivity.this);
                return true;
            case R.id.action_product_basket_clear:
                //Do something
                showConfirmClearBasketDialog();
                return true;
            case R.id.action_product_basket_my_account:
                //Do something
                Utilities.openBackOfficeAccount(ProductBasketActivity.this);
                return true;
            case R.id.action_product_basket_sign_out:
                //Function to perform SignOut
                SharedPrefManager.getInstance(ProductBasketActivity.this).logoutCustomer();
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

    //Function to remove product from basket
    private void deleteFromProductBasket(final int productId) {
        /*
        Retrofit Network Connection
        * */
        Call<String> call = apiInterfaceDeleteBasket.deleteBasketItem(String.valueOf(productId), String.valueOf(customerId));
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
                Toast.makeText(ProductBasketActivity.this, "Error. Check connection and try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBasketItems(){
        Call<List<ProductBasketItem>> call = apiInterfaceLoadBasket.loadBasketItems(String.valueOf(customerId));
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
                progressBar.setVisibility(View.GONE);
                //Show Summary
//                linearLayout.setVisibility(View.VISIBLE);

                Toast.makeText(ProductBasketActivity.this, "Error. Check connection and try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBasketResponse(List<ProductBasketItem> response) {
        //getting the whole json object from the response
        productBasketItemList = new ArrayList<>();
        Utilities.productOrderList = new ArrayList<>();
        priceItemsCost =0.0;
        itemTotal = 0;

        //JSONObject productBasketItem
        ProductBasketItem productBasketItem;

        //traversing through all the objects
        for (int i = 0; i < response.size(); i++) {
            //Accumulating items
            itemTotal++;
            //getting product object from json array
            productBasketItem = response.get(i);
            //Accumulating prices of items
            priceItemsCost = priceItemsCost + (Double.parseDouble(productBasketItem.getPrice()) * productBasketItem.getQuantity());
            //adding the items to Basket List
            productBasketItemList.add(new ProductBasketItem(
                    productBasketItem.getBasketItemId(),
                    productBasketItem.getProductId(),
                    productBasketItem.getCustomerId(),
                    productBasketItem.getName(),
                    productBasketItem.getPrice(),
                    productBasketItem.getLogo(),
                    productBasketItem.getQuantity()
            ));
            //Adding to Product order List
            Utilities.productOrderList.add(new ProductOrderItem(productBasketItem.getBasketItemId(),
                    String.valueOf(productBasketItem.getCustomerId()),
                    productBasketItem.getName(),
                    productBasketItem.getPrice(),
                    productBasketItem.getQuantity()));
        }
        //Service charge function
        serviceCharge = ((Utilities.getServiceCharge(priceItemsCost)));
        textViewServiceCharge.setText(fmt.format(serviceCharge));

        Utilities.setItemsCost(priceItemsCost);
        Utilities.setPriceSubTotal(priceItemsCost+ serviceCharge);
        priceSubTotal = Utilities.getBasketPriceSubtotal();
        //Setting the Top title plus Item total
        textViewBasketTopName.setText(R.string.product_basket);
        //Assign Basket Item total in Utilities
        Utilities.setProdBasketItemTotal(itemTotal);
        StringBuilder textViewTopName = new StringBuilder();
        textViewTopName.append(textViewBasketTopName.getText()).append(" (").append(itemTotal).append(")");
        textViewBasketTopName.setText(textViewTopName);
        if(itemTotal<1){
            Toast.makeText(getApplicationContext(),"Your basket is empty. Add items to continue",Toast.LENGTH_SHORT).show();
        }
        //creating adapter object and setting it to recyclerview
        recyclerAdapter = new ProductBasketItemAdapter(productBasketItemList,getApplicationContext());

        recyclerView.setAdapter(recyclerAdapter);
        textViewItemsCost.setText(fmt.format(priceItemsCost));
        textViewSubTotal.setText(fmt.format(priceSubTotal));
        textViewServiceCharge.setText(fmt.format(serviceCharge));

        progressBar.setVisibility(View.GONE);
        //Show Summary
        linearLayout.setVisibility(View.VISIBLE);
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
                Toast.makeText(ProductBasketActivity.this, "Error. Check connection and try again", Toast.LENGTH_SHORT).show();
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
}

//Retrofit API Interface
interface RetrofitApiInterfaceLoadBasket {
    @POST(URLs.URL_PRODUCT_BASKET_ITEM_LIST)
    @FormUrlEncoded
    Call<List<ProductBasketItem>> loadBasketItems(@Field("customerId") String customerId);
}
//Retrofit API Interface
interface RetrofitApiInterfaceDeleteBasket {
    @POST(URLs.URL_DELETE_PRODUCT_FROM_PRODUCT_BASKET)
    @FormUrlEncoded
    Call<String> deleteBasketItem(@Field("productId") String productId, @Field("customerId") String customerId);
}
//Retrofit API Interface
interface RetrofitApiInterfacePBClearBasket {
    @POST(URLs.URL_CLEAR_PRODUCT_BASKET)
    @FormUrlEncoded
    Call<String> clearBasket(@Field("customerId") String customerId);
}