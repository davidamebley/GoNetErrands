package somame.amebleysystems.com.somame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Model.Customer;
import Model.DeliveryLocation;
import Model.ProductOrder;
import Model.ProductOrderUpdate;
import SharedPref.SharedPrefManager;
import Util.RetrofitApiClient;
import Util.RetrofitApiClient2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class CheckOutActivity extends AppCompatActivity implements View.OnClickListener {
    public static RetrofitApiInterfaceLoadDeliveryLocList apiInterfaceLoadDeliveryLocList;
    public static RetrofitApiInterfaceUpdateOrder apiInterfaceUpdateOrder;
    public static RetrofitApiInterfaceCreateOrder apiInterfaceCreateOrder;
    public static RetrofitApiInterfaceClearBasket apiInterfaceClearBasket;
    Toolbar toolbar;
    ProgressBar progressBar;
    LinearLayout linearLayout, layoutCheckOutSummary;
    AutoCompleteTextView editTextFullName, editTextPhone, editTextAddress, editTextGhPost, editTextMoMoNumber;
    TextInputLayout layoutFullName, layoutPhone, layoutAddress, layoutGhPost, layoutMoMoNumber;
    TextView textViewPriceSubtotal, textViewPriceTotal, textViewPriceDelivery;
    double priceSubtotal, priceTotal, priceDelivery;
    Spinner spinnerPaymentMethod, spinnerDeliveryLocation, spinnerDeliveryPeriod;
    NestedScrollView scrollViewSummary, scrollViewMain;
    Button buttonProceed;
//    DatePicker datePickerDeliveryPeriod;
    CheckBox checkBoxFullName, checkBoxPhone, checkBoxAddress, checkBoxGhPost, checkBoxMoMoNumber;
    String firstName, lastName, fullName, phone, mAddress, ghPostAddress, momoNumber, paymentMethod, deliveryLocation="";
//    List<String> deliveryLocations;
    List<DeliveryLocation> deliveryLocations;
    List<String> tempDeliveryLocations;
    List<Integer> tempDeliveryLocationIds;
    ArrayAdapter<String> spinnerAdapterDeliveryLocation;
    ArrayAdapter<String> spinnerAdapterPaymentMethod;
    ArrayAdapter<String> spinnerAdapterDeliveryPeriod;
    int selectedDeliveryLocationId=0;
    Customer customer;
    int customerId;
    //Setting Currency for Price Text
    Locale locale = new Locale("en", "GH");
    NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
    //This formats a number by removing Currency symbol
    Number doubleNumber = null;
    String deliveryPeriod, returnedOrderNumber;
    private Bundle extras;
    //For Scrolling
    private float mTouchPosition;
    private float mReleasePosition;
    //For Payment Activity
    int returnValue=0;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    //Scroll Up and Down
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mTouchPosition = event.getY();
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            mReleasePosition = event.getY();

            if (mTouchPosition - mReleasePosition > 0) {
                // user scroll down
                Toast.makeText(this, "Scroll Dn", Toast.LENGTH_SHORT).show();
            } else {
                //user scroll up
                Toast.makeText(this, "Scroll Up", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onTouchEvent(event);
    }

//    This receives prompt that returned from prev activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                returnValue = data.getIntExtra("returnedFromPrevious",-1);
                returnedOrderNumber = data.getStringExtra("returnedOrderNumber");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Retrofit api
        apiInterfaceUpdateOrder = RetrofitApiClient.getApiClient().create(RetrofitApiInterfaceUpdateOrder.class);
        apiInterfaceCreateOrder = RetrofitApiClient.getApiClient().create(RetrofitApiInterfaceCreateOrder.class);
        apiInterfaceLoadDeliveryLocList = RetrofitApiClient2.getApiClient().create(RetrofitApiInterfaceLoadDeliveryLocList.class);
        apiInterfaceClearBasket = RetrofitApiClient.getApiClient().create(RetrofitApiInterfaceClearBasket.class);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        //Views Init
        linearLayout = findViewById(R.id.layout_check_out_linear);
        layoutCheckOutSummary = findViewById(R.id.layout_checkout_basket_summary);
        progressBar = findViewById(R.id.progress_check_out);
        textViewPriceSubtotal = findViewById(R.id.textView_checkout_basket_subtotal);
        textViewPriceTotal = findViewById(R.id.textview_checkout_basket_total);
        textViewPriceDelivery = findViewById(R.id.textview_checkout_basket_shipping);
        editTextFullName = findViewById(R.id.edittext_check_out_full_name);
        editTextPhone = findViewById(R.id.edittext_check_out_phone);
        editTextAddress = findViewById(R.id.edittext_check_out_address);
        editTextGhPost = findViewById(R.id.edittext_check_out_gh_post_address);
        editTextMoMoNumber = findViewById(R.id.edittext_check_out_momo_number);
        layoutFullName= findViewById(R.id.textInput_checkout_full_name);
        layoutPhone = findViewById(R.id.textInput_checkout_phone);
        layoutAddress = findViewById(R.id.textInput_checkout_address);
        layoutGhPost = findViewById(R.id.textInput_checkout_gh_post);
        layoutMoMoNumber = findViewById(R.id.textInput_checkout_momo);
        scrollViewSummary = findViewById(R.id.nested_scrollView_checkout_basket_summary);
        scrollViewMain = findViewById(R.id.nestedScrollView_check_out);
        spinnerPaymentMethod= findViewById(R.id.spinner_check_out_payment_method);
        spinnerDeliveryLocation = findViewById(R.id.spinner_check_out_delivery_location);
        spinnerDeliveryPeriod = findViewById(R.id.spinner_check_out_delivery_period);
        checkBoxFullName = findViewById(R.id.checkbox_check_out_full_name);
        checkBoxPhone = findViewById(R.id.checkbox_check_out_phone);
        checkBoxAddress = findViewById(R.id.checkbox_check_out_address);
        checkBoxGhPost = findViewById(R.id.checkbox_check_out_gh_post_gps);
        checkBoxMoMoNumber = findViewById(R.id.checkbox_check_out_momo_number);
        buttonProceed= findViewById(R.id.button_check_out_next);
        //Set Delivery Period On Create
        deliveryPeriod = "Same day";

        //Customer
        customer = SharedPrefManager.getInstance(getApplicationContext()).getCustomer();
        customerId = customer.getCustomerId();

        //Check if user is logged in
        if (!SharedPrefManager.getInstance(this).isCustomerLoggedIn()) {
            finish();
            startActivity(new Intent(this, SignInActivity.class));
        }

        //GET Intent Here
        extras = getIntent().getExtras();
        if (extras != null) {
            //Retrieve details from received extras

        }else {

        }

        //Height of Screen
        adjustLayoutsHeightScreen();

        //        Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_check_out);
        toolbar.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setSubtitle(getString(R.string.checkout));
        toolbar.setSubtitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

//      Progressbar
        progressBar = findViewById(R.id.progress_check_out);

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

        //Spinner Drop Down
        spinnerAdapterPaymentMethod = new ArrayAdapter<String>(CheckOutActivity.this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.payment_method));

        spinnerAdapterPaymentMethod.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPaymentMethod.setAdapter(spinnerAdapterPaymentMethod);

        //Spinner Delivery Period Drop Down Set
        spinnerAdapterDeliveryPeriod = new ArrayAdapter<String>(CheckOutActivity.this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.delivery_period));

        spinnerAdapterDeliveryPeriod.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDeliveryPeriod.setAdapter(spinnerAdapterDeliveryPeriod);
        //Delivery Period Spinner Click
        spinnerDeliveryPeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                deliveryPeriod = spinnerDeliveryPeriod.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Make EditTextViews Fit
        setViewsWidthToFit();

        //Load Default Customer Details
        loadDefaultCustomerDetails();

        //CheckBox click
        addClickListenerToCheckBox();

        //Set Basket Summary
        loadBasketSummary();


        //Delivery Location Select
        spinnerDeliveryLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Select a Location
                selectedDeliveryLocationId = tempDeliveryLocationIds.get(position);
                deliveryLocation = spinnerDeliveryLocation.getSelectedItem().toString();
                //Set Transportation cost
                textViewPriceDelivery.setText(fmt.format(Utilities.getTransportationCost(deliveryLocation)));
                getDeliveryCost();
                setPriceTotal();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Button Proceed CLick
        buttonProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Checkout begins
                attemptCheckout();
            }
        });
    }

    //Function to retrieve delivery cost from textView
    private void getDeliveryCost() {
            String strPriceDelivery = textViewPriceDelivery.getText().toString();
            try{
                doubleNumber = fmt.parse(strPriceDelivery);
            }catch (ParseException e){
                Log.e("exception", "setPriceTotal exception: ",e);
            }
            priceDelivery = doubleNumber.doubleValue();

    }

    //    Function to retrieve order details of customer
    private void attemptCheckout() {
        validateFields();
    }

    //Function to check inputs
    private void validateFields() {
        // Reset errors.
        editTextFullName.setError(null);
        editTextPhone.setError(null);
        editTextAddress.setError(null);
        editTextGhPost.setError(null);
        editTextMoMoNumber.setError(null);
        //Get Order Details
        getOrderDetails();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid fullName
        if (TextUtils.isEmpty(fullName)) {
            editTextFullName.setError(getString(R.string.error_field_required));
            focusView = editTextFullName;
            cancel = true;
        }
        // Check for a valid Phone
        if (TextUtils.isEmpty(phone)) {
            editTextPhone.setError(getString(R.string.error_field_required));
            focusView = editTextPhone;
            cancel = true;
        }else if(!isPhoneValid(phone)){
            editTextPhone.setError(getString(R.string.error_invalid_phone));
            focusView = editTextPhone;
            cancel = true;
        }

        // Check for a valid Address
        if (TextUtils.isEmpty(mAddress)) {
            editTextAddress.setError(getString(R.string.error_field_required));
            focusView = editTextAddress;
            cancel = true;
        }
        // Check for a valid itemDetails
        if (TextUtils.isEmpty(momoNumber)) {
            editTextMoMoNumber.setError(getString(R.string.error_field_required));
            focusView = editTextMoMoNumber;
            cancel = true;
        }
        if (editTextMoMoNumber.getText().length()<10) {
            editTextMoMoNumber.setError(getString(R.string.error_invalid_phone));
            focusView = editTextMoMoNumber;
            cancel = true;
        }else if(!isPhoneValid(momoNumber)){
            editTextMoMoNumber.setError(getString(R.string.error_invalid_phone));
            focusView = editTextMoMoNumber;
            cancel = true;
        }
        // Check for a valid deliveryLocation
        if (TextUtils.isEmpty(deliveryLocation)) {
            spinnerDeliveryLocation.setPrompt(getString(R.string.error_field_required));
            focusView = spinnerDeliveryLocation;
            cancel = true;
        }

        if (cancel) {
            // There was an error don't attempt sign up and focus the first
            // form field with an error.
            focusView.requestFocus();

        }else {
            checkoutCustomer();
        }
    }


    //Function to retrieve order details of customer
    private void getOrderDetails() {
        fullName = editTextFullName.getText().toString();
        phone = editTextPhone.getText().toString();
        mAddress = editTextAddress.getText().toString();
        ghPostAddress = editTextGhPost.getText().toString();
        deliveryLocation = spinnerDeliveryLocation.getSelectedItem().toString();
        deliveryPeriod = spinnerDeliveryPeriod.getSelectedItem().toString();
        paymentMethod = spinnerPaymentMethod.getSelectedItem().toString();
        momoNumber = editTextMoMoNumber.getText().toString();
    }


    //Function to Set Layout Heights to Fit Screen
    private void adjustLayoutsHeightScreen() {
        float screenHeight = Utilities.getScreenHeight(this);
        float screenWidth = Utilities.getScreenWidth(this);
        buttonProceed.getLayoutParams().height = (int) Math.round(screenHeight*0.05);
        buttonProceed.getLayoutParams().width = (int) Math.round(screenWidth*0.4);
        linearLayout.getLayoutParams().height = (int) Math.round(screenHeight*0.74);
        layoutCheckOutSummary.getLayoutParams().height = (int) Math.round(screenHeight*0.24);
    }


    //Function to Load Basket Price Summary
    private void loadBasketSummary() {
        loadDeliveryLocationList();
        priceSubtotal= Utilities.getBasketPriceSubtotal();
        priceTotal = Utilities.getBasketPriceTotal();
        textViewPriceSubtotal.setText(fmt.format(priceSubtotal));
        textViewPriceTotal.setText(fmt.format(priceTotal));

    }

    //FUnction to Handle Checkbox click
    private void addClickListenerToCheckBox() {
        checkBoxFullName.setOnClickListener(this);
        checkBoxPhone.setOnClickListener(this);
        checkBoxAddress.setOnClickListener(this);
        checkBoxGhPost.setOnClickListener(this);
        checkBoxMoMoNumber.setOnClickListener(this);
    }


    //FUnction to load default details of customer
    private void loadDefaultCustomerDetails() {
        firstName = customer.getCustomerFistName();
        lastName = customer.getCustomerLastName();
        fullName = firstName.concat(" "+lastName);
        phone = customer.getCustomerPhone();
        mAddress = customer.getCustomerAddress();
        ghPostAddress = customer.getCustomerGhPostAddress();
        momoNumber = customer.getCustomerPhone();
        deliveryLocation = customer.getCustomerDeliveryLocation();

        editTextFullName.setText(fullName);
        editTextPhone.setText(phone);
        editTextAddress.setText(mAddress);
        editTextGhPost.setText(ghPostAddress);
        editTextMoMoNumber.setText(momoNumber);

        //Function to Enable or disable EditText fields onLoad
        enableDisableTextFields();
        //Load Delivery Locations
        loadDeliveryLocationList();
    }

    private void setPriceTotal() {
        String priceDelivery = textViewPriceDelivery.getText().toString();
        try{
            doubleNumber = fmt.parse(priceDelivery);
        }catch (ParseException e){
            Log.e("exception", "setPriceTotal exception: ",e);
        }
        double doubleDelivery = doubleNumber.doubleValue();
//        Toast.makeText(this, "Transport: "+doubleTransport, Toast.LENGTH_SHORT).show();
        priceTotal = doubleDelivery+priceSubtotal;
        Utilities.setPriceTotal(priceTotal);
        textViewPriceTotal.setText(fmt.format(Utilities.getBasketPriceTotal()));

    }


    //Function to Enable or disable EditText fields onLoad
    private void enableDisableTextFields() {

        AutoCompleteTextView[] view = {editTextFullName,editTextPhone,editTextAddress,editTextGhPost,editTextMoMoNumber};
        CheckBox[] checkBoxes = {checkBoxFullName,checkBoxPhone,checkBoxAddress,checkBoxGhPost,checkBoxMoMoNumber};
        for (int i = 0; i<view.length; i++) {
            if (view[i] != null && !TextUtils.isEmpty(view[i].getText().toString().trim())){
                view[i].setEnabled(false);
            }else{
                checkBoxes[i].setChecked(false);
            }
        }

    }

    //Menu On Toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_check_out_customer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_check_out_share:
                //Do something
                Utilities.shareApp(CheckOutActivity.this);
                return true;
            case R.id.action_check_out_prev:
                //Do something
                navigatePreviousActivity();
                return true;
            case R.id.action_check_out_next:
                //Do something
                navigateNextActivity();
                return true;
            case R.id.action_check_out_my_account:
                //Do something
                Utilities.openBackOfficeAccount(CheckOutActivity.this);
                return true;
            case R.id.action_check_out_goto_basket:
                //Do something
                Utilities.gotoProductBasket(CheckOutActivity.this);
                return true;
            case R.id.action_check_out_sign_out:
                //Function to perform SignOut
                SharedPrefManager.getInstance(CheckOutActivity.this).logoutCustomer();
                finish();
                return true;
            default:
                //Do something
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //Function to Move User to Prev(Basket) Activity
    private void navigatePreviousActivity() {
        Intent basketIntent = new Intent(CheckOutActivity.this, ProductBasketActivity.class);
        startActivity(basketIntent);
        finish();
    }
    //Function to Move User to Next (Payment) Activity
    private void navigateNextActivity() {
        //Checkout begins
        attemptCheckout();
    }

    //Function to Set TextViews to fit with Edit Text
    private void setViewsWidthToFit(){
        View[] view = {layoutFullName,layoutPhone,layoutAddress,layoutGhPost,layoutMoMoNumber};
        for (View v:view) {
            v.getLayoutParams().width= (int)Math.round(Utilities.getScreenWidth(this)* 0.78);
        }
        buttonProceed.getLayoutParams().width= (int)Math.round(Utilities.getScreenWidth(this)* 0.5);
    }


    //    RETROFIT CONNECTION----------(For Delivery Location List)--------------RETROFIT CONNECTION
    private void loadDeliveryLocationList() {
        Call<List<DeliveryLocation>> call = apiInterfaceLoadDeliveryLocList.loadDeliveryLocList();
        call.enqueue(new Callback<List<DeliveryLocation>>() {

            @Override
            public void onResponse(Call<List<DeliveryLocation>> call, retrofit2.Response<List<DeliveryLocation>> response) {
                Log.e("DeliveryLoc_TAG", "response: "+new Gson().toJson(response.body()) );
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.e("onSuccess", response.body().toString());
                        List<DeliveryLocation> deliveryLocations = response.body();
                        //Creating a String array for the List
                        loadDeliveryLocationListResponse(deliveryLocations);
                    } else {
                        Log.e("onEmptyResponse", "Returned empty response");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<DeliveryLocation>> call, Throwable t) {
                //display error message
                Log.d("retrofit_log", "Failed! Error = " + t.getMessage());
                Toast.makeText(CheckOutActivity.this, "Error. Check connection and try again", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void loadDeliveryLocationListResponse(List<DeliveryLocation> response) {
        //getting the whole json object from the response
        deliveryLocations = new ArrayList<>();
        tempDeliveryLocationIds = new ArrayList<>();
        tempDeliveryLocations = new ArrayList<>();

        //JSONObject
        DeliveryLocation deliveryLoc;

        //traversing through all the objects
        for (int i = 0; i < response.size(); i++) {
            //getting object from json response
            deliveryLoc = response.get(i);
            //adding the items to List
            tempDeliveryLocationIds.add(deliveryLoc.getLocationId());
            tempDeliveryLocations.add(deliveryLoc.getLocationName());
            Utilities.setTransportationCost(deliveryLoc.getLocationName(),deliveryLoc.getDeliveryCharge());
        }
        spinnerAdapterDeliveryLocation = new ArrayAdapter<String>(CheckOutActivity.this,
                android.R.layout.simple_list_item_1,tempDeliveryLocations);

        spinnerDeliveryLocation.setAdapter(spinnerAdapterDeliveryLocation);
        if (deliveryLocation != null) {
            if (!deliveryLocation.isEmpty()) {
//                                    Toast.makeText(UserAccountDetailsSetupActivity.this, "Delivery has "+deliveryLoc, Toast.LENGTH_SHORT).show();
                int spinnerPosition = spinnerAdapterDeliveryLocation.getPosition(deliveryLocation);
                spinnerDeliveryLocation.setSelection(spinnerPosition);
                //Set Delivery cost
                textViewPriceDelivery.setText(fmt.format(Utilities.getTransportationCost(deliveryLocation)));
//                                    textViewPriceDelivery.setText(fmt.format(priceTransportation));
            }else {
                spinnerDeliveryLocation.setSelection(-1);
                textViewPriceDelivery.setText(fmt.format(0));
            }
            setPriceTotal();
        }
    }

    //    RETROFIT CONNECTION----------(Checkout Customer)--------------RETROFIT CONNECTION
    private void checkoutCustomer() {
        // Show a progress spinner, and kick off a background task to
        // perform the attempt.
        showProgress(true);
        //Submit to PHP

        //New Order Creation
        if (returnValue == 0){
            //Create New Order Request
            ProductOrder productOrder = new ProductOrder(String.valueOf(customerId),firstName, lastName,phone,
                    mAddress,ghPostAddress,deliveryLocation,deliveryPeriod,paymentMethod,
                    momoNumber,String.valueOf(priceSubtotal),String.valueOf(priceTotal),String.valueOf(priceDelivery),Utilities.productOrderList
            );
            List<ProductOrder> newProductOrder = new ArrayList<>();
            newProductOrder.add(productOrder);
            Gson gson = new Gson();
            final String newOrderArray = gson.toJson(newProductOrder);
//            -------------
            /*
        Retrofit Network Connection
        * */
            Call<String> call = apiInterfaceCreateOrder.createOrder(String.valueOf(newOrderArray));
            call.enqueue(new Callback<String>() {

                @Override
                public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                    Log.e("CreateNewOrder_TAG", "response: "+new Gson().toJson(response.body()) );
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            Log.e("onSuccess", response.body());
                            JSONObject obj = null;
                            try {
                                obj = new JSONObject(response.body());
                                //Success sign in
                                if(obj.optString("success").equals("true")){
                                    String newOrderNo = obj.getString("order_number");

                                    Intent OrderPaymentIntent = new Intent(CheckOutActivity.this,PaymentActivity.class);
                                    OrderPaymentIntent.putExtra("OrderNumber",newOrderNo);
                                    OrderPaymentIntent.putExtra("CustomerName",fullName);
                                    OrderPaymentIntent.putExtra("momoNumber",momoNumber);
                                    OrderPaymentIntent.putExtra("NoOfItems",Utilities.getProdBasketItemTotal());
                                    OrderPaymentIntent.putExtra("TotalCost",Utilities.getBasketPriceTotal());

                                    startActivityForResult(OrderPaymentIntent, 1);
                                    //Clear Cart ========== ======== DON'T FORGET TO CLEAR IT
                                    clearBasket(customerId);
                                } else {
                                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                                }
                                //Hide progressbar
                                showProgress(false);

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
                    //Hide progressbar
                    showProgress(false);
                    Toast.makeText(CheckOutActivity.this, "Error. Check connection and try again", Toast.LENGTH_SHORT).show();
                }
            });

//========================================= UPDATE ORDER
            //Existing Order Updating
        }else if(returnValue == 1 && returnedOrderNumber!=null){
            //Update Returned Order Request
            ProductOrderUpdate productOrderUpdate = new ProductOrderUpdate(returnedOrderNumber,firstName, lastName,phone,
                    mAddress,ghPostAddress,deliveryLocation,deliveryPeriod,paymentMethod,
                    momoNumber,String.valueOf(priceSubtotal),String.valueOf(priceTotal),String.valueOf(priceDelivery),Utilities.productOrderList
            );
            List<ProductOrderUpdate> newProductOrderUpdate = new ArrayList<>();
            newProductOrderUpdate.add(productOrderUpdate);
            Gson gson = new Gson();
            final String orderUpdateArray = gson.toJson(newProductOrderUpdate);
//            ---------------
            /*
        Retrofit Network Connection
        * */
            Call<String> call = apiInterfaceUpdateOrder.updateOrder(String.valueOf(orderUpdateArray), String.valueOf(returnedOrderNumber));
            call.enqueue(new Callback<String>() {

                @Override
                public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                    Log.e("UpdateOrder_TAG", "response: "+new Gson().toJson(response.body()) );
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            Log.e("onSuccess", response.body());
                            JSONObject obj = null;
                            try {
                                obj = new JSONObject(response.body());
                                //Success sign in
                                if(obj.optString("success").equals("true")){
                                    Intent OrderPaymentIntent = new Intent(CheckOutActivity.this,PaymentActivity.class);
                                    OrderPaymentIntent.putExtra("OrderNumber",returnedOrderNumber);
                                    OrderPaymentIntent.putExtra("CustomerName",fullName);
                                    OrderPaymentIntent.putExtra("momoNumber",momoNumber);
                                    OrderPaymentIntent.putExtra("NoOfItems",Utilities.getProdBasketItemTotal());
                                    OrderPaymentIntent.putExtra("TotalCost",Utilities.getBasketPriceTotal());

                                    startActivityForResult(OrderPaymentIntent, 1);
                                } else {
                                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                                }
                                //Hide progressbar
                                showProgress(false);

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
                    //Hide progressbar
                    showProgress(false);
                    Toast.makeText(CheckOutActivity.this, "Error. Check connection and try again", Toast.LENGTH_SHORT).show();
                }
            });

        }
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
                Toast.makeText(CheckOutActivity.this, "Error. Check connection and try again", Toast.LENGTH_SHORT).show();
            }
        });
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

    private boolean isPhoneValid(String phone) {
        //TODO: Replace this with your own logic
        return phone.length() == 10;
    }

    //For Checkbox OnCLick Listener
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.checkbox_check_out_full_name:
                //DO something
                if (!((CheckBox)v).isChecked()){
                    editTextFullName.setEnabled(true);
                }else{
                    editTextFullName.setEnabled(false);
                    editTextFullName.setText(fullName);
                }
                break;
            case R.id.checkbox_check_out_phone:
                //DO something
                if (!((CheckBox)v).isChecked()){
                    editTextPhone.setEnabled(true);
                }else{
                    editTextPhone.setEnabled(false);
                    editTextPhone.setText(phone);
                }
                break;
            case R.id.checkbox_check_out_address:
                //DO something
                if (!((CheckBox)v).isChecked()){
                    editTextAddress.setEnabled(true);
                }else{
                    editTextAddress.setEnabled(false);
                    editTextAddress.setText(mAddress);
                }
                break;
            case R.id.checkbox_check_out_gh_post_gps:
                //DO something
                if (!((CheckBox)v).isChecked()){
                    editTextGhPost.setEnabled(true);
                }else{
                    editTextGhPost.setEnabled(false);
                    editTextGhPost.setText(ghPostAddress);
                }
                break;
            case R.id.checkbox_check_out_momo_number:
                //DO something
                if (!((CheckBox)v).isChecked()){
                    editTextMoMoNumber.setEnabled(true);
                }else{
                    editTextMoMoNumber.setEnabled(false);
                    editTextMoMoNumber.setText(momoNumber);
                }
                break;
        }
    }
}
//Retrofit API Interface
interface RetrofitApiInterfaceUpdateOrder {
    @POST(URLs.URL_PRODUCT_ORDER_UPDATE)
    @FormUrlEncoded
    Call<String> updateOrder(@Field("orderList") String orderUpdateArray, @Field("orderNumber") String returnedOrderNumber);
}
//Retrofit API Interface
interface RetrofitApiInterfaceCreateOrder {
    @POST(URLs.URL_PRODUCT_ORDER)
    @FormUrlEncoded
    Call<String> createOrder(@Field("orderList") String newOrderArray);
}
//Retrofit API Interface
interface RetrofitApiInterfaceLoadDeliveryLocList {
    @POST(URLs.URL_DELIVERY_LOCATION_LIST)
//    @FormUrlEncoded
    Call<List<DeliveryLocation>> loadDeliveryLocList();
}
//Retrofit API Interface
interface RetrofitApiInterfaceClearBasket {
    @POST(URLs.URL_CLEAR_PRODUCT_BASKET)
    @FormUrlEncoded
    Call<String> clearBasket(@Field("customerId") String customerId);
}