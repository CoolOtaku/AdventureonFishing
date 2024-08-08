package com.srcgame.adventureonfishing.model;

import static com.srcgame.adventureonfishing.Game.RANDOM;
import static com.srcgame.adventureonfishing.Game.SOUND;
import static com.srcgame.adventureonfishing.enums.FishStatus.ALIVE;
import static com.srcgame.adventureonfishing.enums.FishStatus.DEAD;
import static com.srcgame.adventureonfishing.enums.FishType.DYNAMITE;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.srcgame.adventureonfishing.Game;
import com.srcgame.adventureonfishing.Main;
import com.srcgame.adventureonfishing.R;
import com.srcgame.adventureonfishing.enums.FishStatus;
import com.srcgame.adventureonfishing.enums.FishType;
import com.srcgame.adventureonfishing.view.LineView;

import java.util.Arrays;
import java.util.Objects;

public class Fish {
    private final Context context;

    private final FishType fishType;
    private FishStatus fishStatus;
    private final ImageView body;

    private boolean isAttached, isDeath;
    private LineView lineView;
    private final Handler handler = new Handler();

    private float[] targetPos = new float[2];

    public Fish(Context context, ViewGroup parent, float x, float y) {
        this.context = context;
        this.targetPos[0] = x;
        this.targetPos[1] = y;

        FishType[] fishTypes = FishType.values();
        this.fishType = fishTypes[RANDOM.nextInt(fishTypes.length)];
        this.fishStatus = ALIVE;

        this.body = new ImageView(context);
        this.body.setX(x);
        this.body.setY(y);
        this.body.setScaleType(ImageView.ScaleType.FIT_XY);

        int imageRes, widthInPx, heightInPx;
        switch (this.fishType) {
            case PUFFER_FISH:
                imageRes = R.drawable.fish_2;
                widthInPx = Main.dpToPx(context, 40);
                heightInPx = Main.dpToPx(context, 30);
                break;
            case CARP_FISH:
                imageRes = R.drawable.fish_3;
                widthInPx = Main.dpToPx(context, 60);
                heightInPx = Main.dpToPx(context, 25);
                break;
            case CLOWN_FISH:
                imageRes = R.drawable.fish_4;
                widthInPx = Main.dpToPx(context, 52);
                heightInPx = Main.dpToPx(context, 28);
                break;
            case GOLD_FISH:
                imageRes = R.drawable.fish_5;
                widthInPx = Main.dpToPx(context, 59);
                heightInPx = Main.dpToPx(context, 30);
                break;
            default:
                imageRes = R.drawable.fish_1;
                widthInPx = Main.dpToPx(context, 98);
                heightInPx = Main.dpToPx(context, 30);
                break;
        }

        Glide.with(context).asGif().load(imageRes).into(this.body);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(widthInPx, heightInPx);
        this.body.setLayoutParams(params);
        parent.addView(this.body);

        int lifeTime = 35000 + RANDOM.nextInt(25000);
        handler.postDelayed(this::die, lifeTime);
    }

    public void move() {
        switch (fishStatus) {
            case ALIVE:
                if (isAttached && lineView != null) {
                    body.setX(lineView.getHookBounds().centerX() - (float) body.getWidth() / 2);
                    body.setY(lineView.getHookBounds().centerY());
                    if (body.getScaleX() == -1) body.setRotation(90);
                    else body.setRotation(-90);
                } else {
                    if ((int) targetPos[0] - 1 <= (int) body.getX() && (int) targetPos[0] + 1 >= (int) body.getX()) {
                        body.setX(targetPos[0]);
                        if (body.getY() < targetPos[1] && body.getScaleX() == 1
                                || body.getY() > targetPos[1] && body.getScaleX() == -1)
                            body.setRotation(90);
                        else if (body.getY() < targetPos[1] && body.getScaleX() == -1
                                || body.getY() > targetPos[1] && body.getScaleX() == 1)
                            body.setRotation(-90);
                    } else if (body.getX() < targetPos[0]) {
                        body.setX(body.getX() + 2);
                        body.setRotation(0);
                        body.setScaleX(1);
                    } else if (body.getX() > targetPos[0]) {
                        body.setX(body.getX() - 2);
                        body.setRotation(0);
                        body.setScaleX(-1);
                    }

                    if (body.getY() < targetPos[1]) body.setY(body.getY() + 1);
                    else if (body.getY() > targetPos[1]) body.setY(body.getY() - 1);
                }
                break;
            case DEAD:
                if (fishType == DYNAMITE) return;
                if (body.getY() < Game.displayHeight + body.getHeight()) body.setY(body.getY() + 1);
                else {
                    destroy();
                }
                break;
        }
    }

    public void die() {
        if (isAttached && fishType != DYNAMITE) return;
        if (isDeath()) return;

        fishStatus = DEAD;
        body.setRotation(0);
        if (fishType == DYNAMITE) {
            SOUND.playSound(R.raw.explosion_sound);
            int oldX = getBounds().centerX();
            int oldY = getBounds().centerY();
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                    Main.dpToPx(context, 200), Main.dpToPx(context, 200));
            body.setLayoutParams(params);
            body.setX(oldX - Main.dpToPx(context, 100));
            body.setY(oldY - Main.dpToPx(context, 100));
            Glide.with(context).asGif().load(R.drawable.explosion).into(new CustomTarget<GifDrawable>() {
                @Override
                public void onResourceReady(@NonNull GifDrawable resource, @Nullable Transition<? super GifDrawable> transition) {
                    resource.setLoopCount(1);
                    body.setImageDrawable(resource);
                    resource.start();

                    resource.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
                        @Override
                        public void onAnimationEnd(Drawable drawable) {
                            destroy();
                        }
                    });
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {
                }
            });
        } else {
            SOUND.playSound(R.raw.drowning_sound);
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                    Main.dpToPx(context, 65), Main.dpToPx(context, 23));
            body.setLayoutParams(params);
            body.setImageResource(R.drawable.fish_skeleton);
        }
    }

    public boolean reachedTarget() {
        return body.getX() <= targetPos[0] && body.getX() + body.getWidth() >= targetPos[0]
                && body.getY() <= targetPos[1] && body.getY() + body.getHeight() >= targetPos[1];
    }

    public void setTargetPos(float[] newPos) {
        targetPos = newPos;
    }

    public void attachToHook(LineView lineView) {
        isAttached = true;
        this.lineView = lineView;
        this.lineView.catchFish(this);
    }

    public boolean isAttached() {
        return isAttached;
    }

    public boolean isDeath() {
        return isDeath;
    }

    public FishType getFishType() {
        return fishType;
    }

    public Rect getBounds() {
        int left = (int) body.getX();
        int top = (int) body.getY();
        int right = left + body.getWidth();
        int bottom = top + body.getHeight();
        return new Rect(left, top, right, bottom);
    }

    public void destroy() {
        handler.removeCallbacks(this::die);
        if (body.getParent() != null) ((ViewGroup) body.getParent()).removeView(body);
        isDeath = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fish fish = (Fish) o;
        return isAttached == fish.isAttached && isDeath == fish.isDeath && Objects.equals(context, fish.context) && fishType == fish.fishType && fishStatus == fish.fishStatus && Objects.equals(body, fish.body) && Objects.equals(lineView, fish.lineView) && Objects.deepEquals(targetPos, fish.targetPos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(context, fishType, fishStatus, body, isAttached, isDeath, lineView, Arrays.hashCode(targetPos));
    }
}