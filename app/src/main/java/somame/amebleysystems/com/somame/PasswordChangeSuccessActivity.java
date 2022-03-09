package somame.amebleysystems.com.somame;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class PasswordChangeSuccessActivity extends AppCompatActivity implements View.OnClickListener{
    TextView txtNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change_success);

        txtNext = findViewById(R.id.textview_pass_success_next);

        txtNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.textview_pass_success_next:
                //If Next Clicked
                Intent signInIntent = new Intent(PasswordChangeSuccessActivity.this, SignInActivity.class);
                startActivity(signInIntent);
                finish();
                break;

        }
    }
}
