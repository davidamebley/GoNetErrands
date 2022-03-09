package somame.amebleysystems.com.somame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.CountDownTimer;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Util.RetrofitApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class AgentVerifyActivity extends AppCompatActivity {
    public static RetrofitApiInterfaceAgentVerify apiInterfaceAgentVerify;
    public static RetrofitApiInterfaceAgentVerifyResendCode apiInterfaceAgentVerifyResendCode;
    TextView textViewResendCode, textViewTimer;
    AutoCompleteTextView editTextVerifyCode;
    Button buttonVerify;
    Toolbar toolbar;
    Bundle extras;
    String agentCode, agentPhone;
    int agentId;
    LinearLayout linearLayout, linearLayoutTimer;
    ProgressBar progressBar;
    CountDownTimer cTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Retrofit api
        apiInterfaceAgentVerify = RetrofitApiClient.getApiClient().create(RetrofitApiInterfaceAgentVerify.class);
        apiInterfaceAgentVerifyResendCode = RetrofitApiClient.getApiClient().create(RetrofitApiInterfaceAgentVerifyResendCode.class);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_verify);

        //Views Init
        textViewResendCode = findViewById(R.id.textview_agent_verify_resend);
        textViewTimer = findViewById(R.id.textview_agent_verify_timer_remaining);
        editTextVerifyCode = findViewById(R.id.edittext_agent_verify);
        buttonVerify = findViewById(R.id.button_agent_verify_verify);
        linearLayout = findViewById(R.id.layout_agent_verify_edittext);
        progressBar = findViewById(R.id.progress_agent_verify);
        linearLayoutTimer = findViewById(R.id.layout_agent_verify_timer);
        cTimer = null;

        //Button tweaks
        buttonVerify.setText(R.string.verify);

        //      Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_agent_verify);

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

        //Extras
        extras = getIntent().getExtras();
        if (extras!=null){
            agentId= extras.getInt("agentId");
            agentCode = extras.getString("agentCode");
            agentPhone = extras.getString("agentPhone");
        }else if(agentId==0){
            //No user detected
            finish();
        }

        //Resend Code
        textViewResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendCode();
                //Start countdown
                startTimer();
            }
        });

        //Verify User
        buttonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptVerifyUser();
            }
        });

    }


    //Function to start Countdown timer
    void startTimer() {
        cTimer = new CountDownTimer(120000, 1000) {
            public void onTick(long millisUntilFinished) {
//                Update timer text
                textViewTimer.setText(String.valueOf(millisUntilFinished / 1000));
                //Show countdown timer
                linearLayoutTimer.setVisibility(VISIBLE);
                //Disable Resend Link temporary
                textViewResendCode.setEnabled(false);
            }
            public void onFinish() {
                //Show countdown timer
                linearLayoutTimer.setVisibility(GONE);
                //Disable Resend Link temporary
                textViewResendCode.setEnabled(true);
            }
        };
        cTimer.start();
    }
    //cancel timer
    void cancelTimer() {
        if(cTimer!=null)
            cTimer.cancel();
    }

    //Function to Resend code
    private void resendCode(){
        // Show a progress spinner, and kick off a background task to
        // perform the user sign up attempt.
        showProgress(true);

        //            ------------------------RETROFIT----------RESEND CODE--------------------------------
            /*
        Retrofit Network Connection
        * */
        Call<String> call = apiInterfaceAgentVerifyResendCode.resendCode(String.valueOf(agentCode));
        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                Log.e("Resend_Code_TAG", "response: "+new Gson().toJson(response.body()) );
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.e("onSuccess", response.body());
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(response.body());
                            //Success
                            if(obj.optString("success").equals("true")){
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            } else {
                                //Fail
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
                        Toast.makeText(AgentVerifyActivity.this, "Error. Check connection and try again", Toast.LENGTH_SHORT).show();
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

    //Function to Verify User
    private void attemptVerifyUser() {
        // Reset errors.
        editTextVerifyCode.setError(null);

        // Store values for verify attempt.
        final String verificationCode = editTextVerifyCode.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email mAddress.
        if (TextUtils.isEmpty(verificationCode)) {
            editTextVerifyCode.setError(getString(R.string.error_field_required));
            focusView = editTextVerifyCode;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt operation and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            // Show a progress spinner, and kick off a background task to
            // perform the user sign up attempt.
            showProgress(true);
            buttonVerify.setEnabled(false);

            //            ------------------------RETROFIT----------VERIFY--------------------------------
            /*
        Retrofit Network Connection
        * */
            Call<String> call = apiInterfaceAgentVerify.verifyAgent(String.valueOf(agentCode), String.valueOf(verificationCode));
            call.enqueue(new Callback<String>() {

                @Override
                public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                    Log.e("Verify_TAG", "response: "+new Gson().toJson(response.body()) );
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            Log.e("onSuccess", response.body());
                            JSONObject obj = null;
                            try {
                                obj = new JSONObject(response.body());
                                //Success sign in
                                if(obj.optString("success").equals("true")){
                                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(AgentVerifyActivity.this, VerifySuccessActivity.class));
                                    finish();
                                } else {
                                    //Invalid details
                                    Toast.makeText(AgentVerifyActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                                //Hide Progress Bar
                                showProgress(false);
                                buttonVerify.setEnabled(true);

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
                    buttonVerify.setEnabled(true);
                    Toast.makeText(AgentVerifyActivity.this, "Error. Check connection and try again", Toast.LENGTH_SHORT).show();
                }
            });
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

            linearLayout.setVisibility(show ? GONE : VISIBLE);
            linearLayout.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    linearLayout.setVisibility(show ? GONE : VISIBLE);
                }
            });

            progressBar.setVisibility(show ? VISIBLE : GONE);
            progressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressBar.setVisibility(show ? VISIBLE : GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressBar.setVisibility(show ? VISIBLE : GONE);
            linearLayout.setVisibility(show ? GONE : VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
//Retrofit API Interface
interface RetrofitApiInterfaceAgentVerify {
    @POST(URLs.URL_AGENT_VERIFY)
    @FormUrlEncoded
    Call<String> verifyAgent(@Field("agentCode") String agentCode, @Field("verificationCode") String verificationCode);
}
//Retrofit API Interface
interface RetrofitApiInterfaceAgentVerifyResendCode {
    @POST(URLs.URL_AGENT_RESEND_CODE)
    @FormUrlEncoded
    Call<String> resendCode(@Field("agentCode") String agentCode);
}