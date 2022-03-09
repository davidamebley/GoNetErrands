package somame.amebleysystems.com.somame;

import android.content.Intent;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class VerifySuccessActivity extends AppCompatActivity {
    TextView textViewSignIn;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_success);

        //Views Init
        textViewSignIn = findViewById(R.id.textview_agent_verify_success_sign_in);

        //      Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_agent_verify_success);

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

        //Sign In
        textViewSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VerifySuccessActivity.this,AgentSignInActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
