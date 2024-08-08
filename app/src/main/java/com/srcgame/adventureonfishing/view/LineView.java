package com.srcgame.adventureonfishing.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.srcgame.adventureonfishing.R;
import com.srcgame.adventureonfishing.enums.FishType;
import com.srcgame.adventureonfishing.model.Fish;

public class LineView extends View {
    private Paint paint;
    private float lineLength;
    private boolean isGrowing, isCatch;
    private Drawable image;

    public Fish caughtFish = null;

    public LineView(Context context) {
        super(context);
        init();
    }

    public LineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void init() {
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(5);
        lineLength = 0;
        image = getContext().getResources().getDrawable(R.drawable.hook);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(0, 0, 0, lineLength, paint);

        image.setBounds(0, (int) lineLength, 25, (int) (lineLength + 58));
        image.draw(canvas);
    }

    public void startGrowing() {
        isGrowing = true;
        post(growRunnable);
    }

    public void stopGrowing() {
        isGrowing = false;
        post(shrinkRunnable);
    }

    public void catchFish(Fish fish) {
        isCatch = true;
        caughtFish = fish;
    }

    public boolean isCatch() {
        return isCatch;
    }

    public boolean isGrowing() {
        return isGrowing;
    }

    private final Runnable growRunnable = new Runnable() {
        @Override
        public void run() {
            if (isGrowing && !isCatch) {
                lineLength += 10;
                if (lineLength > getHeight()) {
                    lineLength = getHeight();
                }
                invalidate();
                postDelayed(this, 50);
            }
        }
    };

    private final Runnable shrinkRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isGrowing && lineLength > 0) {
                lineLength -= 10;
                if (lineLength < 0) {
                    lineLength = 0;
                }
                invalidate();
                postDelayed(this, 50);
            } else if (lineLength == 0 && caughtFish != null) {
                if (caughtFish.getFishType() == FishType.DYNAMITE) caughtFish.die();
                else caughtFish.destroy();
                caughtFish = null;
                isCatch = false;
            }
        }
    };

    public Rect getHookBounds() {
        if (getScaleX() == -1) {
            return new Rect((int) (getX() + getWidth() - 25), (int) (getY() + lineLength),
                    (int) (getX() + getWidth()), (int) (getY() + lineLength + 58));
        } else {
            return new Rect((int) getX(), (int) (getY() + lineLength),
                    (int) (getX() + 25), (int) (getY() + lineLength + 58));
        }
    }
}