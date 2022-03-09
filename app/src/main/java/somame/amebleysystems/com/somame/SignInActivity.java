package somame.amebleysystems.com.somame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import Model.Customer;
import SharedPref.SharedPrefManager;
import Util.RetrofitApiClient;
import Util.TLSSocketFactory;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class SignInActivity extends AppCompatActivity {
    public static RetrofitApiInterfaceSignIn apiInterface;
    FloatingTextButton buttonSignIn;
    TextView textViewSignInAsAgent, txtForgotPass, txtSignUpInstead;
    EditText editTextEmail, editTextPassword;
    LinearLayout linearLayout, linearLayout2;
    ProgressBar progressBar;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Retrofit api
        apiInterface = RetrofitApiClient.getApiClient().create(RetrofitApiInterfaceSignIn.class);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        //Is User Logged In Already
        if (SharedPrefManager.getInstance(this).isCustomerLoggedIn()) {
            finish();
            startActivity(new Intent(this, MainCategoryActivity.class));
        }

        AppCompatTextView txtTitleSignIn = findViewById(R.id.textView_sign_in_title);
        txtSignUpInstead = (TextView) findViewById(R.id.textview_sign_up_instead);
        txtForgotPass = (TextView) findViewById(R.id.textview_forgot_password);
        editTextEmail = findViewById(R.id.edittext_sign_in_email);
        editTextPassword = findViewById(R.id.edittext_sign_in_password);
        textViewSignInAsAgent = (TextView) findViewById(R.id.textview_sign_in_sign_in_as_agent);
        buttonSignIn = findViewById(R.id.button_sign_in_sign_in);
        Typeface custom_font;
        linearLayout = findViewById(R.id.layout_sign_in_edittext);
        linearLayout2 = findViewById(R.id.layout_sign_in_buttons);
        progressBar = findViewById(R.id.progress_sign_in);

        //      Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_sign_in);

//        toolbar.setSubtitle(CurrentRestaurantClass.restaurantName);
        toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        toolbar.setSubtitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();

            }
        });

        //Setting Font
        custom_font = Typeface.createFromAsset(getAssets(),  "fonts/spectral_bold_italic.ttf");
        txtTitleSignIn.setTypeface(custom_font);
        custom_font = Typeface.createFromAsset(getAssets(),  "fonts/spectral_light_italic.ttf");
//        txtSignUpInstead.setTypeface(custom_font);

        //Sign up link clicked
        txtSignUpInstead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent SignUpIntent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(SignUpIntent);
                finish();
            }
        });

        //Forgot password link clicked
        txtForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ForgotIntent = new Intent(SignInActivity.this, ForgotPasswordActivity.class);
                startActivity(ForgotIntent);
                finish();
            }
        });

        //Agent link clicked
        textViewSignInAsAgent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this,AgentSignInActivity.class));
            }
        });

        //Sign In Click
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSignIn();
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    //SSL Stuff
    private void updateAndroidSecurityProvider() {
        try {
            ProviderInstaller.installIfNeeded(this);
        } catch (GooglePlayServicesRepairableException e) {
            Log.e("Test321", "PlayServices not installed");
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e("Test321", "Google Play Services not available.");
        }
    }


    //Function to Begin Sign in Process
    private void attemptSignIn() {
        // Reset errors.
        editTextEmail.setError(null);
        editTextPassword.setError(null);

        // Store values at the time of the sign up attempt.
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError(getString(R.string.error_field_required));
            focusView = editTextPassword;
            cancel = true;
        }


        // Check for a valid email mAddress.
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError(getString(R.string.error_field_required));
            focusView = editTextEmail;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt sign up and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user sign up attempt.
            //Hide some views first
            hideViews();

//            ------------------------RETROFIT----------SIGN IN--------------------------------
            /*
        * Creating a String Request
        * */

            //Pass Email and Password fields
            Call<String> call =apiInterface.getCustomer(email, password);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    Log.e("Response: ", response.body());
                    //Toast.makeText()
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            Log.e("onSuccess", response.body());

                            String jsonResponse = response.body();
                            loadResponse(jsonResponse);

                        } else {
                            Log.e("onEmptyResponse", "Returned empty response");
                            //Bring back hidden views
                            showViews();
                        }
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    //display error message
                    Log.d("retrofit_log", "Failed! Error = " + t.getMessage());
                    //Bring back hidden views
                    showViews();
                    Toast.makeText(SignInActivity.this, "Error. Check connection and try again", Toast.LENGTH_SHORT).show();
                }

            });
        }
    }

    //This function handles the received Retrofit response
    private void loadResponse(String response) {
        try {
            //getting the whole json object from the response
            JSONObject obj = new JSONObject(response);
            //Success sign in
            if(obj.optString("success").equals("true")){

                JSONObject userJson = obj.getJSONObject("user");

                    //creating a new Customer object
                    Customer customer = new Customer(
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

                Toast.makeText(SignInActivity.this, obj.optString("message")+"", Toast.LENGTH_LONG).show();
                //storing the user in shared preferences
                SharedPrefManager.getInstance(getApplicationContext()).customerLogin(customer);
                //starting the Next activity after successful login
                finish();
                startActivity(new Intent(getApplicationContext(), MainCategoryActivity.class));

            }else {
                //Error sign in
                //Bring back hidden views
                showViews();
                Toast.makeText(SignInActivity.this, obj.optString("message")+"", Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Validate Email in a special way
    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
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

    private void showViews(){
        linearLayout.setVisibility(View.VISIBLE);
        linearLayout2.setVisibility(View.VISIBLE);
        showProgress(false);
    }

    private void hideViews(){
        linearLayout.setVisibility(View.GONE);
        linearLayout2.setVisibility(View.GONE);
        showProgress(true);
    }
}

//Retrofit API Interface
interface RetrofitApiInterfaceSignIn {
    @POST(URLs.URL_SIGNIN)
    @FormUrlEncoded
    Call<String> getCustomer(@Field("email") String username, @Field("password") String password);
}