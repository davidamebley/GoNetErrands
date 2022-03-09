package somame.amebleysystems.com.somame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import java.util.List;

import Model.Customer;
import Model.DeliveryLocation;
import SharedPref.SharedPrefManager;
import Util.RetrofitApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.POST;

public class PickupServiceActivity extends AppCompatActivity implements View.OnClickListener{
    public static RetrofitApiInterfaceLoadDeliveryLocationListPickup apiInterfaceLoadDeliveryLocationList;
    Toolbar toolbar;
    ProgressBar progressBar;
    LinearLayout linearLayout;
    AutoCompleteTextView editTextFullName, editTextPhone, editTextAddress, editTextGhPost,
            editTextOtherItems, editTextItemDetails, editTextPickupPointAddress,
            editTextPickupPointGPSAddress, editTextPickupPointContactNumber;
    TextInputLayout layoutFullName, layoutPhone, layoutAddress, layoutGhPost, layoutOtherItems,
            layoutItemDetails, layoutPickUpPointAddress, layoutPickUpPointGPSAddress,
            layoutPickupPointContactNumber;
    Spinner spinnerDeliveryLocation, spinnerPickUpLocation, spinnerItemType;
    Button buttonProceed;
    CheckBox checkBoxFullName, checkBoxPhone, checkBoxAddress, checkBoxGhPost;
    ArrayAdapter<String> spinnerAdapterDeliveryLocation;
    ArrayAdapter<String> spinnerAdapterItemType;
    ArrayAdapter<String> spinnerAdapterPickUpPointLocation;
    String firstName, lastName, fullName, mPhone, mAddress, ghPostAddress, deliveryLocation,
            otherItems, itemDetails, pickUpPointAddress, pickUpPointGPSAddress,
            pickupPointContactNumber, pickUpLocation, itemType;
    List<String> deliveryLocations, pickupLocations;
    List<String> tempDeliveryLocations, tempPickupLocations;
    List<Integer> tempDeliveryLocationIds, tempPickupLocationIds;
    int selectedDeliveryLocationId=0, selectedPickupLocationId=0;
    Customer customer;
    int customerId;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Retrofit api
        apiInterfaceLoadDeliveryLocationList = RetrofitApiClient.getApiClient().create(RetrofitApiInterfaceLoadDeliveryLocationListPickup.class);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_service);
        //        Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_pickup_service);
        toolbar.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setSubtitle(getString(R.string.pickup_service));
        toolbar.setSubtitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
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

        //Views
        linearLayout = findViewById(R.id.layout_pickup_service_linear);
        progressBar = findViewById(R.id.progress_pickup_service);
        editTextFullName = findViewById(R.id.edittext_pickup_service_full_name);
        editTextPhone = findViewById(R.id.edittext_pickup_service_phone);
        editTextAddress = findViewById(R.id.edittext_pickup_service_address);
        editTextGhPost = findViewById(R.id.edittext_pickup_service_gh_post_address);
        editTextOtherItems = findViewById(R.id.edittext_pickup_service_item_type_others);
        editTextItemDetails = findViewById(R.id.edittext_pickup_service_item_details);
        editTextPickupPointAddress = findViewById(R.id.edittext_pickup_point_address);
        editTextPickupPointGPSAddress = findViewById(R.id.edittext_pickup_point_gh_post_address);
        editTextPickupPointContactNumber = findViewById(R.id.edittext_pickup_point_phone);
        layoutFullName= findViewById(R.id.textInput_pickup_service_full_name);
        layoutPhone = findViewById(R.id.textInput_pickup_service_phone);
        layoutAddress = findViewById(R.id.textInput_pickup_service_address);
        layoutGhPost = findViewById(R.id.textInput_pickup_service_gh_post);
        layoutOtherItems = findViewById(R.id.textInput_pickup_service_item_type_others);
        layoutItemDetails = findViewById(R.id.textInput_pickup_service_item_details);
        layoutPickUpPointAddress = findViewById(R.id.textInput_pickup_point_address);
        layoutPickUpPointGPSAddress = findViewById(R.id.textInput_pickup_point_gh_post);
        layoutPickupPointContactNumber = findViewById(R.id.textInput_pickup_point_phone);
        spinnerDeliveryLocation = findViewById(R.id.spinner_pickup_service_delivery_location);
        spinnerPickUpLocation = findViewById(R.id.spinner_pickup_service_pickup_location);
        spinnerItemType = findViewById(R.id.spinner_pickup_service_type_of_item);
        checkBoxFullName = findViewById(R.id.checkbox_pickup_service_full_name);
        checkBoxPhone = findViewById(R.id.checkbox_pickup_service_phone);
        checkBoxAddress = findViewById(R.id.checkbox_pickup_service_address);
        checkBoxGhPost = findViewById(R.id.checkbox_pickup_service_gh_post_gps);
        buttonProceed= findViewById(R.id.button_pickup_proceed);

        //Customer
        customer = SharedPrefManager.getInstance(getApplicationContext()).getCustomer();
        customerId = customer.getCustomerId();

        //Check if user is logged in
        if (!SharedPrefManager.getInstance(this).isCustomerLoggedIn()) {
            finish();
            startActivity(new Intent(this, SignInActivity.class));
        }

        //Spinner for Item Type
        spinnerAdapterItemType = new ArrayAdapter<String>(PickupServiceActivity.this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.pickup_item_type));

        spinnerAdapterItemType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerItemType.setAdapter(spinnerAdapterItemType);

        //Delivery Location Select
        spinnerDeliveryLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Select a Location
                selectedDeliveryLocationId = tempDeliveryLocationIds.get(position);
                deliveryLocation = spinnerDeliveryLocation.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //Pickup Location Select
        spinnerPickUpLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Select a Location
                selectedPickupLocationId = tempPickupLocationIds.get(position);
                pickUpLocation = spinnerPickUpLocation.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //Delivery Item Type Select
        spinnerItemType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinnerItemType.getSelectedItem().toString().equalsIgnoreCase("Others")){
                    editTextOtherItems.setVisibility(View.VISIBLE);
                }else
                    editTextOtherItems.setVisibility(View.GONE);
                itemType = spinnerItemType.getSelectedItem().toString();
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

        //Proceed Button Click
        buttonProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSubmitPickupDetails();
            }
        });
    }


    //Attempt Submission of Pickup Details
    private void attemptSubmitPickupDetails() {
        // Reset errors.
        editTextFullName.setError(null);
        editTextPhone.setError(null);
        editTextAddress.setError(null);
        editTextOtherItems.setError(null);
        editTextItemDetails.setError(null);
        editTextPickupPointAddress.setError(null);

        // Store values at the time of submission.
        fullName = editTextFullName.getText().toString().trim();
        mPhone = editTextPhone.getText().toString().trim();
        mAddress = editTextAddress.getText().toString().trim();
        ghPostAddress = editTextGhPost.getText().toString().trim();
        otherItems = editTextOtherItems.getText().toString().trim();
        itemDetails = editTextItemDetails.getText().toString().trim();
        pickUpPointAddress = editTextPickupPointAddress.getText().toString().trim();
        pickUpPointGPSAddress = editTextPickupPointGPSAddress.getText().toString().trim();
        pickupPointContactNumber = editTextPickupPointContactNumber.getText().toString().trim();
        deliveryLocation = spinnerDeliveryLocation.getSelectedItem().toString();
        pickUpLocation = spinnerPickUpLocation.getSelectedItem().toString();
        if (spinnerItemType.getSelectedItem().toString().equalsIgnoreCase("Others")){
            itemType = editTextOtherItems.getText().toString().trim();
        }else
            itemType = spinnerItemType.getSelectedItem().toString();


        boolean cancel = false;
        View focusView = null;

        // Check for a valid fullName
        if (TextUtils.isEmpty(fullName)) {
            editTextFullName.setError(getString(R.string.error_field_required));
            focusView = editTextFullName;
            cancel = true;
        }
        // Check for a valid Phone
        if (TextUtils.isEmpty(mPhone)) {
            editTextPhone.setError(getString(R.string.error_field_required));
            focusView = editTextPhone;
            cancel = true;
        }
        // Check for a valid Address
        if (TextUtils.isEmpty(mAddress)) {
            editTextAddress.setError(getString(R.string.error_field_required));
            focusView = editTextAddress;
            cancel = true;
        }
        // Check for a valid otherItems
        if (spinnerItemType.getSelectedItem().toString().equalsIgnoreCase("Others") && TextUtils.isEmpty(otherItems)) {
            editTextOtherItems.setError(getString(R.string.error_field_required));
            focusView = editTextOtherItems;
            cancel = true;
        }
        // Check for a valid itemDetails
        if (TextUtils.isEmpty(itemDetails)) {
            editTextItemDetails.setError(getString(R.string.error_field_required));
            focusView = editTextItemDetails;
            cancel = true;
        }
        // Check for a valid pickUpPointAddress
        if (TextUtils.isEmpty(pickUpPointAddress)) {
            editTextPickupPointAddress.setError(getString(R.string.error_field_required));
            focusView = editTextPickupPointAddress;
            cancel = true;
        }
        // Check for a valid deliveryLocation
        if (TextUtils.isEmpty(deliveryLocation)) {
            spinnerDeliveryLocation.setPrompt(getString(R.string.error_field_required));
            focusView = spinnerDeliveryLocation;
            cancel = true;
        }
        // Check for a valid pickUpLocation
        if (TextUtils.isEmpty(pickUpLocation)) {
            spinnerPickUpLocation.setPrompt(getString(R.string.error_field_required));
            focusView = spinnerPickUpLocation;
            cancel = true;
        }
        // Check for a valid itemType
        if (TextUtils.isEmpty(itemType)) {
            editTextOtherItems.setError(getString(R.string.error_field_required));
            focusView = editTextOtherItems;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt sign up and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user sign up attempt.
            showProgress(true);
        }
    }


    //FUnction to load default details of customer
    private void loadDefaultCustomerDetails() {
        fullName = customer.getCustomerFistName().concat(" "+customer.getCustomerLastName());
        mPhone = customer.getCustomerPhone();
        mAddress = customer.getCustomerAddress();
        ghPostAddress = customer.getCustomerGhPostAddress();
        deliveryLocation = customer.getCustomerDeliveryLocation();

        editTextFullName.setText(fullName);
        editTextPhone.setText(mPhone);
        editTextAddress.setText(mAddress);
        editTextGhPost.setText(ghPostAddress);

        //Function to Enable or disable EditText fields onLoad
        enableDisableTextFields();


        //Load Delivery & PICKUP Locations
        loadDeliveryLocationList();

    }

    //Function to Enable or disable EditText fields onLoad
    private void enableDisableTextFields() {

        AutoCompleteTextView[] view = {editTextFullName,editTextPhone,editTextAddress,editTextGhPost};
        for (AutoCompleteTextView v:view) {
            if (v!=null && !TextUtils.isEmpty(v.getText().toString().trim())){
                v.setEnabled(false);
            }
        }

    }

    //FUnction to Handle Checkbox click
    private void addClickListenerToCheckBox() {
        checkBoxFullName.setOnClickListener(this);
        checkBoxPhone.setOnClickListener(this);
        checkBoxAddress.setOnClickListener(this);
        checkBoxGhPost.setOnClickListener(this);
    }

    //Function to Set TextViews to fit with Edit Text
    private void setViewsWidthToFit(){
        View[] view1 = {layoutFullName,layoutPhone,layoutAddress,layoutGhPost};
        View[] view = {layoutItemDetails, layoutOtherItems, layoutPickUpPointAddress,
                layoutPickUpPointGPSAddress, layoutPickupPointContactNumber};
        for (View v:view1) {
            v.getLayoutParams().width= (int)Math.round(Utilities.getScreenWidth(this)* 0.80);
        }
        for (View v:view) {
            v.getLayoutParams().width= (int)Math.round(Utilities.getScreenWidth(this)* 0.80);
        }
        buttonProceed.getLayoutParams().width= (int)Math.round(Utilities.getScreenWidth(this)* 0.5);
    }


    //    VOLLEY CONNECTION----------(For Delivery & PICKUP Location List)--------------VOLLEY CONNECTION
    private void loadDeliveryLocationList() {
        Call<List<DeliveryLocation>> call = apiInterfaceLoadDeliveryLocationList.loadDeliveryLocList();
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
                Toast.makeText(PickupServiceActivity.this, "Error. Check connection and try again", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void loadDeliveryLocationListResponse(List<DeliveryLocation> response) {
        //getting the whole json object from the response
        deliveryLocations = new ArrayList<>();
        pickupLocations = new ArrayList<>();
        tempDeliveryLocationIds = new ArrayList<>();
        tempPickupLocationIds = new ArrayList<>();
        tempDeliveryLocations = new ArrayList<>();
        tempPickupLocations = new ArrayList<>();

        //JSONObject
        DeliveryLocation deliveryLocat;

        //traversing through all the objects
        for (int i = 0; i < response.size(); i++) {
            //getting object from json response
            deliveryLocat = response.get(i);
            //adding the items to List
            tempDeliveryLocationIds.add(deliveryLocat.getLocationId());
            tempDeliveryLocations.add(deliveryLocat.getLocationName());
            tempPickupLocationIds.add(deliveryLocat.getLocationId());
            tempPickupLocations.add(deliveryLocat.getLocationName());
        }
        //Delivery Location Spinner
        spinnerAdapterDeliveryLocation = new ArrayAdapter<String>(PickupServiceActivity.this,
                android.R.layout.simple_list_item_1,tempDeliveryLocations);

        spinnerDeliveryLocation.setAdapter(spinnerAdapterDeliveryLocation);
        if (deliveryLocation != null) {
            if (!deliveryLocation.isEmpty()) {
                int spinnerPosition = spinnerAdapterDeliveryLocation.getPosition(deliveryLocation);
                spinnerDeliveryLocation.setSelection(spinnerPosition);
            }else {
                spinnerDeliveryLocation.setSelection(-1);
            }
        }
        //Pickup Location Spinner
        spinnerAdapterPickUpPointLocation = new ArrayAdapter<String>(PickupServiceActivity.this,
                android.R.layout.simple_list_item_1,tempPickupLocations);

        spinnerPickUpLocation.setAdapter(spinnerAdapterPickUpPointLocation);
        if (pickUpLocation != null) {
            if (!pickUpLocation.isEmpty()) {
                int spinnerPosition = spinnerAdapterPickUpPointLocation.getPosition(pickUpLocation);
                spinnerPickUpLocation.setSelection(spinnerPosition);
            }else {
                spinnerPickUpLocation.setSelection(-1);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_category, menu);
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

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_main_cat_share:
                //Function to Share App
                Utilities.shareApp(this);
//                Toast.makeText(getApplicationContext(),"Share",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_main_cat_my_account:
                //Open Back Office Account
                Utilities.openBackOfficeAccount(getApplicationContext());
                return true;
            case R.id.action_main_cat_sign_out:
                //Function to perform SignOut
                SharedPrefManager.getInstance(getApplicationContext()).logoutCustomer();
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.checkbox_pickup_service_full_name:
                //DO something
                if (!((CheckBox)v).isChecked()){
                    editTextFullName.setEnabled(true);
                }else{
                    editTextFullName.setEnabled(false);
                    editTextFullName.setText(fullName);
                }
                break;
            case R.id.checkbox_pickup_service_phone:
                //DO something
                if (!((CheckBox)v).isChecked()){
                    editTextPhone.setEnabled(true);
                }else{
                    editTextPhone.setEnabled(false);
                    editTextPhone.setText(mPhone);
                }
                break;
            case R.id.checkbox_pickup_service_address:
                //DO something
                if (!((CheckBox)v).isChecked()){
                    editTextAddress.setEnabled(true);
                }else{
                    editTextAddress.setEnabled(false);
                    editTextAddress.setText(mAddress);
                }
                break;
            case R.id.checkbox_pickup_service_gh_post_gps:
                //DO something
                if (!((CheckBox)v).isChecked()){
                    editTextGhPost.setEnabled(true);
                }else{
                    editTextGhPost.setEnabled(false);
                    editTextGhPost.setText(ghPostAddress);
                }
                break;
        }
    }

    /**
     * Shows the progress UI and hides the form.
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
interface RetrofitApiInterfaceLoadDeliveryLocationListPickup {
    @POST(URLs.URL_DELIVERY_LOCATION_LIST)
//    @FormUrlEncoded
    Call<List<DeliveryLocation>> loadDeliveryLocList();
}