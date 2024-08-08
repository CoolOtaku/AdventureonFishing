package com.srcgame.adventureonfishing.model;

import static com.srcgame.adventureonfishing.Game.displayWidth;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.srcgame.adventureonfishing.R;
import com.srcgame.adventureonfishing.view.LineView;

public class Player {
    private final Context context;

    private final ImageView body;
    public final LineView lineView;

    public int lives = 3;

    public Player(Activity context) {
        this.context = context;
        this.body = context.findViewById(R.id.playerImage);
        this.lineView = context.findViewById(R.id.lineView);
        Glide.with(this.context).asGif().load(R.drawable.player).into(new SimpleTarget<GifDrawable>() {
            @Override
            public void onResourceReady(@NonNull GifDrawable resource,
                                        @Nullable Transition<? super GifDrawable> transition) {
                body.setImageDrawable(resource);
                resource.stop();
            }
        });
    }

    public void moveLeft(int speed) {
        move(true, speed);
    }

    public void moveRight(int speed) {
        move(false, speed);
    }

    private void move(boolean isLeft, int speed) {
        if (lineView.isGrowing()) return;

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) body.getLayoutParams();
        if (isLeft) {
            layoutParams.leftMargin -= speed;
            body.setScaleX(1);
            lineView.setScaleX(1);
        } else {
            layoutParams.leftMargin += speed;
            body.setScaleX(-1);
            lineView.setScaleX(-1);
        }
        body.setLayoutParams(layoutParams);

        Drawable drawable = getDrawable();
        if (drawable instanceof GifDrawable) {
            GifDrawable gifDrawable = (GifDrawable) drawable;
            if (!gifDrawable.isRunning()) {
                gifDrawable.start();
            }
        }

        if (body.getX() + body.getWidth() < 0) {
            body.setX(displayWidth);
            lineView.setX(displayWidth);
        }
        else if (body.getX() > displayWidth) {
            body.setX(-body.getWidth());
            lineView.setX(-body.getWidth());
        }
    }

    public Drawable getDrawable() {
        return body.getDrawable();
    }

    public Rect getBounds() {
        int left = (int) body.getX();
        int top = (int) body.getY();
        int right = left + body.getWidth();
        int bottom = top + body.getHeight();
        return new Rect(left, top, right, bottom);
    }

    public void destroy() {
        if (body.getParent() != null) ((ViewGroup) body.getParent()).removeView(body);
        if (lineView.getParent() != null) ((ViewGroup) lineView.getParent()).removeView(lineView);
    }
}