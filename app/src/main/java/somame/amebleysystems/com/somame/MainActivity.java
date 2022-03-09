package somame.amebleysystems.com.somame;

import android.content.Intent;
import android.graphics.Typeface;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatTextView;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class MainActivity extends AppCompatActivity {
    LinearLayout layoutTitle, layoutContinue, layoutSubtitle2;
//    FloatingTextButton buttonContinue;
    Button buttonContinue;
    Animation upToDown, downToTop, leftToRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonContinue = findViewById(R.id.continue_button1);
        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent SignInIntent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(SignInIntent);
                MainActivity.this.finish();
            }
        });

        //Set Animation
        layoutTitle = (LinearLayout) findViewById(R.id.layout_main_title);
        layoutContinue = (LinearLayout) findViewById(R.id.layout_main_button);
        layoutSubtitle2 = (LinearLayout) findViewById(R.id.layout_main_subtitle2);

        upToDown = AnimationUtils.loadAnimation(this, R.anim.up_to_down);
        layoutTitle.setAnimation(upToDown);
        downToTop = AnimationUtils.loadAnimation(this, R.anim.down_to_top);
        layoutContinue.setAnimation(downToTop);
        leftToRight=AnimationUtils.loadAnimation(this, R.anim.left_to_right);
        layoutSubtitle2.setAnimation(leftToRight);

        AppCompatTextView txtTitle = findViewById(R.id.main_title_main);
        AppCompatTextView txtSubtitle = findViewById(R.id.main_title_sub1);
        AppCompatTextView txtSubtitle2 = findViewById(R.id.main_title_sub2);
        Typeface custom_font;

        //Setting Font
        custom_font = Typeface.createFromAsset(getAssets(),  "fonts/fondy_script.ttf");
        txtTitle.setTypeface(custom_font);
        custom_font = Typeface.createFromAsset(getAssets(),  "fonts/quilla.otf");
        txtSubtitle.setTypeface(custom_font);
        custom_font = Typeface.createFromAsset(getAssets(),  "fonts/spectral_bold_italic.ttf");
        txtSubtitle2.setTypeface(custom_font);
    }
}
