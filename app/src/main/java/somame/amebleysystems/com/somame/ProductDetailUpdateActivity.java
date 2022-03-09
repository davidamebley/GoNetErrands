package somame.amebleysystems.com.somame;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
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

import Model.Customer;
import Model.ProductBasketItem;
import Model.RestaurantBasketItem;
import SharedPref.SharedPrefManager;
import Util.RetrofitApiClient;
import Util.RetrofitApiClient2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class ProductDetailUpdateActivity extends AppCompatActivity {
    public static RetrofitApiInterfaceUpdateBasketItem apiInterfaceUpdateBasketItem;
    public static RetrofitApiInterfaceLoadProductBasketItemDetail apiInterfaceLoadProductBasketItemDetail;
    private Bundle extras;
    private Menu menu;
    TextView textViewProductDesc, textViewProductManufacturer, textViewComment;
    AppCompatTextView textViewCommentTitle, textViewProductName, textViewProductPrice;
    ImageView imageViewProductImage, imageViewComment;
    Button buttonUpdateItem;
    LinearLayout linearLayoutComment;
    private int productId=0, subCatId=0, quantity, productWishListId=0, customerId;
    String productName="", productPrice="", productDesc="", productPhoto="", manufacturer="", comment="";
    ElegantNumberButton numberButton;
    ProgressBar progressBar;
    Toolbar toolbar;
    int basketSize=0;
    List<ProductBasketItem> basketItemList;

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Retrofit Api interface
        apiInterfaceUpdateBasketItem = RetrofitApiClient.getApiClient().create(RetrofitApiInterfaceUpdateBasketItem.class);
        apiInterfaceLoadProductBasketItemDetail = RetrofitApiClient2.getApiClient().create(RetrofitApiInterfaceLoadProductBasketItemDetail.class);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail_update);

        //View Init
        imageViewProductImage = findViewById(R.id.imageview_product_detail_update);
        textViewProductPrice = findViewById(R.id.textview_product_detail_update_price);
        textViewProductName = findViewById(R.id.textview_product_detail_update_name);
        textViewCommentTitle = findViewById(R.id.textview_product_detail_update_comment_title);
        textViewProductDesc = findViewById(R.id.textview_product_detail_update_desc);
        textViewProductManufacturer = findViewById(R.id.textview_product_detail_update_manufacturer);
        imageViewComment = findViewById(R.id.imageView_product_detail_update_comments);
        linearLayoutComment = findViewById(R.id.layout_product_detail_update_comments);
        textViewComment = findViewById(R.id.textview_product_detail_update_comment);
        numberButton = findViewById(R.id.number_button_product_update_detail);
        progressBar = findViewById(R.id.progress_product_detail_update);
        buttonUpdateItem = findViewById(R.id.button_product_detail_update);
        comment="";

        //        Customer
        Customer customer = SharedPrefManager.getInstance(getApplicationContext()).getCustomer();
        customerId = customer.getCustomerId();

        //Check if user is logged in
        if (!SharedPrefManager.getInstance(this).isCustomerLoggedIn()) {
            finish();
            startActivity(new Intent(this, SignInActivity.class));
        }
        //Extras
        extras = getIntent().getExtras();
        if (extras!=null){
            productId= extras.getInt("productId");
            productWishListId = extras.getInt("productWishListId");
            productPhoto = extras.getString("productPhoto");
        }

        //Load Basket Size
//        loadBasketFromDB();

//      Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_product_detail_update);

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
                finish();

            }
        });


        //Set ClickListener for Number Button
        numberButton.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
//                Toast.makeText(getApplicationContext(),"Val change",Toast.LENGTH_SHORT).show();
                quantity = getProductQuantity();
            }
        });

        //Setting ImageView Size
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.actionBarSize,typedValue,true);
        int actionBarSize = getResources().getDimensionPixelSize(typedValue.resourceId);
        int imageHeight = actionBarSize*3;
        int imageWidth = actionBarSize*2;
        imageViewProductImage.getLayoutParams().height = imageHeight;
        imageViewProductImage.getLayoutParams().width = imageWidth;

        linearLayoutComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCommentAlertDialog();
            }
        });

        //CardView Add To Basket Tweaks
        buttonUpdateItem.setText(R.string.update_basket_item);
        buttonUpdateItem.getLayoutParams().height= (int)Math.round(Utilities.getScreenHeight(this)* 0.06);

        //Adjust other layouts sizes
        adjustLayoutsHeightScreen();

        //Add Item to Basket Click
        buttonUpdateItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(),"Item added to basket",Toast.LENGTH_SHORT).show();
                updateBasketItem(v);
            }
        });

        //Setting Received Extras to Display
        if (productId != 0){
            //Load Basket Item Detail
            loadBasketItemDetail(productId);
            RequestOptions requestOptions = new RequestOptions();
            //loading the image
            Glide.with(this)
                    .load(productPhoto)
                    .apply(requestOptions)
                    .into(imageViewProductImage);

        }else{
            //Go back if No food Id passed
            onBackPressed();
        }

    }

    //Function to Set Layout Heights to Fit Screen
    private void adjustLayoutsHeightScreen() {
        float screenHeight = Utilities.getScreenHeight(this);
        float screenWidth = Utilities.getScreenWidth(this);
        numberButton.getLayoutParams().height = (int) Math.round(screenHeight*0.05);
        textViewCommentTitle.getLayoutParams().width = (int) Math.round(screenWidth*0.60);
        linearLayoutComment.getLayoutParams().width = (int) Math.round(screenWidth*0.35);
        imageViewComment.getLayoutParams().height = (int) Math.round(screenHeight*0.02);
    }

    //----------------RETROFIT----------------Function to load Basket Item and Details-------RETROFIT
    private void loadBasketItemDetail(final int productId) {
        Call<List<ProductBasketItem>> call = apiInterfaceLoadProductBasketItemDetail.loadItemDetail(String.valueOf(customerId), String.valueOf(productId));
        call.enqueue(new Callback<List<ProductBasketItem>>() {
            @Override
            public void onResponse(Call<List<ProductBasketItem>> call, retrofit2.Response<List<ProductBasketItem>> response) {
                Log.e("Basket_Size", "response: "+new Gson().toJson(response.body()) );
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
                Toast.makeText(ProductDetailUpdateActivity.this, "Error. Check connection and try again", Toast.LENGTH_SHORT).show();
            }
        });
    }
    //Load JSON data from response
    private void loadBasketResponse(List<ProductBasketItem> response) {
        //getting the whole json object from the response
        basketItemList = new ArrayList<>();
        ProductBasketItem basketItem;
        //traversing through all the object
        for (int i = 0; i < response.size(); i++) {
            basketItem = response.get(i);
            textViewProductName.setText(basketItem.getName());
            textViewProductDesc.setText(basketItem.getDescription());
            textViewProductManufacturer.setText(basketItem.getManufacturer());
            comment = basketItem.getComment();
            numberButton.setNumber(String.valueOf(basketItem.getQuantity()));
            RequestOptions requestOptions = new RequestOptions();
            //loading the image
            Glide.with(getApplicationContext())
                    .load(basketItem.getLogo())
                    .apply(requestOptions)
                    .into(imageViewProductImage);

            //Setting Currency for Price Text
            Locale locale = new Locale("en", "GH");
            NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
            textViewProductPrice.setText(fmt.format(Double.parseDouble(basketItem.getPrice())));
        }
        textViewComment.setText((comment.equalsIgnoreCase(""))?"None":comment);
        progressBar.setVisibility(View.GONE);
    }

    //Function to Set and Return Quantity selected
    private int getProductQuantity() {
        return Integer.parseInt(numberButton.getNumber());
    }

    //----------------------RETROFIT-------------------(Function to add item to Basket)----------------RETROFIT-----------------
    private void updateBasketItem(final View view) {
        /*
        Retrofit Network Connection
        * */
        Call<String> call = apiInterfaceUpdateBasketItem.updateItem(String.valueOf(productId), String.valueOf(customerId), String.valueOf(numberButton.getNumber()), String.valueOf(textViewComment.getText()));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
//                Log.e("Update_Basket", "response: "+new Gson().toJson(response.body()) );
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.e("Update_Basket_response", response.body());
                        JSONObject obj = null;
//                        JSONArray array;
                        try {
//                            array = new JSONArray(response.body());
                            obj = new JSONObject(response.body());
                            //Success
                            if(obj.optString("success").equals("true")){
//                            if(array){
                                Toast.makeText(getApplicationContext(),obj.getString("message"),Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ProductDetailUpdateActivity.this, ProductBasketActivity.class));
                                invalidateOptionsMenu();
                                finish();
                            } else {
                                invalidateOptionsMenu();
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
                Toast.makeText(ProductDetailUpdateActivity.this, "Error. Check connection and try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Function to Display the Comment Dialog
    private void showCommentAlertDialog() {
//      Use layout created in xml and inflate as our AlertDialog EditText
        View view = LayoutInflater.from(this).inflate(R.layout.product_comment_layout, null);

        final EditText edittext = view.findViewById(R.id.edittextdialog_product_comment);
        final AlertDialog.Builder alertdialog = new AlertDialog.Builder(this, R.style.Theme_AppCompat_DayNight_Dialog);

        alertdialog.setTitle("Special requests or preferences");
        alertdialog.setView(view);

        comment = textViewComment.getText().toString();
        edittext.setTextColor(Color.BLACK);
        edittext.setText(comment);
        //Okay Button
        alertdialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Setting comment text to comment variable
                comment= edittext.getText().toString().trim();

                textViewComment.setText((comment.equalsIgnoreCase(""))?"None":comment);
            }
        });
        //Cancel Button
        alertdialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Cancel option.
                dialog.cancel();
            }
        });

        alertdialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    //Menu On Toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_product_detail_update, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //Needed for refresh toolbar items
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){

            case R.id.action_product_detail_update_share:
                Utilities.shareApp(this);
                return true;
//            case R.id.action_product_detail_update_goto_basket:
//                Utilities.gotoProductBasket(getApplicationContext());
//                this.finish();
//                return true;
            case R.id.action_product_detail_update_sign_out:
                //Function to perform SignOut
                SharedPrefManager.getInstance(getApplicationContext()).logoutCustomer();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

//Retrofit API Interface
interface RetrofitApiInterfaceUpdateBasketItem {
    @POST(URLs.URL_UPDATE_PRODUCT_BASKET_ITEM)
    @FormUrlEncoded
    Call<String> updateItem(@Field("productId") String foodId, @Field("customerId") String customerId, @Field("quantity") String quantity, @Field("comment") String comment);
}
//Retrofit API Interface
interface RetrofitApiInterfaceLoadProductBasketItemDetail {
    @POST(URLs.URL_PRODUCT_BASKET_ITEM_DETAIL)
    @FormUrlEncoded
    Call<List<ProductBasketItem>> loadItemDetail(@Field("customerId") String customerId, @Field("productId") String productId);
}