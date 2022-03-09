package somame.amebleysystems.com.somame;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
//import com.google.android.material.Snackbar;
import androidx.core.content.ContextCompat;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Model.Customer;
import Model.ProductBasketItem;
import SharedPref.SharedPrefManager;
import Util.RetrofitApiClient;
import Util.RetrofitApiClient2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class ProductDetailActivity extends AppCompatActivity {
    public static RetrofitApiInterfaceLoadProductBasketSize apiInterfaceloadBasketSize;
    public static RetrofitApiInterfaceAddToProdBasket apiInterfaceAddToBasket;
    public static RetrofitApiInterfaceAddToWishList apiInterfaceAddToWishList;
    public static RetrofitApiInterfaceConfirmWishList apiInterfaceConfirmWishList;
    private Bundle extras;
    private Menu menu;
    TextView textViewProductDesc,textViewProductManufacturer, textViewComment;
    AppCompatTextView textViewCommentTitle, textViewProductName, textViewProductPrice;
    ImageView imageViewProductImage, imageViewComment;
    Button buttonAddToBasket;
    LinearLayout linearLayoutComment;
    private int productId=0, subCatId=0, quantity, basketSize=0, customerId;
    String productName="", productPrice="", productDesc="", productPhoto="", manufacturer="", comment="";
    ElegantNumberButton numberButton;
    Toolbar toolbar;
    MenuItem basketMenuItem;
    AlertDialog waitDialog = null;
    List<ProductBasketItem> basketItemList;

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
    protected void onCreate(Bundle savedInstanceState) {
        //Retrofit Api interface
        apiInterfaceloadBasketSize = RetrofitApiClient2.getApiClient().create(RetrofitApiInterfaceLoadProductBasketSize.class);
        apiInterfaceAddToBasket = RetrofitApiClient.getApiClient().create(RetrofitApiInterfaceAddToProdBasket.class);
        apiInterfaceAddToWishList = RetrofitApiClient.getApiClient().create(RetrofitApiInterfaceAddToWishList.class);
        apiInterfaceConfirmWishList = RetrofitApiClient.getApiClient().create(RetrofitApiInterfaceConfirmWishList.class);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        //View Init
        imageViewProductImage = findViewById(R.id.imageview_product_detail);
        textViewProductPrice = findViewById(R.id.textview_product_detail_price);
        textViewProductName = findViewById(R.id.textview_product_detail_name);
        textViewCommentTitle = findViewById(R.id.textview_product_detail_comment_title);
        textViewProductDesc = findViewById(R.id.textview_product_detail_desc);
        textViewProductManufacturer = findViewById(R.id.textview_product_detail_manufacturer);
        imageViewComment = findViewById(R.id.imageView_product_detail_comments);
        linearLayoutComment = findViewById(R.id.layout_product_detail_comments);
        textViewComment = findViewById(R.id.textview_product_detail_comment);
        numberButton = findViewById(R.id.number_button_product_detail);
        buttonAddToBasket = findViewById(R.id.button_product_detail_add);
        comment="";
        quantity=Integer.parseInt(numberButton.getNumber());

        //Customer
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
            subCatId = extras.getInt("subCatId");
            productName = extras.getString("productName");
            productDesc = extras.getString("productDesc");
            productPrice = extras.getString("productPrice");
            productPhoto = extras.getString("productPhoto");
            manufacturer = extras.getString("manufacturer");
        }

//      Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_product_detail);

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

        //Adjust other layouts sizes
        adjustLayoutsHeightScreen();

        //Add comment
        linearLayoutComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCommentAlertDialog();
            }
        });

        //CardView Add To Basket Tweaks
        buttonAddToBasket.setText(R.string.add_to_basket);
        buttonAddToBasket.getLayoutParams().height= (int)Math.round(Utilities.getScreenHeight(this)* 0.06);

        //Add Item to Basket Click
        buttonAddToBasket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemToBasket(v);
            }
        });

        //Setting Received Extras to Display
        if (productId != 0){
//            toolbar.setSubtitle(pro);
            textViewProductName.setText(productName);
            textViewProductDesc.setText(productDesc);
            textViewProductManufacturer.setText(manufacturer);
            //Setting Currency for Price Text
            Locale locale = new Locale("en", "GH");
            NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
            textViewProductPrice.setText(fmt.format(Double.parseDouble(productPrice)));
            RequestOptions requestOptions = new RequestOptions();
            //loading the image
            Glide.with(this)
                    .load(productPhoto)
                    .apply(requestOptions)
                    .into(imageViewProductImage);

        }else{
            //Go back if No food Id passed
            onBackPressed();
            finish();
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

    //Function to confirm product in wish list
    private void confirmProductInWishList() {
        /*
        Retrofit Network Connection
        * */
        Call<String> call = apiInterfaceConfirmWishList.confirmWishList(String.valueOf(productId), String.valueOf(customerId));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                Log.e("Confirm_Basket", "response: "+new Gson().toJson(response.body()) );
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.e("onSuccess", response.body());
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(response.body());
                            //Success
                            if(obj.optString("item_exists").equals("true")){
                                menu.findItem(R.id.action_product_detail_add_wishlist).setIcon(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_wish_list_fill));
                            } else {
                                menu.findItem(R.id.action_product_detail_add_wishlist).setIcon(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_wish_list_outline));
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
            }
        });
    }

    //Function to Set and Return Quantity selected
    private int getProductQuantity() {
        return Integer.parseInt(numberButton.getNumber());
    }

    //----------------------RETROFIT-------------------(Function to add item to Basket)----------------RETROFIT-----------------
    private void addItemToBasket(final View view) {
        //Show Progress Dialog
        showWaitDialog();
        /*
        Retrofit Network Connection
        * */
        Call<String> call = apiInterfaceAddToBasket.addToBasket(String.valueOf(productId), String.valueOf(customerId), String.valueOf(quantity), String.valueOf(comment));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                Log.e("Add_Basket", "response: "+new Gson().toJson(response.body()) );
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.e("onSuccess", response.body());
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(response.body());
                            //Success sign in
                            if(obj.optString("success").equals("true")){
                                Snackbar.make(view,obj.getString("message"), Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            } else {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                            }
                            //Close Waiting Dialog
                            waitDialog.dismiss();
                            invalidateOptionsMenu();
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
                //Close Waiting Dialog
                waitDialog.dismiss();

                Toast.makeText(ProductDetailActivity.this, "Error. Check connection and try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Function to Display the Comment Dialog
    private void showCommentAlertDialog() {
//      Use layout created in xml and inflate as our AlertDialog EditText
        View view = LayoutInflater.from(this).inflate(R.layout.product_comment_layout, null);

        final EditText edittext = view.findViewById(R.id.edittextdialog_product_comment);
        final AlertDialog.Builder alertdialog = new AlertDialog.Builder(this, R.style.Theme_AppCompat_DayNight_Dialog);

//        alertdialog.setMessage("Enter Your Message");
        alertdialog.setTitle("Special requests or preferences");
        alertdialog.setView(view);

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
        getMenuInflater().inflate(R.menu.menu_product_detail, menu);
        //Check if Product is in Wish list
        confirmProductInWishList();
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //Needed for refresh toolbar items
        basketMenuItem = menu.findItem(R.id.action_product_detail_goto_basket);
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
        /*if (id == R.id.action_sign_out) {
            return true;
        }*/
        switch (id){
            case R.id.action_product_detail_add_wishlist:
                //Do something;
                addToWishList();
                return true;
            case R.id.action_product_detail_share:
                //Do something
                Utilities.shareApp(this);
                return true;
            case R.id.action_product_detail_my_orders:
                //Do something
                Utilities.gotoMyOrders(this);
                return true;
            case R.id.action_product_detail_goto_basket:
                //Do something
                Utilities.gotoProductBasket(getApplicationContext());
                this.finish();
                return true;
            case R.id.action_product_detail_view_wish_list:
                //Do something
                Utilities.viewProductWishlist(getApplicationContext());
                return true;
            case R.id.action_product_detail_my_account:
                //Do something
                Utilities.openBackOfficeAccount(getApplicationContext());
                return true;
            case R.id.action_product_detail_sign_out:
                //Function to perform SignOut
                SharedPrefManager.getInstance(getApplicationContext()).logoutCustomer();
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Function to add item to wish list
    private void addToWishList() {
        //Show Progress Dialog
        showWaitDialog();
        /*
        Retrofit Network Connection
        * */
        Call<String> call = apiInterfaceAddToWishList.addToWishList(String.valueOf(productId), String.valueOf(customerId));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                Log.e("Add_Basket", "response: "+new Gson().toJson(response.body()) );
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.e("onSuccess", response.body());
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(response.body());
                            //Success
                            if(obj.optString("result").equals("item_added")){
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                menu.getItem(1).setIcon(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_wish_list_fill));
                                invalidateOptionsMenu();
                            } else if (obj.optString("result").equals("item_removed")){
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                menu.getItem(1).setIcon(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_wish_list_outline));
                                invalidateOptionsMenu();
                            }else {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                            //Close wait dialog
                            waitDialog.dismiss();
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
                //Close wait dialog
                waitDialog.dismiss();

                Toast.makeText(ProductDetailActivity.this, "Error. Check connection and try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Function to Load Basket Item Size
    private void loadBasketFromDB() {
        Call<List<ProductBasketItem>> call = apiInterfaceloadBasketSize.getBasketSize(String.valueOf(customerId));
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
            }
        });
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
            basketMenuItem.setIcon(BadgeCounterIconConverter.convertLayoutToImage(ProductDetailActivity.this,basketSize,R.drawable.ic_product_basket));
        }else{
            basketMenuItem.setIcon(getResources().getDrawable(R.drawable.ic_product_basket));
        }

    }


    //Function to display wait Alert dialog
    private void showWaitDialog() {
//      Use layout created in xml and inflate as our AlertDialog EditText
        View view = LayoutInflater.from(this).inflate(R.layout.layout_wait_dialog, null);

        final TextView textViewWait = view.findViewById(R.id.dialog_textView_wait);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getText(R.string.please_wait));
        builder.setView(view);

        builder.setCancelable(false);

        //Get Text and Set to View
        textViewWait.setText(getText(R.string.processing));
        float screenHeight = Utilities.getScreenHeight(this);
        textViewWait.getLayoutParams().height = (int) Math.round(screenHeight*0.08);
        waitDialog = builder.create();
        waitDialog.show();
    }
}

//Retrofit API Interface
interface RetrofitApiInterfaceLoadProductBasketSize {
    @POST(URLs.URL_PRODUCT_BASKET_ITEM_LIST)
    @FormUrlEncoded
    Call<List<ProductBasketItem>> getBasketSize(@Field("customerId") String customerId);
}
//Retrofit API Interface
interface RetrofitApiInterfaceAddToProdBasket {
    @POST(URLs.URL_ADD_PRODUCT_TO_BASKET)
    @FormUrlEncoded
    Call<String> addToBasket(@Field("productId") String foodId, @Field("customerId") String customerId, @Field("quantity") String quantity, @Field("comment") String comment);
}
//Retrofit API Interface
interface RetrofitApiInterfaceAddToWishList {
    @POST(URLs.URL_ADD_PRODUCT_TO_WISHLIST)
    @FormUrlEncoded
    Call<String> addToWishList(@Field("productWishListId") String productId, @Field("customerId") String customerId);
}
//Retrofit API Interface
interface RetrofitApiInterfaceConfirmWishList {
    @POST(URLs.URL_CONFIRM_PRODUCT_IN_WISHLIST)
    @FormUrlEncoded
    Call<String> confirmWishList(@Field("productWishListId") String productId, @Field("customerId") String customerId);
}