package somame.amebleysystems.com.somame;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.Locale;

import Model.Customer;
import SharedPref.SharedPrefManager;

public class PaymentActivity extends AppCompatActivity {
    Toolbar toolbar;
    ProgressBar progressBar;
    LinearLayout linearLayout, layoutAllFields;
    AppCompatTextView textViewFullName, textViewOrderNumber, textViewNoOfItems, textViewOrderTotal;
    AutoCompleteTextView editTextMoMoNumber;
    TextInputLayout layoutMoMoNumber;
    Spinner spinnerPaymentMethod;
    Button buttonCompletePayment;
    String firstName, lastName, fullName, orderNumber,  momoNumber;
    int noOfItems;
    Double orderTotal;
    ArrayAdapter<String> spinnerAdapterPaymentMethod;
    Customer customer;
    int customerId;
    private Bundle extras;
    //Setting Currency for Price Text
    Locale locale = new Locale("en", "GH");
    NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);


    @Override
    public void onBackPressed() {
        //Send a response back to previous activity
        Intent backIntent = new Intent();
        backIntent.putExtra("returnedFromPrevious", 1);
        backIntent.putExtra("returnedOrderNumber", orderNumber);
        setResult(Activity.RESULT_OK, backIntent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        linearLayout = findViewById(R.id.layout_payment_linear);
        layoutAllFields = findViewById(R.id.layout_payment_all_fields);
        textViewFullName = findViewById(R.id.textView_payment_customer_name);
        textViewOrderNumber = findViewById(R.id.textView_payment_order_no);
        textViewNoOfItems = findViewById(R.id.textView_payment_no_of_items);
        textViewOrderTotal = findViewById(R.id.textView_payment_total);
        editTextMoMoNumber = findViewById(R.id.edittext_payment_momo_number);
//        editTextMoMoNumber.setEnabled(false);
        layoutMoMoNumber = findViewById(R.id.textInput_payment_momo_number);
        spinnerPaymentMethod = findViewById(R.id.spinner_payment_payment_method);
        buttonCompletePayment = findViewById(R.id.button_payment_pay_now);

        //GET Intent Here
        extras = getIntent().getExtras();
        if (extras != null) {
            //Retrieve order details from received extras
            getOrderDetailsFromExtras();
            //Set received details to view
            setOrderDetailsToView();

        }else {
            Toast.makeText(this, "Error while processing. Try again", Toast.LENGTH_LONG).show();
            finish();
        }

        //Customer
        customer = SharedPrefManager.getInstance(getApplicationContext()).getCustomer();
        customerId = customer.getCustomerId();

        //Check if user is logged in
        if (!SharedPrefManager.getInstance(this).isCustomerLoggedIn()) {
            finish();
            startActivity(new Intent(this, SignInActivity.class));
        }

        //        Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_payment);
        toolbar.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setSubtitle(getString(R.string.complete_payment));
        toolbar.setSubtitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

//      Progressbar
        progressBar = findViewById(R.id.progress_payment);

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
        spinnerAdapterPaymentMethod = new ArrayAdapter<String>(PaymentActivity.this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.payment_method));

        spinnerAdapterPaymentMethod.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPaymentMethod.setAdapter(spinnerAdapterPaymentMethod);

        //Button Pay Click
        buttonCompletePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateFields();
            }
        });

        //Make EditTextViews Fit
        setViewsWidthToFit();

    }

    //Function to check inputs
    private void validateFields() {
        boolean cancel = false;
        View focusView = null;

        // Check for a valid fullName
        if (TextUtils.isEmpty(momoNumber)) {
            editTextMoMoNumber.setError(getString(R.string.error_field_required));
            focusView = editTextMoMoNumber;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt sign up and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
    }

    //Function to set received extras to views
    private void setOrderDetailsToView() {
        textViewFullName.setText(fullName);
        textViewOrderNumber.setText(new StringBuilder().append(textViewOrderNumber.getText()).append(": ").append(orderNumber).toString());
        textViewNoOfItems.setText(new StringBuilder().append(textViewNoOfItems.getText()).append(": ").append("-").toString());
        //        Setting Currency for Total Text
        textViewOrderTotal.setText(new StringBuilder().append(": ").append(fmt.format(orderTotal)));
        editTextMoMoNumber.setText(momoNumber);
    }

    //Function to retrieve order details from extras
    private void getOrderDetailsFromExtras() {
        orderNumber = extras.getString("OrderNumber");
        fullName = extras.getString("CustomerName");
        momoNumber = extras.getString("momoNumber");
        noOfItems = extras.getInt("NoOfItems");
        orderTotal = extras.getDouble("TotalCost");
    }


    //Function to Set TextViews to fit with Edit Text
    private void setViewsWidthToFit(){
        View[] view = {layoutMoMoNumber};
        for (View v:view) {
            v.getLayoutParams().width= (int)Math.round(Utilities.getScreenWidth(this)* 0.80);
        }
        buttonCompletePayment.getLayoutParams().width= (int)Math.round(Utilities.getScreenWidth(this)* 0.5);
    }

    //Menu On Toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_payment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_payment_share:
                //Do something
                Utilities.shareApp(this);
                return true;
            case R.id.action_payment_my_account:
                //Do something
                Utilities.openBackOfficeAccount(PaymentActivity.this);
                return true;
            case R.id.action_payment_my_orders:
                //Do something
                Utilities.gotoMyOrders(PaymentActivity.this);
                return true;
            case R.id.action_payment_sign_out:
                //Function to perform SignOut
                SharedPrefManager.getInstance(PaymentActivity.this).logoutCustomer();
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Function to Move User to Prev(Basket) Activity
    private void navigatePreviousActivity() {
//        Intent basketIntent = new Intent(PaymentActivity.this, CheckOutActivity.class);
//        startActivity(basketIntent);
//        finish();

    }
}