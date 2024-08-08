package com.srcgame.adventureonfishing.model;

import static com.srcgame.adventureonfishing.Game.RANDOM;
import static com.srcgame.adventureonfishing.Game.SOUND;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.srcgame.adventureonfishing.Main;
import com.srcgame.adventureonfishing.R;
import com.srcgame.adventureonfishing.enums.AwardType;
import com.srcgame.adventureonfishing.enums.FishType;

public class Award {
    private final Context context;

    private final ImageView body;
    private AwardType awardType;
    private Player target;
    private boolean isDeath;

    public Award(Context context, ViewGroup parent, Fish fromFish, Player target) {
        this.context = context;
        this.target = target;

        AwardType[] awardTypes = AwardType.values();
        this.awardType = awardTypes[RANDOM.nextInt(awardTypes.length)];

        this.body = new ImageView(context);
        this.body.setX(fromFish.getBounds().centerX());
        this.body.setY(fromFish.getBounds().centerY());
        this.body.setScaleType(ImageView.ScaleType.FIT_XY);

        int imageRes;
        switch (this.awardType) {
            case DIAMOND:
                imageRes = R.drawable.diamond;
                break;
            case RED_DIAMOND:
                imageRes = R.drawable.red_diamond;
                break;
            default:
                imageRes = R.drawable.gold_coins;
                break;
        }

        this.body.setImageResource(imageRes);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                Main.dpToPx(context, 40),
                Main.dpToPx(context, 40));
        this.body.setLayoutParams(params);
        parent.addView(this.body);
        SOUND.playSound(R.raw.award_sound);
    }

    public void moveTowardsTarget() {
        float dx = target.getBounds().centerX() - body.getX();
        float dy = target.getBounds().centerY() - body.getY();
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance < 5) {
            body.setX(target.getBounds().centerX());
            body.setY(target.getBounds().centerY());
            destroy();
        } else {
            float step = 7;
            body.setX(body.getX() + (dx / distance) * step);
            body.setY(body.getY() + (dy / distance) * step);
        }
    }

    public boolean isDeath() {
        return isDeath;
    }

    public AwardType getAwardType() {
        return awardType;
    }

    public void destroy() {
        if (body.getParent() != null) ((ViewGroup) body.getParent()).removeView(body);
        isDeath = true;
    }
}