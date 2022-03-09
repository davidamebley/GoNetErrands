package somame.amebleysystems.com.somame;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import Util.NetworkStatus;

public class Splash extends AppCompatActivity {
private static int SPLASH_TIME_OUT = 4000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title bar or actionbar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        TextView txtTitle = findViewById(R.id.splash_title_main);
        TextView txtSubtitle = findViewById(R.id.splash_title_sub1);
        Typeface custom_font;

        //Check for Network
        if(!NetworkStatus.haveNetworkConnection(this)){
            Toast.makeText(this, "Connect to a network to access content.", Toast.LENGTH_LONG).show();
        }

        //SSL Stuff
        updateAndroidSecurityProvider();

        //Setting Font
        custom_font = Typeface.createFromAsset(getAssets(),  "fonts/quicksand.otf");
        txtTitle.setTypeface(custom_font);
        custom_font = Typeface.createFromAsset(getAssets(),  "fonts/nabila.ttf");
        txtSubtitle.setTypeface(custom_font);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainActivityIntent = new Intent(Splash.this, MainActivity.class);
                startActivity(mainActivityIntent);
                finish();
            }
        },SPLASH_TIME_OUT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    //SSL Stuff
    private void updateAndroidSecurityProvider() {
        try {
            ProviderInstaller.installIfNeeded(this);
        } catch (GooglePlayServicesRepairableException e) {
            Log.e("Test321", "PlayServices not installed");
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e("Test321", "Google Play Services not available.");
        }
    }
}
