package com.srcgame.adventureonfishing.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.srcgame.adventureonfishing.Game;
import com.srcgame.adventureonfishing.R;
import com.srcgame.adventureonfishing.view.JoystickView;

public class GameActivity extends AppCompatActivity implements JoystickView.OnMoveListener {
    private Game game;

    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game);

        game = new Game(this);

        JoystickView joystickMove = findViewById(R.id.joystickMove);
        joystickMove.setOnMoveListener(this);

        ImageView buttonFishingRod = findViewById(R.id.buttonFishingRod);
        buttonFishingRod.setOnTouchListener((v, event) -> {
            if (!game.isRunning) return false;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    game.player.lineView.startGrowing();
                    return true;
                case MotionEvent.ACTION_UP:
                    game.player.lineView.stopGrowing();
                    return true;
            }
            return false;
        });
    }

    @Override
    public void onMove(int angle, int strength) {
        if (!game.isRunning) return;

        int speed = strength > 50 ? 10 : 5;

        if (angle == 180 && strength != 0) {
            game.player.moveLeft(speed);
        } else if (angle == 0 && strength != 0) {
            game.player.moveRight(speed);
        }
    }

    @Override
    public void onStopMove() {
        Drawable drawable = game.player.getDrawable();
        if (drawable instanceof GifDrawable) {
            ((GifDrawable) drawable).stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        game.stopGame();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    public void onExit(View view) {
        game.stopGame();
        startActivity(new Intent(GameActivity.this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void startGame(View view) {
        view.setVisibility(View.GONE);
        game.startGame();
    }

    public void playAgain(View view) {
        recreate();
    }
}