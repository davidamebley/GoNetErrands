package SharedPref;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import Model.AdvertisingAgent;
import Model.Customer;
import somame.amebleysystems.com.somame.AgentSignInActivity;
import somame.amebleysystems.com.somame.SignInActivity;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.makeRestartActivityTask;

/**
 * Created by Akwasi on 3/15/2018.
 */

//here for this class we are using a singleton pattern

public class SharedPrefManager {

    //the constants
    private static final String SHARED_PREF_CUSTOMER = "sharedprefCustomer";
    private static final String SHARED_PREF_AGENT = "sharedprefAgent";
    private static final String KEY_USERNAME = "keyusername";
    private static final String KEY_AGENTFIRSTNAME = "keyagentfirstname";
    private static final String KEY_AGENTLASTNAME = "keyagentlastname";
    private static final String KEY_AGENTCODE = "keyagentcode";
    private static final String KEY_EMAIL = "keyemail";
    private static final String KEY_AGENTEMAIL = "keyagentemail";
    private static final String KEY_AGENTPHONE = "keyagentphone";
    private static final String KEY_ID = "keyid";
    private static final String KEY_AGENTID = "keyagentid";
    private static final String KEY_CUSTOMER_FIRSTNAME = "firstname";
    private static final String KEY_CUSTOMER_LASTNAME = "lastname";
    private static final String KEY_CUSTOMER_EMAIL = "email";
    private static final String KEY_CUSTOMER_PHONE = "phone";
    private static final String KEY_CUSTOMER_ADDRESS = "address";
    private static final String KEY_CUSTOMER_GH_POST_ADDRESS = "ghpostaddress";
    private static final String KEY_CUSTOMER_GENDER = "gender";
    private static final String KEY_CUSTOMER_DELIVERY_LOC = "deliverylocation";
    private static final String KEY_CUSTOMER_ID = "customerid";
    private static final String KEY_CUSTOMER_CODE = "customercode";
    private static final String KEY_CUSTOMER_DOB = "customerdob";
    private static final String KEY_CUSTOMER_PHOTO = "customerphoto";
    private static final String KEY_CUSTOMER_AGENT_CODE = "customerphoto";

    public static final String DELETE = "Delete";

    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }


    /*public static boolean isConnectedToInternet(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);

        if(connectivityManager != null){
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if(info )
        }
    }*/


    //method to let the user login
    //this method will store the user data in shared preferences
    public void customerLogin(Customer customer) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_CUSTOMER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_CUSTOMER_ID, customer.getCustomerId());
        editor.putString(KEY_CUSTOMER_CODE, customer.getCustomerCode());
        editor.putString(KEY_CUSTOMER_FIRSTNAME, customer.getCustomerFistName());
        editor.putString(KEY_CUSTOMER_LASTNAME, customer.getCustomerLastName());
        editor.putString(KEY_CUSTOMER_GENDER, customer.getCustomerGender());
        editor.putString(KEY_CUSTOMER_PHOTO, customer.getCustomerPhoto());
        editor.putString(KEY_CUSTOMER_DOB, customer.getCustomerDOB());
        editor.putString(KEY_CUSTOMER_EMAIL, customer.getCustomerEmail());
        editor.putString(KEY_CUSTOMER_PHONE, customer.getCustomerPhone());
        editor.putString(KEY_CUSTOMER_ADDRESS, customer.getCustomerAddress());
        editor.putString(KEY_CUSTOMER_GH_POST_ADDRESS, customer.getCustomerGhPostAddress());
        editor.putString(KEY_CUSTOMER_DELIVERY_LOC, customer.getCustomerDeliveryLocation());
        editor.putString(KEY_CUSTOMER_AGENT_CODE, customer.getCustomerAgentCode());
        editor.apply();
    }
    public void setCustomerDetails(Customer customer) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_CUSTOMER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_CUSTOMER_ID, customer.getCustomerId());
        editor.putString(KEY_CUSTOMER_CODE, customer.getCustomerCode());
        editor.putString(KEY_CUSTOMER_FIRSTNAME, customer.getCustomerFistName());
        editor.putString(KEY_CUSTOMER_LASTNAME, customer.getCustomerLastName());
        editor.putString(KEY_CUSTOMER_GENDER, customer.getCustomerGender());
        editor.putString(KEY_CUSTOMER_PHOTO, customer.getCustomerPhoto());
        editor.putString(KEY_CUSTOMER_DOB, customer.getCustomerDOB());
        editor.putString(KEY_CUSTOMER_EMAIL, customer.getCustomerEmail());
        editor.putString(KEY_CUSTOMER_PHONE, customer.getCustomerPhone());
        editor.putString(KEY_CUSTOMER_ADDRESS, customer.getCustomerAddress());
        editor.putString(KEY_CUSTOMER_GH_POST_ADDRESS, customer.getCustomerGhPostAddress());
        editor.putString(KEY_CUSTOMER_DELIVERY_LOC, customer.getCustomerDeliveryLocation());
        editor.putString(KEY_CUSTOMER_AGENT_CODE, customer.getCustomerAgentCode());
        editor.apply();
    }
    //this method will store the agent data in shared preferences
    public void agentLogin(AdvertisingAgent agent) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_AGENT, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_AGENTID, agent.getAgentId());
        editor.putString(KEY_AGENTFIRSTNAME, agent.getFirstName());
        editor.putString(KEY_AGENTLASTNAME, agent.getLastName());
        editor.putString(KEY_AGENTCODE, agent.getAgentCode());
        editor.putString(KEY_AGENTEMAIL, agent.getEmail());
        editor.putString(KEY_AGENTPHONE, agent.getPhone());
        editor.apply();
    }

    //this method will checker whether user is already logged in or not
    public boolean isCustomerLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_CUSTOMER, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_CUSTOMER_EMAIL, null) != null;
    }
    //this method will checker whether agent is already logged in or not
    public boolean isAgentLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_AGENT, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_AGENTEMAIL, null) != null;
    }

    //this method will set the logged in user
    public Customer getCustomer() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_CUSTOMER, Context.MODE_PRIVATE);
        return new Customer(
                sharedPreferences.getInt(KEY_CUSTOMER_ID, -1),
                sharedPreferences.getString(KEY_CUSTOMER_CODE, null),
                sharedPreferences.getString(KEY_CUSTOMER_FIRSTNAME, null),
                sharedPreferences.getString(KEY_CUSTOMER_LASTNAME, null),
                sharedPreferences.getString(KEY_CUSTOMER_GENDER, null),
                sharedPreferences.getString(KEY_CUSTOMER_PHOTO, null),
                sharedPreferences.getString(KEY_CUSTOMER_DOB, null),
                sharedPreferences.getString(KEY_CUSTOMER_EMAIL, null),
                sharedPreferences.getString(KEY_CUSTOMER_PHONE, null),
                sharedPreferences.getString(KEY_CUSTOMER_ADDRESS, null),
                sharedPreferences.getString(KEY_CUSTOMER_GH_POST_ADDRESS, null),
                sharedPreferences.getString(KEY_CUSTOMER_DELIVERY_LOC, null),
                sharedPreferences.getString(KEY_CUSTOMER_AGENT_CODE, null)
        );
    }

    //this method will set the logged in agent
    public AdvertisingAgent getAgent() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_AGENT, Context.MODE_PRIVATE);
        return new AdvertisingAgent(
                sharedPreferences.getInt(KEY_ID, -1),
                sharedPreferences.getString(KEY_AGENTFIRSTNAME, null),
                sharedPreferences.getString(KEY_AGENTLASTNAME, null),
                sharedPreferences.getString(KEY_AGENTPHONE, null),
                sharedPreferences.getString(KEY_AGENTCODE, null),
                sharedPreferences.getString(KEY_AGENTEMAIL, null)
        );
    }

    //this method will logout the user
    public void logoutCustomer() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_CUSTOMER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Intent logoutIntent = new Intent(mCtx, SignInActivity.class);
//        logoutIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        mCtx.startActivity(logoutIntent);
    }
    //this method will logout the agent
    public void logoutAgent() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_AGENT, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Intent logoutIntent = new Intent(mCtx, AgentSignInActivity.class);
        logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        mCtx.startActivity(logoutIntent);
    }
}