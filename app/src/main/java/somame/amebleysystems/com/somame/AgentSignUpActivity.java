package somame.amebleysystems.com.somame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import Util.RetrofitApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import ru.dimorinny.floatingtextbutton.FloatingTextButton;

import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;

public class AgentSignUpActivity extends AppCompatActivity {
    public static RetrofitApiInterfaceAgentSignUp apiInterfaceSignUp;
    AutoCompleteTextView editTextFirstName, editTextLastName, editTextEmail, editTextPhone, editTextPhoneConfirm;
    EditText editTextPassword, editTextPasswordConfirm, editTextAddress;
    FloatingTextButton buttonSignUp;
    Button buttonSetDOB;
    TextView txtSignIn, textViewTermsOfService;
    Toolbar toolbar;
    RadioGroup radioGroup;
    ProgressBar progressBar;
    LinearLayout editTextLayout;
    RelativeLayout layoutDOB;
    String selectedGender="", selectedDateofBirth="";
    //More
    private SimpleDateFormat dateFormat;
    private Calendar calendar;
    Date currentDate;
    private TextView dateView;
    private int year, month, day, ageYears;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Retrofit api
        apiInterfaceSignUp = RetrofitApiClient.getApiClient().create(RetrofitApiInterfaceAgentSignUp.class);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_sign_up);

        //View Init
        editTextFirstName = findViewById(R.id.edittext_agent_sign_up_firstname);
        editTextLastName = findViewById(R.id.edittext_agent_sign_up_lastname);
        editTextEmail = findViewById(R.id.edittext_agent_sign_up_email);
        editTextPhone = findViewById(R.id.edittext_agent_sign_up_phone);
//        editTextPhoneConfirm = findViewById(R.id.edittext_agent_sign_up_phone_confirm);
        editTextPassword = findViewById(R.id.edittext_agent_sign_up_password);
        editTextPasswordConfirm = findViewById(R.id.edittext_agent_sign_up_password_confirm);
        editTextAddress = findViewById(R.id.edittext_agent_sign_up_address);
        radioGroup = findViewById(R.id.radio_group_agent_sign_up_gender);
        editTextLayout = findViewById(R.id.layout_agent_sign_up_edittext);
        layoutDOB = findViewById(R.id.layout_agent_sign_up_dob);
        progressBar = findViewById(R.id.progress_agent_sign_up_sign_up);

        txtSignIn = findViewById(R.id.textview_agent_sign_up_sign_in);
        textViewTermsOfService = findViewById(R.id.textview_agent_sign_up_terms2);
        buttonSignUp = findViewById(R.id.button_agent_sign_up_sign_up);

        //      Toolbar
        toolbar = findViewById(R.id.toolbar_agent_sign_up);
        setSupportActionBar(toolbar);

        toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        toolbar.setTitle(R.string.app_name);
        toolbar.setSubtitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();

            }
        });

        //More on Date Picker
        dateView = findViewById(R.id.textView_agent_sign_up_dob);
        calendar = Calendar.getInstance(Locale.US);
        year = calendar.get(Calendar.YEAR);
        currentDate = calendar.getTime();
        dateFormat = new SimpleDateFormat("yyyy/mm/dd", Locale.US);

        month = calendar.get(MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month+1, day);
        //The following two opens the Date Dialog
        layoutDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(v);
            }
        });
        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(v);
            }
        });

        //Sign In Instead
        txtSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AgentSignUpActivity.this,AgentSignInActivity.class));
                finish();
            }
        });

        //Sign Up Click
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Set Gender
                radioGenderClick(v);
                //Function to get age in Years
                ageYears = getAgeYears(selectedDateofBirth, currentDate);
//                Toast.makeText(getApplicationContext(),String.valueOf(ageYears),Toast.LENGTH_SHORT).show();
                attemptSignUp();
            }
        });

        //Terms of Service Click
        textViewTermsOfService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTermsAndConditionsDialog();
            }
        });
    }

    //Function to Begin Sign up Process
    private void attemptSignUp() {
        // Reset errors.
        editTextFirstName.setError(null);
        editTextLastName.setError(null);
        editTextPhone.setError(null);
        editTextEmail.setError(null);
        editTextPassword.setError(null);
        editTextPasswordConfirm.setError(null);
        editTextAddress.setError(null);
        editTextAddress.setError(null);

        // Store values at the time of the sign up attempt.
        final String email = editTextEmail.getText().toString().trim();
        final String password1 = editTextPassword.getText().toString().trim();
        final String password2 = editTextPasswordConfirm.getText().toString().trim();
        final String firstName = editTextFirstName.getText().toString().trim();
        final String lastName = editTextLastName.getText().toString().trim();
        final String phone = editTextPhone.getText().toString().trim();
        final String address = editTextAddress.getText().toString().trim();
        final String gender = selectedGender;
        final String agentDOB = selectedDateofBirth;

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password1) && !TextUtils.isEmpty(password2) && !isPasswordValid(password1)) {
            editTextPassword.setError(getString(R.string.error_invalid_password));
            focusView = editTextPassword;
            cancel = true;
        }else  if (TextUtils.isEmpty(password1)) {
            editTextPassword.setError(getString(R.string.error_field_required));
            focusView = editTextPassword;
            cancel = true;
        }else  if (!TextUtils.isEmpty(password1) && !password1.equals(password2)) {
            editTextPasswordConfirm.setError(getString(R.string.error_mismatch_password));
            focusView = editTextPasswordConfirm;
            cancel = true;
        }else  if (TextUtils.isEmpty(password2)) {
            editTextPasswordConfirm.setError(getString(R.string.error_field_required));
            focusView = editTextPasswordConfirm;
            cancel = true;
        }

        //Check for a valid DOB
        if (ageYears < 1){
            Toast.makeText(AgentSignUpActivity.this, getString(R.string.error_age_invalid), Toast.LENGTH_LONG).show();
            dateView.setError(getString(R.string.error_age_invalid));
            focusView = dateView;
            cancel = true;
        }else if (ageYears > 0 && ageYears < 18){
            Toast.makeText(AgentSignUpActivity.this, getString(R.string.error_age_below), Toast.LENGTH_LONG).show();
            dateView.setError(getString(R.string.error_age_below));
            focusView = dateView;
            cancel = true;
        }


        // Check for a valid email mAddress.

        isValidEmail(editTextEmail.getText());

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError(getString(R.string.error_field_required));
            focusView = editTextEmail;
            cancel = true;
        }
        else if (!isValidEmail(email)) {
            editTextEmail.setError(getString(R.string.error_invalid_email));
            focusView = editTextEmail;
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

        // Check Empty mAddress
        if (TextUtils.isEmpty(address)) {
            editTextAddress.setError(getString(R.string.error_field_required));
            focusView = editTextAddress;
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
            Call<String> call = apiInterfaceSignUp.signUpAgent(String.valueOf(firstName), String.valueOf(lastName), String.valueOf(email), String.valueOf(address),String.valueOf(gender),String.valueOf(agentDOB), String.valueOf(phone), String.valueOf(password1), String.valueOf(password2));
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
                                    startActivity(new Intent(AgentSignUpActivity.this, AgentVerifyActivity.class));
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
                    Toast.makeText(AgentSignUpActivity.this, "Error. Check connection and try again", Toast.LENGTH_SHORT).show();               }
            });

        }

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


    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 5;
    }

    private boolean isPhoneValid(String phone) {
        //TODO: Replace this with your own logic
        return phone.length() == 10;
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
                }
            };

    private void showDate(int year, int month, int day) {
        dateView.setText(new StringBuilder().append(year).append("-")
                .append(month).append("-").append(day));
        selectedDateofBirth = dateView.getText().toString();
        //Remove Error from Date View
        dateView.setError(null);
//        buttonSetDOB.setError(null);
        //Function to get age in Years
        ageYears = getAgeYears(selectedDateofBirth, currentDate);
//        Toast.makeText(getApplicationContext(),String.valueOf(ageYears),Toast.LENGTH_SHORT).show();
    }


    public void radioGenderClick(View view) {
        int radioButtonId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(radioButtonId);
        selectedGender = radioButton.getText().toString();
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
        dateView.setText(new StringBuilder().append(year).append("-")
                .append(month).append("-").append(day));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
//Retrofit API Interface
interface RetrofitApiInterfaceAgentSignUp {
    @POST(URLs.URL_AGENT_SIGNUP)
    @FormUrlEncoded
    Call<String> signUpAgent(@Field("firstName") String firstName, @Field("lastName") String lastName, @Field("email") String email1, @Field("address") String address, @Field("gender") String gender, @Field("dateOfBirth") String agentDOB, @Field("phone") String phone, @Field("password1") String password1, @Field("password2") String password2);
}