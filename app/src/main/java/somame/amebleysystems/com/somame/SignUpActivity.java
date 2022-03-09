package somame.amebleysystems.com.somame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import Util.RetrofitApiClient2;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class SignUpActivity extends AppCompatActivity{
    public static RetrofitApiInterfaceSignUp apiInterfaceSignUp;
    FloatingTextButton buttonSignUp;
    EditText editTextFirstName, editTextLastName, editTextEmail1, editTextEmail2, editTextPhone, editTextPassword1, editTextPassword2, editTextAgentCode;
    AppCompatTextView txtTitleSignUp;
    TextView txtSignInInstead, txtTermsOfService;
    LinearLayout editTextLayout;
    ProgressBar progressBar;
    Typeface custom_font;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Retrofit api
        apiInterfaceSignUp = RetrofitApiClient.getApiClient().create(RetrofitApiInterfaceSignUp.class);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //View Init
        txtTitleSignUp = findViewById(R.id.signup_for_somame);
        editTextFirstName = findViewById(R.id.edittext_sign_up_firstname);
        editTextLastName = findViewById(R.id.edittext_sign_up_lastname);
        editTextEmail1 = findViewById(R.id.edittext_sign_up_email);
        editTextEmail2 = findViewById(R.id.edittext_sign_up_email_confirm);
        editTextPhone = findViewById(R.id.edittext_sign_up_phone);
        editTextPassword1 = findViewById(R.id.edittext_sign_up_password);
        editTextPassword2 = findViewById(R.id.edittext_sign_up_password_confirm);
        editTextLayout = findViewById(R.id.layout_sign_up_edittext);
        editTextAgentCode = findViewById(R.id.edittext_sign_up_agent_code);
        progressBar = findViewById(R.id.progress_sign_up);

        txtSignInInstead = findViewById(R.id.textview_sign_up_sign_in);
        txtTermsOfService = findViewById(R.id.textview_sign_up_terms2);
        buttonSignUp = findViewById(R.id.button_sign_up_sign_up);
        //Prepare the Terms_Conditions TextReader Class
        ReadTextFromServer.executeReadTextFile();

        //      Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_sign_up);

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
        custom_font = Typeface.createFromAsset(getAssets(), "fonts/spectral_bold_italic.ttf");
        txtTitleSignUp.setTypeface(custom_font);
        custom_font = Typeface.createFromAsset(getAssets(), "fonts/spectral_light_italic.ttf");
//        txtSignInInstead.setTypeface(custom_font);

        //Some Views Tweaks
        editTextAgentCode.getLayoutParams().width= (int)Math.round(Utilities.getScreenWidth(this)* 0.6);

        txtSignInInstead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent SignInIntent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(SignInIntent);
                finish();
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSignUp();
            }
        });

        //Terms_Conditions Click
        txtTermsOfService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTermsAndConditionsDialog();
            }
        });

    }


    //Create Terms_Conditions AlertDialog TextView
    private void showTermsAndConditionsDialog() {
//      Use layout created in xml and inflate as our AlertDialog EditText
        View view = LayoutInflater.from(this).inflate(R.layout.layout_terms_conditions_dialog, null);

        final TextView textViewTermsConditions = view.findViewById(R.id.dialog_textView_terms_conditions);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Terms and Conditions");
        builder.setView(view);

        builder.setPositiveButton("I understand", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });

        //Get Text and Set to View
        textViewTermsConditions.setText(ReadTextFromServer.getTextContent());
        final AlertDialog dialog = builder.create();
        dialog.show();
        //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });

    }


    private void attemptSignUp() {

        // Reset errors.
        editTextFirstName.setError(null);
        editTextLastName.setError(null);
        editTextPhone.setError(null);
        editTextEmail1.setError(null);
        editTextEmail2.setError(null);
        editTextPassword1.setError(null);
        editTextPassword2.setError(null);

        // Store values at the time of the sign up attempt.
        final String email1 = editTextEmail1.getText().toString().trim();
        final String email2 = editTextEmail2.getText().toString().trim();
        final String password1 = editTextPassword1.getText().toString().trim();
        final String password2 = editTextPassword2.getText().toString().trim();
        final String firstName = editTextFirstName.getText().toString().trim();
        final String lastName = editTextLastName.getText().toString().trim();
        final String phone = editTextPhone.getText().toString().trim();
        final String agentCode = editTextAgentCode.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password1) && !TextUtils.isEmpty(password2) && !isPasswordValid(password1)) {
            editTextPassword1.setError(getString(R.string.error_invalid_password));
            focusView = editTextPassword1;
            cancel = true;
        }else  if (TextUtils.isEmpty(password1)) {
            editTextPassword1.setError(getString(R.string.error_field_required));
            focusView = editTextPassword1;
            cancel = true;
        }else  if (!TextUtils.isEmpty(password1) && !password1.equals(password2)) {
            editTextPassword2.setError(getString(R.string.error_mismatch_password));
            focusView = editTextPassword2;
            cancel = true;
        }else  if (TextUtils.isEmpty(password2)) {
            editTextPassword2.setError(getString(R.string.error_field_required));
            focusView = editTextPassword2;
            cancel = true;
        }

        // Check for a valid email mAddress.

        isValidEmail(editTextEmail1.getText());

        if (TextUtils.isEmpty(email1)) {
            editTextEmail1.setError(getString(R.string.error_field_required));
            focusView = editTextEmail1;
            cancel = true;
        }else if (!TextUtils.isEmpty(email1) && TextUtils.isEmpty(email2)) {
            editTextEmail2.setError(getString(R.string.error_field_required));
            focusView = editTextEmail2;
            cancel = true;
        }else if (!TextUtils.isEmpty(email1) && !TextUtils.isEmpty(email2) && !email1.equals(email2)) {
            editTextEmail2.setError(getString(R.string.error_mismatch_email));
            focusView = editTextEmail2;
            cancel = true;
        } /*else if (!isEmailValid(email1)) {
            editTextEmail1.setError(getString(R.string.error_invalid_email));
            focusView = editTextEmail1;
            cancel = true;
        }*/
        else if (!isValidEmail(email1)) {
            editTextEmail1.setError(getString(R.string.error_invalid_email));
            focusView = editTextEmail1;
            cancel = true;
        }

        // Check for a valid name.
        if (TextUtils.isEmpty(firstName)) {
            editTextFirstName.setError(getString(R.string.error_field_required));
            focusView = editTextFirstName;
            cancel = true;
        }
        // Check for a valid last name.
        if (TextUtils.isEmpty(lastName)) {
            editTextLastName.setError(getString(R.string.error_field_required));
            focusView = editTextLastName;
            cancel = true;
        }
        // Check for a valid Phone.
        if (TextUtils.isEmpty(phone)) {
            editTextPhone.setError(getString(R.string.error_field_required));
            focusView = editTextPhone;
            cancel = true;
        }else if (!isPhoneValid(phone)) {
            editTextPhone.setError(getString(R.string.error_invalid_phone));
            focusView = editTextPhone;
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
            buttonSignUp.setEnabled(false);
//            ------------------------RETROFIT----------SIGN UP--------------------------------
            /*
        Retrofit Network Connection
        * */
            Call<String> call = apiInterfaceSignUp.signUpUser(String.valueOf(firstName), String.valueOf(lastName), String.valueOf(email1), String.valueOf(email2), String.valueOf(phone), String.valueOf(password1), String.valueOf(password2), String.valueOf(agentCode));
            call.enqueue(new Callback<String>() {

                @Override
                public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                    Log.e("SignUp_TAG", "response: "+new Gson().toJson(response.body()) );
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            Log.e("onSuccess", response.body());
                            JSONObject obj = null;
                            try {
                                obj = new JSONObject(response.body());
                                //Success sign in
                                if(obj.optString("success").equals("true")){
                                    resetFields(editTextLayout);
                                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                                //Hide Progress Bar
                                showProgress(false);
                                buttonSignUp.setEnabled(true);

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
                    buttonSignUp.setEnabled(true);
                    Toast.makeText(SignUpActivity.this, "Error. Check connection and try again", Toast.LENGTH_SHORT).show();
                }
            });
        }
}

    //Function to reset Controls
    private void resetFields(ViewGroup group) {
        for (int i = 0, count = group.getChildCount(); i < count; ++i) {
            View view = group.getChildAt(i);
            if (view instanceof EditText) {
                ((EditText)view).setText("");
            }

            if(view instanceof ViewGroup && (((ViewGroup)view).getChildCount() > 0))
                resetFields((ViewGroup)view);
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

            editTextLayout.setVisibility(show ? View.GONE : View.VISIBLE);
            editTextLayout.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    editTextLayout.setVisibility(show ? View.GONE : View.VISIBLE);
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
            editTextLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 5;
    }

    private boolean isPhoneValid(String phone) {
        //TODO: Replace this with your own logic
        return phone.length() == 10;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
//Retrofit API Interface
interface RetrofitApiInterfaceSignUp {
    @POST(URLs.URL_SIGNUP)
    @FormUrlEncoded
    Call<String> signUpUser(@Field("firstName") String firstName, @Field("lastName") String lastName, @Field("email1") String email1, @Field("email2") String email2, @Field("phone") String phone, @Field("password1") String password1, @Field("password2") String password2, @Field("agentCode") String agentCode);
}