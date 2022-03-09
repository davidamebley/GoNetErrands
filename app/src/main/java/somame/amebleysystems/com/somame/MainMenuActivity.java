package somame.amebleysystems.com.somame;

import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainMenuActivity extends AppCompatActivity {
CircleImageView circleImageView;
NestedScrollView nestedScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        circleImageView = (CircleImageView)findViewById(R.id.imageView_main_menu_user_profile);
        nestedScrollView = findViewById(R.id.nested_scroll_view);



    }

   //round image
//    getCircularBitmapFrom()

    //When Back Key Pressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }


    /*private static Bitmap getCircularBitmapFrom(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }
        float radius = bitmap.getWidth() > bitmap.getHeight() ? ((float) bitmap
                .getHeight()) / 2f : ((float) bitmap.getWidth()) / 2f;
        Bitmap canvasBitmap = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);

        Canvas canvas = new Canvas(canvasBitmap);

        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                radius, paint);

        return canvasBitmap;
    }*/
}
