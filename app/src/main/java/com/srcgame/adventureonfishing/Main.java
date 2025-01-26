package com.srcgame.adventureonfishing;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.util.TypedValue;
import android.view.TextureView;

import androidx.annotation.RequiresApi;

import com.onesignal.Continue;
import com.onesignal.OneSignal;
import com.onesignal.debug.LogLevel;
import com.srcgame.adventureonfishing.model.GameResult;

public class Main extends Application {
    private static final String ONESIGNAL_APP_ID = "[YOU_ONESIGNAL_APP_ID]";
    private static final String PREFS_NAME = "MusicPreferences";
    public static final String MUSIC_PLAYING_KEY = "isMusicPlaying";

    public static MediaPlayer musicPlayer;
    public static boolean isMusicPlaying = true;

    public static GameDatabase gameDatabase;
    public static SharedPreferences.Editor editor;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate() {
        super.onCreate();
        OneSignal.getDebug().setLogLevel(LogLevel.VERBOSE);
        OneSignal.initWithContext(this, ONESIGNAL_APP_ID);
        OneSignal.getNotifications().requestPermission(true, Continue.with(r -> {
            if (r.isSuccess()) {
                if (r.getData()) {
                    // `requestPermission` completed successfully and the user has accepted permission
                } else {
                    // `requestPermission` completed successfully but the user has rejected permission
                }
            } else {
                r.getThrowable();
            }
        }));

        gameDatabase = GameDatabase.getInstance(this);

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        editor = preferences.edit();

        isMusicPlaying = preferences.getBoolean(MUSIC_PLAYING_KEY, false);

        musicPlayer = MediaPlayer.create(this, R.raw.background_music);
        musicPlayer.setLooping(true);
        if (isMusicPlaying) musicPlayer.start();
    }

    public static void adjustAspectRatio(TextureView view, int videoWidth, int videoHeight) {
        int viewWidth = view.getWidth();
        int viewHeight = view.getHeight();
        double aspectRatio = (double) videoHeight / videoWidth;

        int newWidth;
        if (viewWidth / aspectRatio >= viewHeight) {
            newWidth = viewWidth;
        } else {
            newWidth = (int) (viewHeight * aspectRatio);
        }
        float scaleX = (float) viewWidth / newWidth;

        Matrix matrix = new Matrix();
        matrix.setScale(scaleX, 1, viewWidth / 2f, viewHeight / 2f);
        view.setTransform(matrix);
    }

    public static int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static void saveGameResult(int pufferFishCount, int carpFishCount, int clownFishCount, int goldFishCount,
                                      int coins, int diamonds, int redDiamonds, int mapImage) {
        long timestamp = System.currentTimeMillis();
        GameResult gameResult = new GameResult(pufferFishCount, carpFishCount, clownFishCount, goldFishCount,
                diamonds, coins, redDiamonds, mapImage, timestamp);

        AsyncTask.execute(() -> gameDatabase.gameResultDao().insert(gameResult));
    }
}
