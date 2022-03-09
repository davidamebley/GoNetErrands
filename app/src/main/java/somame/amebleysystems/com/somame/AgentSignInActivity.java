package somame.amebleysystems.com.somame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Model.AdvertisingAgent;
import Model.Customer;
import SharedPref.SharedPrefManager;
import Util.RetrofitApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class AgentSignInActivity extends AppCompatActivity {
    public static RetrofitApiInterfaceAgentSignIn apiInterfaceAgentSignIn;
    EditText editTextEmailPhone, editTextPassword;
    AppCompatTextView textViewForgotPassword;
    FloatingTextButton buttonSignIn;
    LinearLayout linearLayout;
    ProgressBar progressBar;
    Toolbar toolbar;
    TextView txtSignUpAsAgent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Retrofit api
        apiInterfaceAgentSignIn = RetrofitApiClient.getApiClient().create(RetrofitApiInterfaceAgentSignIn.class);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_sign_in);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        //Is User Logged In Already
        if (SharedPrefManager.getInstance(this).isAgentLoggedIn()) {
            finish();
            startActivity(new Intent(this, AgentHomeActivity.class));
        }

        //Views Init
        textViewForgotPassword = findViewById(R.id.textview_agent_sign_in_forgot_pass);
        editTextEmailPhone = findViewById(R.id.edittext_agent_sign_in_email_phone);
        editTextPassword = findViewById(R.id.edittext_agent_sign_in_password);
        buttonSignIn = findViewById(R.id.button_agent_sign_in_sign_in);
        linearLayout = findViewById(R.id.layout_agent_sign_in_edittext);
        progressBar = findViewById(R.id.progress_agent_sign_in);
        txtSignUpAsAgent = findViewById(R.id.textview_agent_sign_in_sign_up);


        //      Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_agent_sign_in);

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

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSignIn();
            }
        });

        txtSignUpAsAgent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent AgentIntent = new Intent(AgentSignInActivity.this, AgentMainActivity.class);
                startActivity(AgentIntent);
            }
        });
    }

    //Function to Begin Sign up Process
    private void attemptSignIn() {
        // Reset errors.
        editTextEmailPhone.setError(null);
        editTextPassword.setError(null);

        // Store values at the time of the sign up attempt.
        final String username = editTextEmailPhone.getText().toString().trim();
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
        if (TextUtils.isEmpty(username)) {
            editTextEmailPhone.setError(getString(R.string.error_field_required));
            focusView = editTextEmailPhone;
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
            buttonSignIn.setEnabled(false);

            //            ------------------------RETROFIT----------SIGN IN--------------------------------
            /*
             * Creating a String Request
             * */

            //Pass Email and Password fields
            Call<String> call =apiInterfaceAgentSignIn.getAgent(username, password);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, retrofit2.Response<String> response) {

                    Log.e("SignIn_Response: ", response.body());
                    //Toast.makeText()
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            Log.e("onSuccess", response.body());

                            String jsonResponse = response.body();
                            loadResponse(jsonResponse);

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
                    buttonSignIn.setEnabled(true);
                    Toast.makeText(AgentSignInActivity.this, "Error. Check connection and try again", Toast.LENGTH_SHORT).show();
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

                //creating a new agent object
                AdvertisingAgent agent = new AdvertisingAgent(
                        userJson.getInt("agent_id"),
                        userJson.getString("agent_first_name"),
                        userJson.getString("agent_last_name"),
                        userJson.getString("agent_phone"),
                        userJson.getString("agent_code"),
                        userJson.getString("agent_email")
                );
                if (obj.getString("message").equalsIgnoreCase("Login successful")) {
                    //Login successful
                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    //storing the user in shared preferences
                    SharedPrefManager.getInstance(getApplicationContext()).agentLogin(agent);

                    //starting the Home activity after successful login
                    finish();
                    startActivity(new Intent(getApplicationContext(), AgentHomeActivity.class));

                } else{
                    //Account NOT verified
                    //Getting and Sending Details to Verify Activity
                    Intent verifyAgentActivity = new Intent(AgentSignInActivity.this, AgentVerifyActivity.class);
                    verifyAgentActivity.putExtra("agentId", userJson.getInt("agent_id"));
                    verifyAgentActivity.putExtra("agentCode", userJson.getString("agent_code"));
                    verifyAgentActivity.putExtra("agentPhone", userJson.getString("agent_phone"));
                    startActivity(verifyAgentActivity);
                    finish();
                }

            }else {/*if (obj.getBoolean("error"))*/
                //Invalid Username or Password
                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
            }
            //Hide Progress Bar
            showProgress(false);
            buttonSignIn.setEnabled(true);

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
}
//Retrofit API Interface
interface RetrofitApiInterfaceAgentSignIn {
    @POST(URLs.URL_AGENT_SIGNIN)
    @FormUrlEncoded
    Call<String> getAgent(@Field("username") String username, @Field("password") String password);
}