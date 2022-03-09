package somame.amebleysystems.com.somame;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import Model.Customer;
import SharedPref.SharedPrefManager;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainMenu2 extends AppCompatActivity {
    CircleImageView imageViewUserImage;
    AppCompatTextView textViewUserName, textViewUserEmail;
    CardView cardViewOpenBasket, cardViewSignOut, cardViewMyAccount;
    ProgressBar progressBar;
    Customer customer;
    AlertDialog alertDialogBasket;
    String []genderValues = {"Grocery~Supermarket","Restaurant & Fastfood"};
    int basketOption=0;

    @Override
    protected void onResume() {
        super.onResume();
        //When Activity returns from Prev. Act.
        //Get and Set User Details
        loadCustomerDetails();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main_menu);
        setSupportActionBar(toolbar);
        //Views Init
        cardViewOpenBasket = findViewById(R.id.cardView_mainmenu_mybasket);
        cardViewSignOut = findViewById(R.id.cardView_mainmenu_sign_out);
        cardViewMyAccount = findViewById(R.id.button_mainmenu_myaccount);
        imageViewUserImage = findViewById(R.id.imageView_main_menu_user_profile);
        textViewUserName = findViewById(R.id.textview_main_menu_username);
        textViewUserEmail = findViewById(R.id.textview_main_menu_user_email);

        progressBar = findViewById(R.id.progress_main_menu);
        progressBar.setVisibility(View.GONE);

        //if the user is not logged in
        //starting the login activity
        if (!SharedPrefManager.getInstance(this).isCustomerLoggedIn()) {
            finish();
            startActivity(new Intent(this, SignInActivity.class));
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_main_menu);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                Intent ProductCatIntent = new Intent(MainMenu2.this, MainCategoryActivity.class);
                ProductCatIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                finish();
                startActivity(ProductCatIntent);
                progressBar.setVisibility(View.GONE);
            }
        });

        //My Account Click
        cardViewMyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(MainMenu2.this, UserAccountDetailsSetupActivity.class);
                startActivity(newIntent);
            }
        });

        //My basket Click
        cardViewOpenBasket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Utilities.gotoProductBasket(getApplicationContext());
                showBasketAlertDialog();
            }
        });

        //SignOut Click
        cardViewSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefManager.getInstance(getApplicationContext()).logoutCustomer();
                finish();
            }
        });

        //Get and Set User Details
        loadCustomerDetails();

    }

    //Function to Load and Set User Details
    private void loadCustomerDetails() {
        //Set Details for Current User
        customer = SharedPrefManager.getInstance(this).getCustomer();
        String customerPhoto = customer.getCustomerPhoto();
        String customerFullName = customer.getCustomerFistName()+" "+customer.getCustomerLastName();
        textViewUserName.setText(customerFullName);
        textViewUserEmail.setText(customer.getCustomerEmail());
        if (customerPhoto.isEmpty()){
            //No Photo Set
            imageViewUserImage.setImageResource(R.drawable.ic_person_profile_img);
        }else{
            //Load Custom Photo
            RequestOptions requestOptions = new RequestOptions();
            //loading the image
            Glide.with(getApplicationContext())
                    .load(customer.getCustomerPhoto())
                    .apply(requestOptions)
                    .into(imageViewUserImage);
        }
    }

    //    Back
    @Override
    public void onBackPressed() {
            super.onBackPressed();
            finish();
    }

    public void showBasketAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainMenu2.this);

        builder.setTitle("Select Your Choice");

        int checkedItem=0;//Select First Item by Default
        builder.setSingleChoiceItems(genderValues, checkedItem, new DialogInterface.OnClickListener() {


            public void onClick(DialogInterface dialog, int item) {

                switch(item)
                {
                    case 0:
                        basketOption = 0;
                        break;
                    case 1:
                        basketOption = 1;
                        break;
                }

            }
        }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (basketOption==0){
                    //GO to Product basket
                    Utilities.gotoProductBasket(getApplicationContext());
                }else{
                    //GO to Restaurant Basket
                    Utilities.gotoRestaurantBasket(getApplicationContext());
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialogBasket.dismiss();
            }
        });
        alertDialogBasket = builder.create();
        alertDialogBasket.show();

    }

}