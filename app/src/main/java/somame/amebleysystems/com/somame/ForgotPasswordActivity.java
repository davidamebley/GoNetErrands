package somame.amebleysystems.com.somame;

import android.content.Intent;
import android.graphics.Typeface;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class ForgotPasswordActivity extends AppCompatActivity {
        Spinner forgotPassSpinner;
        String selectedContact;
        FloatingTextButton buttonSendCode;
        EditText editTextContact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        TextView txtTitleForgotPassword = (TextView)findViewById(R.id.forgot_password_title);
        TextView txtSignInInstead = (TextView)findViewById(R.id.textview_forgot_sign_in);
        buttonSendCode = (FloatingTextButton)findViewById(R.id.button_forgot_send_code);
        editTextContact = (EditText) findViewById(R.id.edittext_forgot);
        Typeface custom_font;

        //Setting Font
        custom_font = Typeface.createFromAsset(getAssets(),  "fonts/spectral_bold_italic.ttf");
        txtTitleForgotPassword.setTypeface(custom_font);
        custom_font = Typeface.createFromAsset(getAssets(),  "fonts/spectral_light_italic.ttf");
        txtSignInInstead.setTypeface(custom_font);


        //Spinner Drop Down
        forgotPassSpinner = (Spinner) findViewById(R.id.spinner_forgot_password);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(ForgotPasswordActivity.this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.forgot_password));

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        forgotPassSpinner.setAdapter(spinnerAdapter);


        //On Select Contact Method
        forgotPassSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Get Contact Method
                selectedContact = forgotPassSpinner.getSelectedItem().toString();

                if (selectedContact.equalsIgnoreCase("Phone")){
                    editTextContact.setHint("My Phone Number");
                    editTextContact.setInputType(InputType.TYPE_CLASS_PHONE);
                    editTextContact.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_local_phone_black_24dp,0,0,0);
                }else if(selectedContact.equalsIgnoreCase("Email")){
                    editTextContact.setHint("My Email Address");
                    editTextContact.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    editTextContact.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_email_black_24dp,0,0,0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Sign In
        txtSignInInstead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent SignInIntent = new Intent(ForgotPasswordActivity.this, SignInActivity.class);
                startActivity(SignInIntent);
            }
        });

        //Send code
        buttonSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get Contact Method
                selectedContact = forgotPassSpinner.getSelectedItem().toString();
//                Go to Reset Verification Screen
                Intent ResetVerifyIntent = new Intent(ForgotPasswordActivity.this, VerifyResetCodeActivity.class);
                startActivity(ResetVerifyIntent);
            }
        });
    }
}
