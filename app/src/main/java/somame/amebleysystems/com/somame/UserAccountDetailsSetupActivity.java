package somame.amebleysystems.com.somame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.NonNull;
import com.google.android.material.appbar.AppBarLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.SubtitleCollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.google.android.material.widget.SubtitleCollapsingToolbarLayout;
//import com.google.android.material.appbar.SubtitleCollapsingToolbarLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.palette.graphics.Palette;
import androidx.appcompat.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import Model.Customer;
import Model.DeliveryLocation;
import Model.Product;
import SharedPref.SharedPrefManager;
import Util.RetrofitApiClient;
import Util.RetrofitApiClient2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;

public class UserAccountDetailsSetupActivity extends AppCompatActivity {
    public static RetrofitApiInterfaceChangePassword apiInterfaceChangePassword;
    public static RetrofitApiInterfaceSaveDetails apiInterfaceSaveDetails;
    public static RetrofitApiInterfaceLoadDeliveryLocationList apiInterfaceLoadDeliveryLocationList;
    public static RetrofitApiInterfaceUserAccountLoadCustomer apiInterfaceLoadCustomer;

    TextView textAppName, textViewSubtitle, textViewFullName, textViewEmail, textViewDOB,
            textViewAddress, textViewGhPostAddress, textViewPhone, textViewGender;
    ImageView imageViewUserProfile, imageViewEditName, imageViewEditPassword, imageViewEditDOB, imageViewEditPhone,
            imageViewEditAddress, imageViewEditGhPost, imageViewEditGender;
//    RadioGroup radioGroup;
    RadioButton radioGenderButton;
    Spinner spinnerDeliveryLocation;
    String fullName="", email="", currPassword="", newPassword="", confirmNewPassword="", dOB="", gender="", phone="", address="",
            deliveryLoc="", ghPostAddress="", firstName="", lastName="",tempGender="", checkSelectedDate="";
    CoordinatorLayout coordinatorLayout;
    FloatingActionButton fabEditProfile;
    AppBarLayout appBarLayout;
    ProgressBar progressBar;
    LinearLayout linearLayout;
    Toolbar toolbar;
    //More
    private SimpleDateFormat dateFormat;
    private Calendar calendar;
    Date currentDate;
    private int year, month, day, ageYears;
    List<String> deliveryLocations;
    List<String> tempDeliveryLocations;
    List<Integer> tempDeliveryLocationIds;
    int selectedDeliveryLocationId=0;
    CharSequence[] genderValues = {"Male","Female","Hide"};
    AlertDialog alertDialogGender;
    Customer customer;
    //creating Spinner List
    ArrayAdapter<String> spinnerAdapter;
    SubtitleCollapsingToolbarLayout collapsingToolbarLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Retrofit api
        apiInterfaceChangePassword = RetrofitApiClient.getApiClient().create(RetrofitApiInterfaceChangePassword.class);
        apiInterfaceSaveDetails = RetrofitApiClient.getApiClient().create(RetrofitApiInterfaceSaveDetails.class);
        apiInterfaceLoadDeliveryLocationList = RetrofitApiClient2.getApiClient().create(RetrofitApiInterfaceLoadDeliveryLocationList.class);
        apiInterfaceLoadCustomer = RetrofitApiClient.getApiClient().create(RetrofitApiInterfaceUserAccountLoadCustomer.class);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account_details_setup);

        //Check if user is logged in
        if (!SharedPrefManager.getInstance(this).isCustomerLoggedIn()) {
            finish();
            startActivity(new Intent(this, SignInActivity.class));
        }

        //Get Current User
//        customer = SharedPrefManager.getInstance(this).getCustomer();

        //View Init
//        radioGroup = findViewById(R.id.radio_group_user_acc_setup_gender);
        textViewFullName = findViewById(R.id.textView_user_acc_details_setup_full_name);
        textViewEmail = findViewById(R.id.textView_user_acc_details_setup_email);
        textViewDOB = findViewById(R.id.textView_user_acc_details_setup_dob);
        textViewAddress = findViewById(R.id.textView_user_acc_details_setup_address);
        textViewGender = findViewById(R.id.textView_user_acc_details_setup_gender);
        textViewGhPostAddress = findViewById(R.id.textView_user_acc_details_setup_gh_post_gps);
        spinnerDeliveryLocation = findViewById(R.id.spinner_user_acc_details_setup_delivery_location);
//        spinnerGender = findViewById(R.id.spinner_user_acc_details_setup_gender);
        textViewPhone = findViewById(R.id.textView_user_acc_details_setup_phone);
        imageViewEditName = findViewById(R.id.imageView_user_acc_details_setup_full_name);
        imageViewEditPassword = findViewById(R.id.imageView_user_acc_details_setup_password);
        imageViewEditDOB = findViewById(R.id.imageView_user_acc_details_setup_dob);
        imageViewEditPhone = findViewById(R.id.imageView_user_acc_details_setup_phone);
        imageViewEditAddress = findViewById(R.id.imageView_user_acc_details_setup_address);
        imageViewEditGender = findViewById(R.id.imageView_user_acc_details_setup_gender);
        imageViewEditGhPost = findViewById(R.id.imageView_user_acc_details_setup_gh_post_gps);
        fabEditProfile = findViewById(R.id.fab_user_acc_details_setup_edit);
        linearLayout = findViewById(R.id.layout_user_acc_details_setup_all_fields);
        progressBar = findViewById(R.id.progress_user_acc_details_setup);
        tempGender = String.valueOf(genderValues[2]);

        //More on Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_user_acc_details_setup);
        toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        setSupportActionBar(toolbar);
        toolbar.setSubtitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Check if Date was changed
                if (!checkSelectedDate.equalsIgnoreCase(textViewDOB.getText().toString().trim()))
                    Utilities.madeChangesToData=true;
                if(Utilities.madeChangesToData){
                    showConfirmExitAlertDialog();
                }else
                {
                onBackPressed();
                finish();
                }
            }
        });

        //More Tweaks for Beautiful Display
        //This snippet maintains image height dynamically
        final Display dWidth = getWindowManager().getDefaultDisplay();
        coordinatorLayout = findViewById(R.id.coordinator_user_acc_details_setup);
        appBarLayout = findViewById(R.id.appBar_user_acc_details_setup);

        //Pre-collapse height of Collapsing toolbar using set offset bar method (One-third part will be collapsed)
        appBarLayout.post(new Runnable() {
            @Override
            public void run() {
                int heightPx = dWidth.getWidth() * 1 / 3;
                setAppBarOffset(heightPx);
            }
        });

        //More on Date Picker
        calendar = Calendar.getInstance(Locale.US);
        year = calendar.get(Calendar.YEAR);
        currentDate = calendar.getTime();
        dateFormat = new SimpleDateFormat("yyyy/mm/dd", Locale.US);

        month = calendar.get(MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month+1, day);
        imageViewEditDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(v);
            }
        });

        //Profile Picture and Toolbar
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbar_user_acc_details_setup);
        imageViewUserProfile = findViewById(R.id.imageView_user_acc_details_setup_profile);
        imageViewUserProfile.getLayoutParams().height = (int)(Utilities.getScreenHeight(this)*(0.4));

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
        collapsingToolbarLayout.setSubtitle("Subtitle");
        CharSequence accSetup = "Account Setup";
        collapsingToolbarLayout.setTitle((accSetup));

        //ImageView Clicks
        imageViewEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditNameAlertDialog();
            }
        });
        imageViewEditPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditPasswordAlertDialog();
            }
        });
        imageViewEditPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditPhoneAlertDialog();
            }
        });
        imageViewEditAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditAddressAlertDialog();
            }
        });
        imageViewEditGhPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditGhPostAddressAlertDialog();
            }
        });
        imageViewEditGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showEditGenderAlertDialog();
                CreateGenderAlertDialog();
            }
        });

        //Set TextViews Width To Fit With Edit(Pencil) Button
        setTextViewsWidthToFit();

        //-----------Load User Details to Views------------
        loadCustomerDetails();

        //On Click to Select Location from Spinner
        spinnerDeliveryLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Load Restaurant List Based on Location
                selectedDeliveryLocationId = tempDeliveryLocationIds.get(position);
                if (!deliveryLoc.equalsIgnoreCase(spinnerDeliveryLocation.getSelectedItem().toString())){

                }
                deliveryLoc = spinnerDeliveryLocation.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Check Date
        checkSelectedDate = dOB;
//        Toast.makeText(this, "Date "+checkSelectedDate, Toast.LENGTH_SHORT).show();
    }


    //Function to load customer details from SharedPref
    private void loadCustomerDetails() {
        //Load customer details from DB
        loadCustomerDetailsFromDB();
        customer = SharedPrefManager.getInstance(UserAccountDetailsSetupActivity.this).getCustomer();
        //Setting Variables
        firstName = customer.getCustomerFistName();
        lastName = customer.getCustomerLastName();
        fullName = firstName+" "+lastName;
        email = customer.getCustomerEmail();
        gender = customer.getCustomerGender();
        address = customer.getCustomerAddress();
        ghPostAddress = customer.getCustomerGhPostAddress();
        deliveryLoc = customer.getCustomerDeliveryLocation();
        phone = customer.getCustomerPhone();
        dOB = customer.getCustomerDOB();
        //Set Subtitle
        collapsingToolbarLayout.setSubtitle(fullName);
        //Setting Views
        textViewFullName.setText(fullName);
        textViewEmail.setText(email);
        textViewGender.setText((gender.equalsIgnoreCase(""))?genderValues[2]:gender);
//        Toast.makeText(this, gender, Toast.LENGTH_SHORT).show();
        textViewAddress.setText((address.equalsIgnoreCase(""))?"None":address);
        textViewGhPostAddress.setText((ghPostAddress.equalsIgnoreCase(""))?"None":ghPostAddress);
        textViewPhone.setText(phone);
        textViewDOB.setText(dOB);
        //Spinner Drop Down for Location
        loadDeliveryLocationList();

    }

    //Function to get Age difference in Years
    private int getAgeYears(String firstDate, Date lastDate) {
        int difference=0;
        try {
            Date newFirst = new SimpleDateFormat("yyyy-mm-dd",Locale.US).parse(firstDate);
//            Date newLast = new SimpleDateFormat("yyyy/mm/dd",Locale.US).parse(firstDate);
            Calendar calendarFirstDate = getCalendar(newFirst);
            Calendar calendarLastDate = getCalendar(lastDate);

            difference = calendarLastDate.get(Calendar.YEAR) - calendarFirstDate.get(Calendar.YEAR);
            if (calendarFirstDate.get(MONTH) > calendarLastDate.get(MONTH) ||
                    (calendarFirstDate.get(MONTH) == calendarLastDate.get(MONTH) && calendarFirstDate.get(DATE) > calendarLastDate.get(DATE))){
                difference--;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return difference;
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);//A dummy id, not really mean anything
    }

    public static Calendar getCalendar(Date date){
        Calendar calendar = Calendar.getInstance(Locale.US);
        calendar.setTime(date);
        return calendar;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2+1, arg3);
//                    Toast.makeText(getApplicationContext(),String.valueOf(ageYears),Toast.LENGTH_SHORT).show();
                }
            };

    private void showDate(int year, int month, int day) {
        textViewDOB.setText(new StringBuilder().append(year).append("-")
                .append(month).append("-").append(day));
        dOB = textViewDOB.getText().toString();
        textViewDOB.setError(null);
//        Utilities.madeChangesToData=true;
        //Function to get age in Years
        ageYears = getAgeYears(dOB, currentDate);
//        Toast.makeText(getApplicationContext(),String.valueOf(ageYears),Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onBackPressed() {
        //Check if Date was changed
        if (!checkSelectedDate.equalsIgnoreCase(textViewDOB.getText().toString().trim()))
            Utilities.madeChangesToData=true;
        if(Utilities.madeChangesToData){
        showConfirmExitAlertDialog();
        }else
        {
            finish();
        }
    }

    //Set AppBar Offset Function
    private void setAppBarOffset(int offsetPx) {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        if (behavior != null) {
            behavior.onNestedPreScroll(coordinatorLayout, appBarLayout, null, 0, offsetPx, new int[]{0, 0});
        }
    }

    //Menu On Toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_account_details_setup_menu, menu);
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
            case R.id.action_user_acc_details_share:
                //Do something
                Utilities.shareApp(this);
                return true;
            case R.id.action_user_acc_details_save:
                //Save Changes
                saveChangesToDB();
                return true;
            case R.id.action_user_acc_details_sign_out:
                //Function to perform SignOut
                SharedPrefManager.getInstance(this).logoutCustomer();
                finish();
                return true;
            default:
                //Do something
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //Function to save user detail changes
    private void saveChangesToDB() {

        if (!checkSelectedDate.equalsIgnoreCase(textViewDOB.getText().toString().trim()))
            Utilities.madeChangesToData=true;
        if (Utilities.madeChangesToData){
            attemptSaveChangesToDB();
            Utilities.madeChangesToData=false;
        }
    }


    //Functions for Edit Details
    private void showEditNameAlertDialog() {
//      Use layout created in xml and inflate as our AlertDialog EditText
        View view = LayoutInflater.from(this).inflate(R.layout.layout_user_acc_setup_fullname, null);

        final EditText edittextFirstName = view.findViewById(R.id.dialog_edittext_user_acc_setup_firstname);
        final EditText edittextLastName = view.findViewById(R.id.dialog_edittext_user_acc_setup_lastname);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage("Test for preventing dialog close");
        builder.setTitle("Change your name");
        builder.setView(view);

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });
        edittextFirstName.setText(firstName);
        edittextLastName.setText(lastName);

        final AlertDialog dialog = builder.create();
        dialog.show();
        //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Do stuff, possibly set wantToCloseDialog to true then...
                boolean dialogError=false;
//                if(wantToCloseDialog)
                if (TextUtils.isEmpty(edittextFirstName.getText().toString().trim())){
                    edittextFirstName.setError(getString(R.string.error_field_required));
                    edittextFirstName.requestFocus();
                    dialogError=true;
                }
                if (TextUtils.isEmpty(edittextLastName.getText().toString().trim())){
                    edittextLastName.setError(getString(R.string.error_field_required));
                    edittextLastName.requestFocus();
                    dialogError=true;
                }
                if (!dialogError){
                    //Set New Password
                    dialog.dismiss();
                    firstName = edittextFirstName.getText().toString().trim();
                    lastName = edittextLastName.getText().toString().trim();
                    fullName = firstName.concat(" "+lastName);
                    textViewFullName.setText(fullName);
                    Utilities.madeChangesToData=true;
                }

            }
        });

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });
    }

    private void showEditPasswordAlertDialog() {
//      Use layout created in xml and inflate as our AlertDialog EditText
        View view = LayoutInflater.from(this).inflate(R.layout.layout_user_acc_setup_password, null);

        final EditText edittextPasswordCurrent = view.findViewById(R.id.dialog_edittext_user_acc_setup_current_password);
        final EditText edittextPasswordNew = view.findViewById(R.id.dialog_edittext_user_acc_setup_new_password);
        final EditText edittextPasswordConfirm = view.findViewById(R.id.dialog_edittext_user_acc_setup_confirm_password);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage("Test for preventing dialog close");
        builder.setTitle("Change password");
        builder.setView(view);
        currPassword="";
        newPassword="";
        confirmNewPassword="";

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
        //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Do stuff, possibly set wantToCloseDialog to true then...
                currPassword= edittextPasswordCurrent.getText().toString().trim();
                newPassword= edittextPasswordNew.getText().toString().trim();
                confirmNewPassword= edittextPasswordConfirm.getText().toString().trim();
                boolean dialogError=false;
//                if(wantToCloseDialog)
                if (TextUtils.isEmpty(currPassword)){
                    edittextPasswordCurrent.setError(getString(R.string.error_field_required));
                    edittextPasswordCurrent.requestFocus();
                    dialogError=true;
                }
                if (TextUtils.isEmpty(newPassword)){
                    edittextPasswordNew.setError(getString(R.string.error_field_required));
                    edittextPasswordNew.requestFocus();
                    dialogError=true;
                }
                if (!TextUtils.isEmpty(newPassword) && !newPassword.equals(confirmNewPassword)){
                    edittextPasswordConfirm.setError(getString(R.string.error_mismatch_password));
                    edittextPasswordConfirm.requestFocus();
                    dialogError=true;
                }
                if (!TextUtils.isEmpty(currPassword) && !TextUtils.isEmpty(newPassword)&& !TextUtils.isEmpty(confirmNewPassword) && !isPasswordValid(newPassword)){
                    edittextPasswordNew.setError(getString(R.string.error_invalid_password));
                    edittextPasswordNew.requestFocus();
                    dialogError=true;
                }
                    if (!dialogError){
                        //Set New Password
                        dialog.dismiss();
                        attemptPasswordChange();
                    }

            }
        });

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                    dialog.dismiss();
            }
        });

    }


    private void showEditPhoneAlertDialog() {
//      Use layout created in xml and inflate as our AlertDialog EditText
        View view = LayoutInflater.from(this).inflate(R.layout.layout_user_acc_setup_phone, null);
        final EditText edittextPhone = view.findViewById(R.id.dialog_edittext_user_acc_setup_phone);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage("Test for preventing dialog close");
        builder.setTitle("Change phone number");
        builder.setView(view);

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });

        edittextPhone.setText(phone);
        final AlertDialog dialog = builder.create();
        dialog.show();
        //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                boolean dialogError=false;
                if (TextUtils.isEmpty(edittextPhone.getText().toString().trim())) {
                    edittextPhone.setError(getString(R.string.error_field_required));
                    edittextPhone.requestFocus();
                    dialogError=true;
                }
                if (!isPhoneValid(edittextPhone.getText().toString().trim())) {
                    edittextPhone.setError(getString(R.string.error_invalid_phone));
                    edittextPhone.requestFocus();
                    dialogError=true;
                }
                if (!dialogError){
                    //Set New Phone
                    phone= edittextPhone.getText().toString().trim();
                    textViewPhone.setText(phone);
                    dialog.dismiss();
                    Utilities.madeChangesToData=true;
                }
                //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
            }
        });

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                    dialog.dismiss();
            }
        });

    }

    private void showEditAddressAlertDialog() {
//      Use layout created in xml and inflate as our AlertDialog EditText
        View view = LayoutInflater.from(this).inflate(R.layout.layout_user_acc_setup_address, null);
        final EditText edittextAddress = view.findViewById(R.id.dialog_edittext_user_acc_setup_address);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change mAddress");
        builder.setView(view);

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });

        edittextAddress.setText(address);
        final AlertDialog dialog = builder.create();
        dialog.show();
        //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                boolean dialogError=false;
                if (TextUtils.isEmpty(edittextAddress.getText().toString().trim())) {
                    edittextAddress.setError(getString(R.string.error_field_required));
                    edittextAddress.requestFocus();
                    dialogError=true;
                }
                if (!dialogError){
                    //Set New Phone
                    address= edittextAddress.getText().toString().trim();
                    textViewAddress.setText(address);
                    dialog.dismiss();
                    Utilities.madeChangesToData=true;
                }
                //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
            }
        });

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });
    }

    private void showConfirmExitAlertDialog(){
        //      Use layout created in xml and inflate as our AlertDialog EditText
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Exit");
        builder.setMessage("Exit without saving changes?");
//        builder.setView(view);

        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
        //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
                Utilities.madeChangesToData=false;
                finish();
            }
        });

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });
    }

    private void showEditGhPostAddressAlertDialog() {
//      Use layout created in xml and inflate as our AlertDialog EditText
        View view = LayoutInflater.from(this).inflate(R.layout.layout_user_acc_setup_gh_post_gps, null);

        final EditText edittextGhPostAddress = view.findViewById(R.id.dialog_edittext_user_acc_setup_gh_post_address);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit your GH Post mAddress");
        builder.setView(view);

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });

        edittextGhPostAddress.setText(ghPostAddress);
        final AlertDialog dialog = builder.create();
        dialog.show();
        //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                    //Set New Phone
                    ghPostAddress= edittextGhPostAddress.getText().toString().trim();
                    textViewGhPostAddress.setText(ghPostAddress);
                    dialog.dismiss();
                Utilities.madeChangesToData=true;
            }
        });

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });

    }

//    private void showEditGenderAlertDialog() {
////      Use layout created in xml and inflate as our AlertDialog EditText
//        final View view = LayoutInflater.from(this).inflate(R.layout.layout_user_acc_setup_gender, null);
//
//        final RadioGroup radioGroupGender= view.findViewById(R.id.radio_group_user_acc_setup_gender);
//        final AlertDialog.Builder alertdialog = new AlertDialog.Builder(this, R.style.Theme_AppCompat_DayNight_Dialog);
//
//        alertdialog.setTitle("Select your gender");
//        alertdialog.setView(view);
//
//        //Okay Button
//        alertdialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//                //Setting text
////                radioClickUserAccSetupGender(view);
//                int selectedId = radioGroupGender.getCheckedRadioButtonId();
//                radioGenderButton = (RadioButton)findViewById(selectedId);
//                // radioButton text
//                String radiovalue = String.valueOf(radioGenderButton.getText());
//                Toast.makeText(UserAccountDetailsSetupActivity.this, radiovalue, Toast.LENGTH_SHORT).show();
//            }
//        });
//        //Cancel Button
//        alertdialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//                // Cancel option.
//                dialog.cancel();
//            }
//        });
//
//        alertdialog.show();
//    }

    public void CreateGenderAlertDialog(){
//        final String[] tempGender = new String[1];

        AlertDialog.Builder builder = new AlertDialog.Builder(UserAccountDetailsSetupActivity.this);

        builder.setTitle("Select Your Choice");

        int checkedItem=-1;
        if(gender!=null&&gender.equalsIgnoreCase("male")){
            checkedItem=0;
        }else if(gender!=null&&gender.equalsIgnoreCase("female")){
            checkedItem=1;
        }else{
            checkedItem=2;
            }

        builder.setSingleChoiceItems(genderValues, checkedItem, new DialogInterface.OnClickListener() {


            public void onClick(DialogInterface dialog, int item) {

                switch(item)
                {
                    case 0:
//                        Toast.makeText(UserAccountDetailsSetupActivity.this, "First Item Clicked", Toast.LENGTH_LONG).show();
                        tempGender = String.valueOf(genderValues[0]);
                        break;
                    case 1:
//                        Toast.makeText(UserAccountDetailsSetupActivity.this, "Second Item Clicked", Toast.LENGTH_LONG).show();
                        tempGender = String.valueOf(genderValues[1]);
                        break;
                    case 2:
//                        Toast.makeText(UserAccountDetailsSetupActivity.this, "Third Item Clicked", Toast.LENGTH_LONG).show();
                        tempGender = String.valueOf(genderValues[2]);
                        break;
                }
//                alertDialogGender.dismiss();
            }
        }).setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gender = tempGender;
                textViewGender.setText(gender);
                Utilities.madeChangesToData=true;
//                Toast.makeText(UserAccountDetailsSetupActivity.this, tempGender, Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialogGender.dismiss();
            }
        });
        alertDialogGender = builder.create();
        alertDialogGender.show();

    }


    //    CONNECTION----------(For Password Change)-------------- CONNECTION
    private void attemptPasswordChange() {
        // Show a progress spinner, and kick off a background task to
        // perform the user sign up attempt.
        showProgress(true);

        //            ------------------------RETROFIT----------CHANGE PASS--------------------------------
            /*
        Retrofit Network Connection
        * */
        Call<String> call = apiInterfaceChangePassword.changePass(String.valueOf(email), String.valueOf(currPassword), String.valueOf(newPassword));
        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                Log.e("Change_Pass_TAG", "response: "+new Gson().toJson(response.body()) );
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.e("onSuccess", response.body());
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(response.body());
                            //Success sign in
                            if(obj.optString("success").equals("true")){
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            } else if (obj.has("error")) {
                                //Invalid details
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                //Hide Progress Bar
                                showProgress(false);
                            }
                            //Hide Progress Bar
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
                //Hide Progress Bar
                showProgress(false);
                Toast.makeText(UserAccountDetailsSetupActivity.this, "Error. Check connection and try again", Toast.LENGTH_SHORT).show();
            }
        });

    }

    //    CONNECTION----------(For Save Details to DB)------------- CONNECTION
    private void attemptSaveChangesToDB() {
        // Show a progress spinner, and kick off a background task to
        // perform the user sign up attempt.
        showProgress(true);

        //            ------------------------RETROFIT----------CHANGE PASS--------------------------------
            /*
        Retrofit Network Connection
        * */
        Call<String> call = apiInterfaceSaveDetails.saveChanges(email, firstName, lastName, dOB, gender, phone, address, deliveryLoc, ghPostAddress);
        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                Log.e("Save_TAG", "response: "+new Gson().toJson(response.body()) );
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.e("onSuccess", response.body());
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(response.body());
                            //Success sign in
                            if(obj.optString("success").equals("true")){
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                Utilities.madeChangesToData=false;
                                //Update Check Date
                                checkSelectedDate=textViewDOB.getText().toString().trim();
                            } else if (obj.has("error")) {
                                //Invalid details
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                //Hide Progress Bar
                                showProgress(false);
                            }
                            //Hide Progress Bar
                            showProgress(false);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("onEmptyResponse", "Returned empty response");
                        //Hide Progress Bar
                        showProgress(false);
                        Toast.makeText(UserAccountDetailsSetupActivity.this, "Error. Check connection and try again", Toast.LENGTH_SHORT).show();
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

    //    RETROFIT CONNECTION----------(For Delivery Location List)--------------RETROFIT CONNECTION
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
            }
        });

    }
    private void loadDeliveryLocationListResponse(List<DeliveryLocation> response) {
        //getting the whole json object from the response
        deliveryLocations = new ArrayList<>();
        tempDeliveryLocationIds = new ArrayList<>();
        tempDeliveryLocations = new ArrayList<>();

        //JSONObject
        DeliveryLocation deliveryLocat;

        //traversing through all the objects
        for (int i = 0; i < response.size(); i++) {
            //getting object from json response
            deliveryLocat = response.get(i);
            //adding the items to List
            tempDeliveryLocationIds.add(deliveryLocat.getLocationId());
            tempDeliveryLocations.add(deliveryLocat.getLocationName());
            Utilities.setTransportationCost(deliveryLocat.getLocationName(),deliveryLocat.getDeliveryCharge());
        }
        spinnerAdapter = new ArrayAdapter<String>(UserAccountDetailsSetupActivity.this,
                android.R.layout.simple_list_item_1,tempDeliveryLocations);

        spinnerDeliveryLocation.setAdapter(spinnerAdapter);
        if (deliveryLoc != null) {
            if (!deliveryLoc.isEmpty()) {
//                                    Toast.makeText(UserAccountDetailsSetupActivity.this, "Delivery has "+deliveryLoc, Toast.LENGTH_SHORT).show();
                int spinnerPosition = spinnerAdapter.getPosition(deliveryLoc);
                spinnerDeliveryLocation.setSelection(spinnerPosition);
            }else {
                spinnerDeliveryLocation.setSelection(-1);
            }
        }
    }

    //RETROFIT CONNECTION -----------(Load Customer Details)-------------------RETROFIT CONNECTION
    private void loadCustomerDetailsFromDB() {
        //Show Progress Bar
        showProgress(true);

        Call<String> call =apiInterfaceLoadCustomer.getCustomer(email);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {

                Log.e("Load_CustomerResponse: ", response.body());
                //Toast.makeText()
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.e("onSuccess", response.body());

                        String jsonResponse = response.body();
                        loadResponse(jsonResponse);

                    } else {
                        Log.e("onEmptyResponse", "Returned empty response");
                        //Hide Progress Bar
                        showProgress(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                //display error message
                Log.d("retrofit_log", "Failed! Error = " + t.getMessage());
                //Hide Progress Bar
                showProgress(false);
                Toast.makeText(UserAccountDetailsSetupActivity.this, "Error. Check connection and try again", Toast.LENGTH_SHORT).show();
            }
        });

        }
    //This function handles the received Retrofit response
    private void loadResponse(String response) {
        try {
            //getting the whole json object from the response
            JSONObject obj = new JSONObject(response);
            //Success sign in
            if(obj.optString("success").equals("true")){

                JSONObject userJson = obj.getJSONObject("user");

                //creating a Customer object
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
                        userJson.getString("address"),
                        userJson.getString("gh_post_address"),
                        userJson.getString("delivery_location"),
                        userJson.getString("agent_code")
                );

                //storing the user in shared preferences
                SharedPrefManager.getInstance(getApplicationContext()).customerLogin(cus);
            }else {
                //Invalid Username or Password
                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
            }
            //Hide Progress Bar
            showProgress(false);

        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 5;
    }

    private boolean isPhoneValid(String phone) {
        //TODO: Replace this with your own logic
        return phone.length() == 10;
    }

    //Function to Set TextViews to fit with Edit Button
    private void setTextViewsWidthToFit(){
//        editTextAgentCode.getLayoutParams().width= (int)Math.round(Utilities.getScreenWidth(this)* 0.6);
        View[] view = {textViewFullName,textViewEmail,textViewDOB,textViewAddress,textViewGender,textViewGhPostAddress,textViewPhone};
        for (View v:view) {
            v.getLayoutParams().width= (int)Math.round(Utilities.getScreenWidth(this)* 0.8);
        }
    }
    }
//Retrofit API Interface
interface RetrofitApiInterfaceChangePassword {
    @POST(URLs.URL_PASSWORD_CHANGE)
    @FormUrlEncoded
    Call<String> changePass(@Field("email") String email, @Field("currentPassword") String currPassword, @Field("newPassword") String newPassword);
}
//Retrofit API Interface
interface RetrofitApiInterfaceSaveDetails {
    @POST(URLs.URL_SAVE_CUSTOMER_DETAILS_CHANGES)
    @FormUrlEncoded
    Call<String> saveChanges(@Field("email") String email, @Field("firstName") String firstName, @Field("lastName") String lastName, @Field("dob") String dOB, @Field("gender") String gender, @Field("phone") String phone, @Field("mAddress") String address, @Field("deliveryLocation") String deliveryLoc, @Field("ghPostAddress") String ghPostAddress);
}
//Retrofit API Interface
interface RetrofitApiInterfaceLoadDeliveryLocationList {
    @POST(URLs.URL_DELIVERY_LOCATION_LIST)
//    @FormUrlEncoded
    Call<List<DeliveryLocation>> loadDeliveryLocList();
}
//Retrofit API Interface
interface RetrofitApiInterfaceUserAccountLoadCustomer {
    @POST(URLs.URL_LOAD_CUSTOMER_DETAILS)
    @FormUrlEncoded
    Call<String> getCustomer(@Field("email") String email);
}