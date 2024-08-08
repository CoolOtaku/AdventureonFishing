package com.srcgame.adventureonfishing;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;

import com.bumptech.glide.Glide;
import com.srcgame.adventureonfishing.enums.FishType;
import com.srcgame.adventureonfishing.model.Award;
import com.srcgame.adventureonfishing.model.Fish;
import com.srcgame.adventureonfishing.model.Player;

import java.util.LinkedList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class Game {
    private final Context context;
    public static Random RANDOM = new Random();

    public ConstraintLayout parent;
    public Guideline guideline;
    public ImageView mapBackgroundImage, mapWaterAnimFrame;
    public LinearLayout heartsContainer, playAgainContainer;
    public TextView textGoldCoin, textDiamond, textRedDiamond;

    private final LinkedList<Fish> fishes = new LinkedList<>();
    private final LinkedList<Award> awards = new LinkedList<>();
    public Player player;
    @SuppressLint("StaticFieldLeak")
    public static SoundManager SOUND;
    private long lastFishAddedTime;

    public boolean isRunning;
    public static int displayWidth, displayHeight;
    public int locationImage;
    public double locationWaterHeight, locationPlayerMargin;
    private int countPufferFish, countCarpFish, countClownFishCount, countGoldFishCount;
    private int countCoins, countDiamonds, countRedDiamonds;

    private final Timer timer = new Timer();
    private final Handler handler = new Handler();
    private final Runnable runGame = new Runnable() {
        public void run() {
            if (!isRunning) return;

            Rect hookBounds = player.lineView.getHookBounds();

            LinkedList<Fish> toRemove1 = new LinkedList<>();
            synchronized (fishes) {
                for (Fish fish : fishes) {
                    if (!player.lineView.isCatch() && !fish.isAttached() && Rect.intersects(hookBounds, fish.getBounds())) {
                        fish.attachToHook(player.lineView);
                        player.lineView.stopGrowing();
                        updateStatisticFish(fish);

                        if (RANDOM.nextInt(100) < 35 && fish.getFishType() != FishType.DYNAMITE)
                            addAward(fish);
                        else if (RANDOM.nextInt(100) < 50 && fish.getFishType() == FishType.PUFFER_FISH) {
                            player.lives--;
                            SOUND.playSound(R.raw.ouch_sound);
                        } else if (RANDOM.nextInt(100) < 25 && fish.getFishType() != FishType.PUFFER_FISH
                                && fish.getFishType() != FishType.DYNAMITE && player.lives < 3) {
                            player.lives++;
                            SOUND.playSound(R.raw.award_sound);
                        }
                    }

                    fish.move();

                    if (!fish.isAttached() && fish.reachedTarget())
                        fish.setTargetPos(getRandomPosition());
                    if (fish.isDeath()) {
                        toRemove1.add(fish);
                        if (fish.getFishType() == FishType.DYNAMITE) {
                            if (Rect.intersects(fish.getBounds(), player.getBounds()))
                                player.lives--;
                            for (Fish fish2 : fishes) {
                                if (!fish2.equals(fish) && !fish2.isDeath()
                                        && Rect.intersects(fish.getBounds(), fish2.getBounds()))
                                    fish2.die();
                            }
                        }
                    }
                }

                fishes.removeAll(toRemove1);
            }
            toRemove1.clear();

            LinkedList<Award> toRemove2 = new LinkedList<>();
            synchronized (awards) {
                for (Award award : awards) {
                    award.moveTowardsTarget();

                    if (award.isDeath()) {
                        updateStatisticAward(award);
                        toRemove2.add(award);
                    }
                }

                awards.removeAll(toRemove2);
            }
            toRemove2.clear();

            if (System.currentTimeMillis() - lastFishAddedTime >= 14000) {
                lastFishAddedTime = System.currentTimeMillis();
                addFish();
            }

            if (player.lives <= 0) stopGame();

            for (int i = 0; i < heartsContainer.getChildCount(); i++) {
                if (i < player.lives) heartsContainer.getChildAt(i).setAlpha(1.0F);
                else heartsContainer.getChildAt(i).setAlpha(0.2F);
            }
            textGoldCoin.setText(String.valueOf(countCoins));
            textDiamond.setText(String.valueOf(countDiamonds));
            textRedDiamond.setText(String.valueOf(countRedDiamonds));
        }
    };

    public Game(Activity context) {
        this.context = context;

        parent = context.findViewById(R.id.gameContainer);
        mapBackgroundImage = context.findViewById(R.id.mapBackgroundImage);
        guideline = context.findViewById(R.id.guideline);
        mapWaterAnimFrame = context.findViewById(R.id.mapWaterAnimFrame);
        heartsContainer = context.findViewById(R.id.heartsContainer);
        playAgainContainer = context.findViewById(R.id.playAgainContainer);
        textGoldCoin = context.findViewById(R.id.textGoldCoin);
        textDiamond = context.findViewById(R.id.textDiamond);
        textRedDiamond = context.findViewById(R.id.textRedDiamond);

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        displayWidth = displayMetrics.widthPixels;
        displayHeight = displayMetrics.heightPixels;

        locationImage = context.getIntent().getIntExtra("locationImage", R.drawable.map_1);
        locationWaterHeight = context.getIntent().getDoubleExtra("locationWaterHeight", 0.63);
        locationPlayerMargin = context.getIntent().getDoubleExtra("locationPlayerMargin", 0.45);

        mapBackgroundImage.setImageResource(locationImage);
        guideline.setGuidelinePercent((float) locationPlayerMargin);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mapWaterAnimFrame.getLayoutParams();
        layoutParams.matchConstraintPercentHeight = (float) locationWaterHeight;
        mapWaterAnimFrame.setLayoutParams(layoutParams);
        Glide.with(this.context).asGif().load(R.drawable.water).into(mapWaterAnimFrame);

        player = new Player(context);
        SOUND = new SoundManager(context);
        SOUND.loadSound(R.raw.explosion_sound);
        SOUND.loadSound(R.raw.drowning_sound);
        SOUND.loadSound(R.raw.award_sound);
        SOUND.loadSound(R.raw.ouch_sound);

        isRunning = false;
    }

    public void startGame() {
        if (!isRunning) {
            isRunning = true;
            lastFishAddedTime = System.currentTimeMillis();

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(runGame);
                }
            }, 0, 30);

            addFish();
        }
    }

    public void stopGame() {
        if (isRunning)
            Main.saveGameResult(countPufferFish, countCarpFish, countClownFishCount, countGoldFishCount,
                    countCoins, countDiamonds, countRedDiamonds, locationImage);
        isRunning = false;
        timer.cancel();
        timer.purge();
        handler.removeCallbacks(runGame);
        for (Fish fish : fishes) fish.destroy();
        for (Award award : awards) award.destroy();
        fishes.clear();
        awards.clear();
        player.destroy();
        SOUND.release();
        if (player.lives <= 0) playAgainContainer.setVisibility(View.VISIBLE);
    }

    public void addFish() {
        float[] pos = getRandomPosition();
        fishes.add(new Fish(context, parent, pos[0], pos[1]));
    }

    public void addAward(Fish fish) {
        awards.add(new Award(context, parent, fish, player));
    }

    private void updateStatisticFish(Fish fish) {
        switch (fish.getFishType()) {
            case PUFFER_FISH:
                countPufferFish++;
                break;
            case CARP_FISH:
                countCarpFish++;
                break;
            case CLOWN_FISH:
                countClownFishCount++;
                break;
            case GOLD_FISH:
                countGoldFishCount++;
                break;
        }
    }

    private void updateStatisticAward(Award award) {
        switch (award.getAwardType()) {
            case GOLD_COIN:
                countCoins++;
                break;
            case DIAMOND:
                countDiamonds++;
                break;
            case RED_DIAMOND:
                countRedDiamonds++;
                break;
        }
    }

    private float[] getRandomPosition() {
        float[] res = new float[2];
        res[0] = mapWaterAnimFrame.getX() + RANDOM.nextInt(mapWaterAnimFrame.getWidth());
        res[1] = mapWaterAnimFrame.getY() + RANDOM.nextInt(mapWaterAnimFrame.getHeight());
        return res;
    }
}