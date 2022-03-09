package somame.amebleysystems.com.somame;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Toast;

import Model.Customer;
import SharedPref.SharedPrefManager;

public class MainCategoryActivity extends AppCompatActivity implements View.OnClickListener{
    CardView cardViewGrocery, cardViewRestaurant, cardViewPickUp;
    Customer customer;
    ProgressBar progressBar;
    private boolean doubleBackToExitPressedOnce = false;
    int customerId;
    private Menu menu;

    //    Back to Exit
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finish();
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Touch again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_category);
//        Toolbar Declare
        Toolbar toolbar = findViewById(R.id.toolbar_main_cat);
        toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        toolbar.setSubtitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        cardViewGrocery = findViewById(R.id.cardView_main_cat_button1);
        cardViewRestaurant = findViewById(R.id.cardView_main_cat_button2);
        cardViewPickUp = findViewById(R.id.cardView_main_cat_button3);

        customer = SharedPrefManager.getInstance(getApplicationContext()).getCustomer();
        customerId = customer.getCustomerId();

        //Check if user is logged in
        if (!SharedPrefManager.getInstance(this).isCustomerLoggedIn()) {
            finish();
            startActivity(new Intent(this, SignInActivity.class));
        }

//        ProgressBar
        progressBar = findViewById(R.id.progress_main_cat);

//        More On Toolbar
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_back);
        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                    finish();
                        onBackPressed();
                    }
                }
        );

        //Handle CardView Clicks
        cardViewClickListener();

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
            case R.id.action_main_cat_agent:
                //Open Agent
                startActivity(new Intent(MainCategoryActivity.this, AgentSignInActivity.class));
                finish();
                return true;
            case R.id.action_main_cat_my_account:
                //Open Back Office Account
                Utilities.openBackOfficeAccount(MainCategoryActivity.this);
                return true;
            case R.id.action_main_cat_sign_out:
                //Function to perform SignOut
                SharedPrefManager.getInstance(MainCategoryActivity.this).logoutCustomer();
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Function to Handle CardView clicks
    private void cardViewClickListener(){
        cardViewGrocery.setOnClickListener(this);
        cardViewRestaurant.setOnClickListener(this);
        cardViewPickUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cardView_main_cat_button1:
                //DO something
                startActivity(new Intent(MainCategoryActivity.this, ProductCatActivity.class));
                break;
            case R.id.cardView_main_cat_button2:
                //DO something
                startActivity(new Intent(MainCategoryActivity.this, SelectRestaurantActivity.class));
                break;
            case R.id.cardView_main_cat_button3:
                //DO something
                startActivity(new Intent(MainCategoryActivity.this, PickupServiceActivity.class));
//                Toast.makeText(this, "Option not available", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
