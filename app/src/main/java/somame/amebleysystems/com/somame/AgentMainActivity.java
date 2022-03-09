package somame.amebleysystems.com.somame;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class AgentMainActivity extends AppCompatActivity {
    Typeface custom_font;
    AppCompatTextView txtTitle, txtSubtitle, txtSubtitle2;
    Button buttonContinue;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_main);

        //Views Init
        txtTitle = findViewById(R.id.agent_main_title_main);
        txtSubtitle = findViewById(R.id.agent_main_title_sub1);
        txtSubtitle2 = findViewById(R.id.agent_main_title_sub2);
        buttonContinue = findViewById(R.id.button_agent_continue);

        //Button tweaks
        buttonContinue.setText(R.string.become_an_agent);

        //      Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_agent_main);

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
        custom_font = Typeface.createFromAsset(getAssets(),  "fonts/fondy_script.ttf");
        txtTitle.setTypeface(custom_font);
        custom_font = Typeface.createFromAsset(getAssets(),  "fonts/quilla.otf");
        txtSubtitle.setTypeface(custom_font);
        custom_font = Typeface.createFromAsset(getAssets(),  "fonts/spectral_bold_italic.ttf");
        txtSubtitle2.setTypeface(custom_font);

        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpIntent = new Intent(AgentMainActivity.this, AgentSignUpActivity.class);
                startActivity(signUpIntent);
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
