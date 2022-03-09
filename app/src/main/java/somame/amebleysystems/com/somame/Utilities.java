package somame.amebleysystems.com.somame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Model.Customer;
import Model.ProductOrderItem;
import SharedPref.SharedPrefManager;
import Util.RetrofitApiClient;
import Util.RetrofitApiClient2;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by Akwasi on ${9/27/2018}.
 */

public class Utilities {
    //Context Menu item
    public static final String DELETE = "Delete";
    public static final String VIEW_PRODUCT = "View Product";
    public static final String UPDATE_PRODUCT = "Update Product";
    public static final Double ITEMS_MIN_COST = 5.0;
    private static int customerId;
    private static int prodBasketItemTotal= 0;
    private static Double priceSubTotal=0.0;
    private static Double priceTotal=0.0;
    private static Double serviceCharge =0.0;
    private static Double itemsCost=0.0;
    static boolean madeChangesToData=false;
    private static Map<String, String> transportationCost = new HashMap<>();
    static List<ProductOrderItem> productOrderList;
//    static List<ProductOrderItem> productOrderList;


    //Get Transportation Cost
    static double getTransportationCost(String mKey) {
        return Double.parseDouble(transportationCost.get(mKey));
    }

    //Set Transportation Cost
    static void setTransportationCost(String mKey, String mValue) {
        transportationCost.put(mKey, mValue);
    }

    //Set Basket Price Subtotal
    static void setPriceSubTotal(double subtotal){
        priceSubTotal = subtotal;
    }

    //Set Percent Service Charge
    public static void setServiceCharge(double charge){
        serviceCharge = charge;
    }

    //Set Items Cost
    static void setItemsCost(double cost){
        itemsCost = cost;
    }

    //Set Basket Price Total
    static void setPriceTotal(double total){
        priceTotal = total;
    }

    //Get Price SubTotal
    static double getBasketPriceSubtotal(){
        return priceSubTotal;
    }

    //Set Prod Basket No od Item
    static void setProdBasketItemTotal(int total){prodBasketItemTotal = total;}

    static int getProdBasketItemTotal(){return prodBasketItemTotal;}

    //Get Items Cost
    public static double getItemsCost(){
        return itemsCost;
    }

    //Get Percent Service Charge
    static double getServiceCharge(double itemsCost){
        if(itemsCost <= 20.0 && itemsCost >= 5.0){
            serviceCharge = 2.0;
        }else if(itemsCost >20.0 && itemsCost <= 50.0){
            serviceCharge = 3.0;
        }else if(itemsCost >50.0 && itemsCost <=100.0){
            serviceCharge = 5.0;
        }else if(itemsCost >100.0 && itemsCost <=500.0){
            serviceCharge = 10.0;
        }else if(itemsCost >500.0 && itemsCost <=1000.0){
            serviceCharge = 15.0;
        }else if(itemsCost >1000.0){
            serviceCharge = 20.0;
        }
        return serviceCharge;
    }

    //Get Price Total
    static double getBasketPriceTotal(){
        return priceTotal;
    }

    //Function to sign out
    public static void performSignout(Context context) {
        Intent signOutIntent = new Intent(context,SignInActivity.class);
        signOutIntent.addFlags((FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        context.startActivity(signOutIntent);
//        finish();
    }

    //Get Customer ID
    public static int getCustomerID(Context context){
        int custID = SharedPrefManager.getInstance(context).getCustomer().getCustomerId();
        return custID;
    }

    public static void gotoRestaurantBasket(Context context){
        Intent newIntent = new Intent(context, RestaurantBasketActivity.class);
        newIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(newIntent);
    }

    public static void gotoProductBasket(Context context){
//        Toast.makeText(context,"Basket not ready",Toast.LENGTH_SHORT).show();
        Intent newIntent = new Intent(context, ProductBasketActivity.class);
        newIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(newIntent);
    }

    //Open MyOrders
    public static void gotoMyOrders(Context context){
        Intent newIntent = new Intent(context, MyOrdersActivity.class);
        newIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(newIntent);
    }

    public static void viewProductWishlist(Context context){
        Intent newIntent = new Intent(context, ProductWishListActivity.class);
        newIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(newIntent);
    }

    public static void openBackOfficeAccount(Context context){
        Intent newIntent = new Intent(context, MainMenu2.class);
        newIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(newIntent);
    }

    public static void addToWishList(Context context){
        /*Intent basketIntent = new Intent(context, BasketActivity.class);
        context.startActivity(basketIntent);*/
        Toast.makeText(context,"Item added to wish list",Toast.LENGTH_SHORT).show();
    }

    public static float convertPixelsToDp (float px){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float dp = px/(metrics.densityDpi/160f);
        return Math.round(dp);
    }

    public static float convertDpToPixel (float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp*(metrics.densityDpi/160f);
        return Math.round(px);
    }

    //Function to set margins of view
    public static void setMargins(Context context ,View view, int left, int top, int right, int bottom) {
        //First Convert dp value to px
        Resources resources = context.getResources();
        left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,left,resources.getDisplayMetrics());
        top = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,top,resources.getDisplayMetrics());
        right = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,right,resources.getDisplayMetrics());
        bottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,bottom,resources.getDisplayMetrics());
        //Now we set Margin
        if(view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams){
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left,top,right,bottom);
            view.requestLayout();
        }
    }

    public static float getScreenHeight (Activity activity){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return (float) displayMetrics.heightPixels;
    }

    public static float getScreenWidth (Activity activity){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return (float) displayMetrics.widthPixels;
    }

    public static void shareApp(Context context) {
        try {
            final String appPackageName = context.getPackageName();
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
//        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            String shareBody = "Hi there, \nI send "+context.getString(R.string.app_name)+" to buy all my items and foodstuff for me. You can also try it now!\n";
//        shareBody = shareBody + "market://details?id=" + appPackageName;
            shareBody = shareBody + "http://play.google.com/store/apps/details?id=" + appPackageName;
            share.putExtra(Intent.EXTRA_TEXT, shareBody);
            context.startActivity(Intent.createChooser(share,"Share via"));
        } catch (Exception ex) {
            Log.e("Share Intent Exception", "shareApp: ", ex);
        }
    }



    //--------VOLLEY---------------(LOAD CUSTOMER FROM DB)-------------VOLLEY
    public void loadCustomerDetailsFromDB(final Context mContext, final String emailText) {
        //Show Progress Bar
//        showProgress(true);
        /*
        * Creating a String Request
        * The request type is POST defined by first parameter
        * The URL is defined in the second parameter
        * Then we have a Response Listener and a Error Listener
        * In response listener we will get the JSON response as a String
        * */
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_LOAD_CUSTOMER_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response: ",response);

                        try {

                            //converting response to json object
                            JSONObject obj = new JSONObject(response);
                            //if success
                            if (obj.has("success")) {
                                //getting the user from the response
                                JSONObject userJson = obj.getJSONObject("user");

                                //creating a new agent object
                                Customer cus = new Customer(
                                        userJson.getInt("customer_id"),
                                        userJson.getString("customer_code"),
                                        userJson.getString("firstname"),
                                        userJson.getString("lastname"),
                                        userJson.getString("gender"),
                                        userJson.getString("photo"),
                                        userJson.getString("dob"),
                                        userJson.getString("email"),
                                        userJson.getString("phone"),
                                        userJson.getString("mAddress"),
                                        userJson.getString("gh_post_address"),
                                        userJson.getString("delivery_location"),
                                        userJson.getString("agent_code")
                                );
                                //Login successful
//                                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                //storing the user in shared preferences
                                SharedPrefManager.getInstance(mContext).setCustomerDetails(cus);

                            }else /*if (obj.getBoolean("error"))*/ {
                                //Invalid Username or Password
                                Toast.makeText(mContext, obj.getString("message"), Toast.LENGTH_SHORT).show();

                            }
                            //Hide Progress Bar
//                            showProgress(false);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                })
        {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", String.valueOf(emailText));
                return params;
            }

        };

        Volley.newRequestQueue(mContext).add(stringRequest);
    }

}
