package somame.amebleysystems.com.somame;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import Model.AdvertisingAgent;
import SharedPref.SharedPrefManager;

/**
 * Created by Akwasi on ${9/27/2018}.
 */

public class AgentHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ProgressBar progressBar;
    Toolbar toolbar;
    TextView textViewLastEarning, textViewLatestRef, textViewNoOfRefs, textViewActiveRefs, textViewTotalEarning, textViewAgentName, textViewAgentCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_home);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        textViewLastEarning = findViewById(R.id.textview_agent_home_last_earning);
        textViewLatestRef = findViewById(R.id.textview_agent_home_last_referral);
        textViewNoOfRefs = findViewById(R.id.textview_agent_home_no_of_refs);
        textViewActiveRefs = findViewById(R.id.textview_agent_home_active_refs);
        textViewTotalEarning = findViewById(R.id.textview_agent_home_total_earnings);
        //Setting Progress bar
        progressBar = findViewById(R.id.progress_agent_home_content);

        //if the user is not logged in
        //starting the login activity
        if (!SharedPrefManager.getInstance(this).isAgentLoggedIn()) {
            finish();
            startActivity(new Intent(this, AgentSignInActivity.class));
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar_agent_home_content);
        toolbar.setSubtitle(getString(R.string.agent_home)); //Set subtitle of Toolbar
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


        //Drawer Layout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_agent);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_agent);
        navigationView.setNavigationItemSelectedListener(this);


        //Set Name for Current Agent
        AdvertisingAgent agent = SharedPrefManager.getInstance(this).getAgent();

        View headerView = navigationView.getHeaderView(0);
        textViewAgentName = (TextView) headerView.findViewById(R.id.textView_nav_header_agent_name);
        textViewAgentCode = (TextView) headerView.findViewById(R.id.textView_nav_header_agent_code);
        String agentFullName = agent.getFirstName()+" "+agent.getLastName();
        String agentCode = agent.getAgentCode();
        textViewAgentName.setText(agentFullName);
        textViewAgentCode.setText(getString(R.string.agent_code).concat(": "+agentCode));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_agent);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_agent_earnings) {
            // Handle the menu
            startActivity(new Intent(AgentHomeActivity.this, AgentEarningActivity.class));

        } else if (id == R.id.nav_agent_referrals) {
            //Open Cart
            /*Intent cartIntent = new Intent(HomeActivity.this, CartActivity.class);
            startActivity(cartIntent);*/

        } else if (id == R.id.nav_agent_account) {
            //Opem orders
            /*Intent orderRequestIntent = new Intent(HomeActivity.this, OrderStatus.class);
            startActivity(orderRequestIntent);*/

        } else if (id == R.id.nav_agent_sign_out) {
            //Log user out
            finish();
            SharedPrefManager.getInstance(getApplicationContext()).logoutAgent();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_agent);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
