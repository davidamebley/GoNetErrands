package somame.amebleysystems.com.somame;

import android.content.Intent;
import android.graphics.Color;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import SharedPref.SharedPrefManager;

public class AgentEarningActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ProgressBar progressBar;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_earning);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        //Setting Progress bar
        progressBar = findViewById(R.id.progress_agent_earning_content);

        //if the user is not logged in
        //starting the login activity
        if (!SharedPrefManager.getInstance(this).isAgentLoggedIn()) {
            finish();
            startActivity(new Intent(this, AgentSignInActivity.class));
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar_agent_earning);
        toolbar.setSubtitle(getString(R.string.agent_earning)); //Set subtitle of Toolbar
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setSubtitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
