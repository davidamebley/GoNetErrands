package somame.amebleysystems.com.somame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Dave on 1/23/2018.
 */

public class BadgeCounterIconConverter {

    public static Drawable convertLayoutToImage(Context mContext, int count, int drawableId) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.layout_badge_basket_icon, null);
        ((ImageView)view.findViewById(R.id.imageView_basket_badge_icon)).setImageResource(drawableId);

        if (count == 0) {
            View counterTextPanel = view.findViewById(R.id.frameLayout_counterValuePanel);
            counterTextPanel.setVisibility(View.GONE);
        } else {
            TextView textView = (TextView) view.findViewById(R.id.textView_badge_count);
            textView.setText("" + count);
        }

        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return new BitmapDrawable(mContext.getResources(), bitmap);
    }
}
