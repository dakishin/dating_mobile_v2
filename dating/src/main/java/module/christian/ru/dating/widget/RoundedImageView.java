package module.christian.ru.dating.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import module.christian.ru.dating.R;


/**
 * Created by dakishin@gmail.com
 */
public class RoundedImageView extends ImageView {
    private Bitmap windowFrame;

    public RoundedImageView(Context context) {
        super(context);
    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (windowFrame == null) {
            try {
                createWindowFrame();
            } catch (Exception e) {
                windowFrame = null;
            }

        }

        if (windowFrame != null) {
            canvas.drawBitmap(windowFrame, 0, 0, null);
        }


    }


    private void createWindowFrame() {
        windowFrame = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888); // Create a new image we will draw over the map
        Canvas osCanvas = new Canvas(windowFrame); // Create a   canvas to draw onto the new image

        RectF outerRectangle = new RectF(0, 0, getWidth(), getHeight());

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG); // Anti alias allows for smooth corners
        paint.setColor(getResources().getColor(R.color.windowBackground)); // This is the color of your activity background

        osCanvas.drawRect(outerRectangle, paint);

        paint.setColor(Color.TRANSPARENT); // An obvious color to help debugging
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)); // A out B http://en.wikipedia.org/wiki/File:Alpha_compositing.svg
        float centerX = getWidth() / 2;
        float centerY = getHeight() / 2;
        float radius = getWidth() / 2;
        osCanvas.drawCircle(centerX, centerY, radius, paint);
    }

    @Override
    public boolean isInEditMode() {
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        windowFrame = null;
    }

}