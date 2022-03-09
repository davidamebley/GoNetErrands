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
import android.widget.LinearLayout;
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

import Adapter.MyOrderAdapter;
import Adapter.ProductAdapter;
import Adapter.ProductBasketItemAdapter;
import Model.Customer;
import Model.MyOrder;
import Model.Product;
import Model.ProductBasketItem;
import Model.ProductOrderItem;
import SharedPref.SharedPrefManager;
import Util.RetrofitApiClient2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class MyOrdersActivity extends AppCompatActivity {
    public static RetrofitApiInterfaceLoadOrders apiInterfaceLoadOrders;
    RecyclerView recyclerView;
    MyOrderAdapter recyclerAdapter;
    List<MyOrder>myOrderList;
    LinearLayout linearLayout;
    ProgressBar progressBar;
    Toolbar toolbar;
    Customer customer;
    int customerId;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Retrofit api
        apiInterfaceLoadOrders = RetrofitApiClient2.getApiClient().create(RetrofitApiInterfaceLoadOrders.class);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        //        Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_my_orders);
        toolbar.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setSubtitle(getString(R.string.my_orders));
        toolbar.setSubtitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

//      Progressbar
        progressBar = findViewById(R.id.progress_my_orders);
        linearLayout = findViewById(R.id.linear_my_orders_recycler);

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

        //Customer
        customer = SharedPrefManager.getInstance(getApplicationContext()).getCustomer();
        customerId = customer.getCustomerId();

        //Check if user is logged in
        if (!SharedPrefManager.getInstance(this).isCustomerLoggedIn()) {
            finish();
            startActivity(new Intent(this, SignInActivity.class));
        }

        //RecyclerView Tweaks
        recyclerView = findViewById(R.id.recycler_my_orders);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Load MyOrders
        loadMyOrders();
    }

    //============RETROFIT (Load My Orders) ==============RETROFIT
    private void loadMyOrders() {
        Call<List<MyOrder>> call = apiInterfaceLoadOrders.loadOrders(String.valueOf(customerId));
        call.enqueue(new Callback<List<MyOrder>>() {

            @Override
            public void onResponse(Call<List<MyOrder>> call, retrofit2.Response<List<MyOrder>> response) {
                Log.e("Orders_TAG", "response: "+new Gson().toJson(response.body()) );
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.e("onSuccess", response.body().toString());
                        List<MyOrder> OrderList = response.body();
                        //Creating a String array for the List
                        loadOrdersResponse(OrderList);

                    } else {
                        Log.e("onEmptyResponse", "Returned empty response");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<MyOrder>> call, Throwable t) {
                //display error message
                Log.d("retrofit_log", "Failed! Error = " + t.getMessage());
                //Hide ProgressBar
                showProgress(false);
                //creating adapter object and setting it to recyclerview
                recyclerAdapter = new MyOrderAdapter(MyOrdersActivity.this,myOrderList);
                recyclerView.setAdapter(recyclerAdapter);
                progressBar.setVisibility(View.GONE);

                Toast.makeText(MyOrdersActivity.this, "Error. Check connection and try again", Toast.LENGTH_SHORT).show();
            }
        });

        showProgress(true);

    }
    private void loadOrdersResponse(List<MyOrder> response) {
        //getting the whole json object from the response
        myOrderList = new ArrayList<>();

        //JSONObject Item
        MyOrder myOrderItem;

        //traversing through all the objects
        for (int i = 0; i < response.size(); i++) {
            //getting product object from json array
            myOrderItem = response.get(i);
            //adding the items to List
            myOrderList.add(new MyOrder(
                    myOrderItem.getOrderNo(),
                    myOrderItem.getCustomerName(),
                    myOrderItem.getMomoNumber(),
                    myOrderItem.getOrderStatus(),
                    myOrderItem.getPaymentStatus(),
                    myOrderItem.getOrderDate(),
                    myOrderItem.getOrderTotal()
            ));
        }
        //Hide ProgressBar
        showProgress(false);
        //creating adapter object and setting it to recyclerview
        recyclerAdapter = new MyOrderAdapter(MyOrdersActivity.this,myOrderList);
        recyclerView.setAdapter(recyclerAdapter);
        progressBar.setVisibility(View.GONE);
    }

    //Menu On Toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_orders, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_my_orders_share:
                //Do something
                Utilities.shareApp(this);
                return true;
            case R.id.action_my_orders_my_account:
                //Do something
                Utilities.openBackOfficeAccount(getApplicationContext());
                return true;
            case R.id.action_my_orders_sign_out:
                //Function to perform SignOut
                SharedPrefManager.getInstance(getApplicationContext()).logoutCustomer();
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
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
}
//Retrofit API Interface
interface RetrofitApiInterfaceLoadOrders {
    @POST(URLs.URL_MY_ORDERS)
    @FormUrlEncoded
    Call<List<MyOrder>> loadOrders(@Field("customerId") String customerId);
}