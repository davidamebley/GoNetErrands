package somame.amebleysystems.com.somame;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class VerifyResetCodeActivity extends AppCompatActivity implements View.OnClickListener{
    ProgressBar progressBar;
    TextView txtNext;
    ImageButton buttonPrev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_reset_code);

        progressBar = (ProgressBar) findViewById(R.id.progress_reset_verify);
        buttonPrev = (ImageButton) findViewById(R.id.button_reset_prev);
        txtNext = findViewById(R.id.textview_reset_next);

        buttonPrev.setOnClickListener(this);
        txtNext.setOnClickListener(this);

        //Hide Progress
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_reset_prev:
                //If Previous Clicked
                Intent forgotPassIntent = new Intent(VerifyResetCodeActivity.this, ForgotPasswordActivity.class);
                startActivity(forgotPassIntent);
                break;
            case R.id.textview_reset_next:
                //If Next Clicked
                Intent changePassIntent = new Intent(VerifyResetCodeActivity.this, ChangePasswordActivity.class);
                startActivity(changePassIntent);
                break;
        }
    }
}
