package somame.amebleysystems.com.somame;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener{
    TextView txtNext;
    ImageButton buttonPrev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        txtNext = (TextView) findViewById(R.id.textview_change_pass_next);
        buttonPrev = (ImageButton) findViewById(R.id.button_change_pass_prev);

        txtNext.setOnClickListener(this);
        buttonPrev.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.textview_change_pass_next:
                // If Next Clicked
                Intent passwordSuccessIntent = new Intent(ChangePasswordActivity.this, PasswordChangeSuccessActivity.class);
                startActivity(passwordSuccessIntent);
                finish();
                break;
            case R.id.button_change_pass_prev:
                // If Previous Clicked
                Intent forgotPasswordIntent = new Intent(ChangePasswordActivity.this, ForgotPasswordActivity.class);
                startActivity(forgotPasswordIntent);
                break;
        }
    }
}
