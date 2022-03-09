package somame.amebleysystems.com.somame;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import androidx.annotation.NonNull;
import com.google.android.material.appbar.AppBarLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.SubtitleCollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.google.android.material.widget.SubtitleCollapsingToolbarLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.palette.graphics.Palette;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.io.File;
import java.io.FileOutputStream;
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

public class RestaurantFoodDetailUpdateActivity extends AppCompatActivity {
    public static RetrofitApiInterfaceLoadRestauBasketSize apiInterfaceLoadRestauBasketSize;
    public static RetrofitApiInterfaceUpdateRestauBasketItem apiInterfaceUpdateRestauBasketItem;
    public static RetrofitApiInterfaceLoadRestauBasketItemDetail apiInterfaceLoadRestauBasketItemDetail;
    private Bundle extras;
    TextView textViewFoodName, textViewFoodDesc, textViewComment;
    AppCompatTextView textViewFoodCommentTitle, textViewFoodPrice;
    ImageView imageViewFoodImage, imageViewComment;
    LinearLayout linearLayoutComment;
    private int foodId=0,quantity, basketSize, customerId;
    String foodName="", foodPrice="", foodDesc="", foodPhoto="", currentRestaurant="", comment;
    CoordinatorLayout coordinatorLayout;
    ElegantNumberButton numberButton;
    AppBarLayout appBarLayout;
    SubtitleCollapsingToolbarLayout collapsingToolbarLayout;
    ProgressBar progressBar;
    FloatingActionButton fab;
    private Menu menu;
    Toolbar toolbar;
    AlertDialog waitDialog = null;
    List<RestaurantBasketItem> basketItemList;

    @Override
    protected void onResume() {
        super.onResume();
        loadBasketFromDB();
        invalidateOptionsMenu();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Retrofit Api interface
        apiInterfaceLoadRestauBasketSize = RetrofitApiClient2.getApiClient().create(RetrofitApiInterfaceLoadRestauBasketSize.class);
        apiInterfaceUpdateRestauBasketItem = RetrofitApiClient.getApiClient().create(RetrofitApiInterfaceUpdateRestauBasketItem.class);
        apiInterfaceLoadRestauBasketItemDetail = RetrofitApiClient2.getApiClient().create(RetrofitApiInterfaceLoadRestauBasketItemDetail.class);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_food_detail_update);
        //View Init
        textViewFoodPrice = findViewById(R.id.textview_restaurant_food_detail_update_food_price);
        textViewFoodDesc = findViewById(R.id.textview_restaurant_food_detail_update_food_desc);
        textViewFoodCommentTitle = findViewById(R.id.textview_restaurant_food_detail_update_comment_title);
        imageViewComment = findViewById(R.id.imageView_restaurant_food_detail_update_comments);
        linearLayoutComment = findViewById(R.id.layout_restaurant_food_detail_update_comments);
        textViewComment = findViewById(R.id.textview_restaurant_food_detail_update_comment);
//        radioGroup = findViewById(R.id.radio_group_restau_food_detail_update);
        numberButton = findViewById(R.id.number_button_restaurant_food_detail_update);
        comment="";

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
            foodId = extras.getInt("itemId");
        }

        //      Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_restaurant_food_detail_update);
        toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        setSupportActionBar(toolbar);
        toolbar.setSubtitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //Load Basket Item Size
        loadBasketFromDB();

        //Set ClickListener for Number Button
        numberButton.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                quantity = getProductQuantity();
            }
        });

        //Add comment to food
        linearLayoutComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCommentAlertDialog();
            }
        });

        //More Tweaks for Beautiful Display
        //This snippet maintains image height dynamically
        final Display dWidth = getWindowManager().getDefaultDisplay();
        coordinatorLayout = findViewById(R.id.coordinator_restaurant_food_detail_update);
        appBarLayout = findViewById(R.id.appBar_restaurant_food_detail_update);

        //Function to Set Layout Heights to Fit Screen
        adjustLayoutsHeightScreen();

        //Pre-collapse height of Collapsing toolbar using set offset bar method (One-third part will be collapsed)
        appBarLayout.post(new Runnable() {
            @Override
            public void run() {
                int heightPx = dWidth.getWidth() * 1 / 3;
                setAppBarOffset(heightPx);
            }
        });

        collapsingToolbarLayout = findViewById(R.id.collapsingToolbar_restaurant_food_detail_update);
        imageViewFoodImage = findViewById(R.id.imageview_restaurant_food_detail_update);
        imageViewFoodImage.getLayoutParams().height = dWidth.getWidth();

        //This Snippet to match toolbar color to Image dominating color using Palette
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.bg_app_main_menu_header_bg);
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(@NonNull Palette palette) {
                int mutedColor = palette.getMutedColor(getResources().getColor(R.color.colorPrimary));
                collapsingToolbarLayout.setContentScrimColor(mutedColor);
            }
        });

        //UPDATE Basket Click
        fab = findViewById(R.id.fab_restaurant_food_detail_add_update);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                radioClick(view);
                updateBasketItem(view);
            }
        });

        //Setting Received Extras to Display
        if (foodId != 0){
            //Load Basket Item Detail
            loadBasketItemDetail(foodId);

        }else{
            //Go back if No food Id passed
            onBackPressed();
        }

        //When Food tile clicked
        collapsingToolbarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFoodTitle();
            }
        });

        //Setting the Product Image
        RequestOptions requestOptions = new RequestOptions();
        //loading the image
        Glide.with(this)
                .load(foodPhoto)
                .apply(requestOptions)
                .into(imageViewFoodImage);
    }


    //Function to display food tilte in Alert dialog
    private void showFoodTitle() {
//      Use layout created in xml and inflate as our AlertDialog EditText
        View view = LayoutInflater.from(this).inflate(R.layout.layout_restaurant_food_name_dialog, null);

        final TextView textViewFoodName = view.findViewById(R.id.dialog_textView_restaurant_food_name);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(currentRestaurant);
        builder.setView(view);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });

        //Get Text and Set to View
        textViewFoodName.setText(foodName);
        final AlertDialog dialog = builder.create();
        dialog.show();
        //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });
    }

    //Function to Set Layout Heights to Fit Screen
    private void adjustLayoutsHeightScreen() {
        float screenHeight = Utilities.getScreenHeight(this);
        float screenWidth = Utilities.getScreenWidth(this);
        numberButton.getLayoutParams().height = (int) Math.round(screenHeight*0.05);
        textViewFoodCommentTitle.getLayoutParams().width = (int) Math.round(screenWidth*0.65);
        linearLayoutComment.getLayoutParams().width = (int) Math.round(screenWidth*0.35);
        imageViewComment.getLayoutParams().height = (int) Math.round(screenHeight*0.02);

    }

    //----------------VOLLEY----------------Function to load Basket Item and Details-------VOLLEY
    private void loadBasketItemDetail(final int foodId) {
//        progressBar.setVisibility(View.VISIBLE);
        Call<List<RestaurantBasketItem>> call = apiInterfaceLoadRestauBasketItemDetail.loadItemDetail(String.valueOf(customerId), String.valueOf(foodId));
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
//        /*
//        * Creating a String Request
//        * The request type is GET defined by first parameter
//        * The URL is defined in the second parameter
//        * Then we have a Response Listener and a Error Listener
//        * In response listener we will get the JSON response as a String
//        * */
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_RESTAURANT_BASKET_ITEM_DETAIL,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.e("Response: ",response);
//
//                        try {
//
//                            JSONArray array = new JSONArray(response);
//                            //traversing through all the objects
//                            for (int i = 0; i < array.length(); i++) {
//                                //getting product object from json array
//                                JSONObject basketItem = array.getJSONObject(i);
//                                //Setting array items to UI
//                                collapsingToolbarLayout.setTitle(basketItem.getString("food_name"));
//                                textViewFoodDesc.setText(basketItem.getString("food_desc"));
//                                collapsingToolbarLayout.setSubtitle(basketItem.getString("restaurant_name"));
//                                comment = basketItem.getString("comment");
////                                deliveryOption = basketItem.getString("delivery_option");
//                                numberButton.setNumber(String.valueOf(basketItem.getInt("quantity")));
//                                RequestOptions requestOptions = new RequestOptions();
//                                //loading the image
//                                Glide.with(getApplicationContext())
//                                        .load(basketItem.getString("food_photo"))
//                                        .apply(requestOptions)
//                                        .into(imageViewFoodImage);
//
//                                //Setting Currency for Price Text
//                                Locale locale = new Locale("en", "GH");
//                                NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
//                                textViewFoodPrice.setText(fmt.format(Double.parseDouble(basketItem.getString("price"))));
//
//                            }
//                            textViewComment.setText((comment.equalsIgnoreCase(""))?"None":comment);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//                    }
//                }){
//
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("customerId", String.valueOf(customerId));
//                params.put("itemId", String.valueOf(foodId));
//                return params;
//            }
//
//        };
//
//        //adding our stringrequest to queue
//        Volley.newRequestQueue(this).add(stringRequest);
    }
    //Load JSON data from response
    private void loadBasketResponse(List<RestaurantBasketItem> response) {
        //getting the whole json object from the response
        basketItemList = new ArrayList<>();
        RestaurantBasketItem basketItem;
        //traversing through all the object
        for (int i = 0; i < response.size(); i++) {
            basketItem = response.get(i);
            //getting product object from json array
            //Setting array items to UI
            collapsingToolbarLayout.setTitle(basketItem.getName());
            textViewFoodDesc.setText(basketItem.getDescription());
            collapsingToolbarLayout.setSubtitle(basketItem.getRestaurantName());
            comment = basketItem.getComment();
//                                deliveryOption = basketItem.getString("delivery_option");
            numberButton.setNumber(String.valueOf(basketItem.getQuantity()));
            RequestOptions requestOptions = new RequestOptions();
            //loading the image
            Glide.with(getApplicationContext())
                    .load(basketItem.getLogo())
                    .apply(requestOptions)
                    .into(imageViewFoodImage);

            //Setting Currency for Price Text
            Locale locale = new Locale("en", "GH");
            NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
            textViewFoodPrice.setText(fmt.format(Double.parseDouble(basketItem.getPrice())));
        }
        textViewComment.setText((comment.equalsIgnoreCase(""))?"None":comment);
//        progressBar.setVisibility(View.GONE);
    }

    //Function to Set and Return Quantity selected
    private int getProductQuantity() {
        return Integer.parseInt(numberButton.getNumber());
    }

    //----------------------VOLLEY-------------------(Function to add item to Basket)----------------VOLLEY-----------------
    private void updateBasketItem(final View view) {
        //Show Progress Dialog
        showWaitDialog();
        /*
        Retrofit Network Connection
        * */
        Call<String> call = apiInterfaceUpdateRestauBasketItem.updateItem(String.valueOf(customerId), String.valueOf(foodId), String.valueOf(numberButton.getNumber()), String.valueOf(textViewComment.getText()));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
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
                                Toast.makeText(getApplicationContext(),obj.getString("message"),Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(RestaurantFoodDetailUpdateActivity.this, RestaurantBasketActivity.class));
//                                invalidateOptionsMenu();
                            } else {
//                                invalidateOptionsMenu();
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                            }
                            //Close wait dialog
                            waitDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("onEmptyResponse", "Returned empty response");
                    }
                    waitDialog.dismiss();
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                //display error message
                Log.d("retrofit_log", "Failed! Error = " + t.getMessage());
            }
        });
    }

    //Function to Display the Comment Dialog
    private void showCommentAlertDialog() {
//      Use layout created in xml and inflate as our AlertDialog EditText
        View view = LayoutInflater.from(this).inflate(R.layout.restaurant_food_comment_update_layout, null);

        final EditText edittext = view.findViewById(R.id.edittextdialog_restauarnt_food_update_comment);
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

    //Menu On Toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_restaurant_food_detail_update, menu);
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

            case R.id.action_restaurant_food_detail_update_share:
                shareImage();
                return true;
//            case R.id.action_restaurant_food_detail_update_goto_basket:
//                Utilities.gotoRestaurantBasket(getApplicationContext());
//                this.finish();
//                return true;
            case R.id.action_restaurant_food_detail_update_sign_out:
                //Function to perform SignOut
                SharedPrefManager.getInstance(getApplicationContext()).logoutCustomer();
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Function to share the Food Image
    private void shareImage() {
        imageViewFoodImage.setDrawingCacheEnabled(true);
        final String appPackageName = getPackageName();
        Bitmap bitmap = imageViewFoodImage.getDrawingCache();
        File root = Environment.getExternalStorageDirectory();

        File cachePath = new File(root.getAbsolutePath() + "/DCIM/Camera/image.jpg");
        try {
            cachePath.createNewFile();
            FileOutputStream ostream = new FileOutputStream(cachePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
            ostream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");
        String shareBody = "Hi there, check out this amazing food item on "+getString(R.string.app_name) +".\n";
        shareBody = shareBody + "http://play.google.com/store/apps/details?id=" + appPackageName;
        String shareHeader = getString(R.string.app_name);
        share.putExtra(android.content.Intent.EXTRA_SUBJECT, shareHeader);
        share.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(cachePath));
        startActivity(Intent.createChooser(share,"Share via"));
    }

    //Set AppBar Offset Function
    private void setAppBarOffset(int offsetPx) {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        if (behavior != null) {
            behavior.onNestedPreScroll(coordinatorLayout, appBarLayout, null, 0, offsetPx, new int[]{0, 0});
        }
    }

    //Function to Load Basket Item Size
    private void loadBasketFromDB() {
        Call<List<RestaurantBasketItem>> call = apiInterfaceLoadRestauBasketSize.getBasketSize(String.valueOf(customerId));
        call.enqueue(new Callback<List<RestaurantBasketItem>>() {
            @Override
            public void onResponse(Call<List<RestaurantBasketItem>> call, retrofit2.Response<List<RestaurantBasketItem>> response) {
                Log.e("Basket_Size", "response: "+new Gson().toJson(response.body()) );
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.e("onSuccess", response.body().toString());
                        List<RestaurantBasketItem> basketItemList = response.body();
                        //Creating a String array for the List
                        loadBasketSizeResponse(basketItemList);
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
//        /*
//         * Creating a String Request
//         * The request type is GET defined by first parameter
//         * The URL is defined in the second parameter
//         * Then we have a Response Listener and a Error Listener
//         * In response listener we will get the JSON response as a String
//        * */
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_RESTAURANT_BASKET_ITEM_LIST,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.e("Response: ",response);
//
//                        try {
//                            JSONArray array = new JSONArray(response);
//                            int addCount = 0;
//                            //traversing through all the objects
//                            for (int i = 0; i < array.length(); i++) {
//                                //Accumulating items
//                                addCount++;
//                            }
//                            basketSize = addCount;
//                            //Converting BasketIcon to BadgeIcon
//                            MenuItem menuItem = menu.findItem(R.id.action_restaurant_food_detail_update_goto_basket);
//                            menuItem.setIcon(BadgeCounterIconConverter.convertLayoutToImage(RestaurantFoodDetailUpdateActivity.this,basketSize,R.drawable.ic_product_basket));
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                    }
//                }){
//
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("customerId", String.valueOf(customerId));
//                return params;
//            }
//
//        };
//
//        //adding our stringrequest to queue
//        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }
    //Load JSON data from response
    private void loadBasketSizeResponse(List<RestaurantBasketItem> response) {
        //getting the whole json object from the response
        basketItemList = new ArrayList<>();
        int addCount = 0;
//      JSONObject;
        RestaurantBasketItem basketItem;
        //traversing through all the object
        for (int i = 0; i < response.size(); i++) {
            //getting product object from json array
            addCount++;
        }
        basketSize = addCount;
        //Converting BasketIcon to BadgeIcon
//        MenuItem menuItem = menu.findItem(R.id.action_restaurant_food_detail_update_goto_basket);
//        menuItem.setIcon(BadgeCounterIconConverter.convertLayoutToImage(RestaurantFoodDetailUpdateActivity.this,basketSize,R.drawable.ic_product_basket));
//        //Converting BasketIcon to BadgeIcon
//        if (basketSize > 0) {
//            basketMenuItem.setIcon(BadgeCounterIconConverter.convertLayoutToImage(ProductDetailActivity.this,basketSize,R.drawable.ic_product_basket));
//        }else{
//            basketMenuItem.setIcon(getResources().getDrawable(R.drawable.ic_product_basket));
//        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
interface RetrofitApiInterfaceLoadRestauBasketSize {
    @POST(URLs.URL_RESTAURANT_BASKET_ITEM_LIST)
    @FormUrlEncoded
    Call<List<RestaurantBasketItem>> getBasketSize(@Field("customerId") String customerId);
}
//Retrofit API Interface
interface RetrofitApiInterfaceUpdateRestauBasketItem {
    @POST(URLs.URL_UPDATE_RESTAURANT_BASKET_ITEM)
    @FormUrlEncoded
    Call<String> updateItem(@Field("customerId") String customerId, @Field("itemId") String foodId, @Field("quantity") String quantity, @Field("comment") String comment);
}
//Retrofit API Interface
interface RetrofitApiInterfaceLoadRestauBasketItemDetail {
    @POST(URLs.URL_RESTAURANT_BASKET_ITEM_DETAIL)
    @FormUrlEncoded
    Call<List<RestaurantBasketItem>> loadItemDetail(@Field("customerId") String customerId, @Field("itemId") String productId);
}