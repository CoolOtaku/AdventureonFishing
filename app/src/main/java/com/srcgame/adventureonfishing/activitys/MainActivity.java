package com.srcgame.adventureonfishing.activitys;

import static com.srcgame.adventureonfishing.Main.MUSIC_PLAYING_KEY;
import static com.srcgame.adventureonfishing.Main.adjustAspectRatio;
import static com.srcgame.adventureonfishing.Main.editor;
import static com.srcgame.adventureonfishing.Main.isMusicPlaying;
import static com.srcgame.adventureonfishing.Main.musicPlayer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.srcgame.adventureonfishing.Main;
import com.srcgame.adventureonfishing.R;
import com.srcgame.adventureonfishing.adapters.LocationsAdapter;
import com.srcgame.adventureonfishing.adapters.StatisticsAdapter;
import com.srcgame.adventureonfishing.model.GameResult;
import com.srcgame.adventureonfishing.model.Location;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {
    private TextureView mainScreenBG;
    private MediaPlayer mediaPlayer;
    private ImageButton buttonMusic;
    private RecyclerView locationRecyclerView, statisticsRecyclerView;

    @SuppressLint("MissingInflatedId")
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
        setContentView(R.layout.activity_main);

        mainScreenBG = findViewById(R.id.mainScreenBG);
        mainScreenBG.setSurfaceTextureListener(this);
        mainScreenBG.setOnClickListener(v -> {
            if (locationRecyclerView.getVisibility() == View.VISIBLE) {
                locationRecyclerView.setVisibility(View.GONE);
            }
            if (statisticsRecyclerView.getVisibility() == View.VISIBLE) {
                statisticsRecyclerView.setVisibility(View.GONE);
            }
        });

        locationRecyclerView = findViewById(R.id.locationRecyclerView);
        statisticsRecyclerView  = findViewById(R.id.statisticsRecyclerView);

        String[] locationNames = getResources().getStringArray(R.array.map_names);
        String[] locationImages = getResources().getStringArray(R.array.map_images);
        String[] locationWaterHeight = getResources().getStringArray(R.array.map_water_height);
        String[] locationPlayerMargin = getResources().getStringArray(R.array.map_player_margin);

        List<Location> locations = new ArrayList<>();
        for (int i = 0; i < locationNames.length; i++) {
            int imageResId = getResources().getIdentifier(locationImages[i], "drawable", getPackageName());
            locations.add(new Location(locationNames[i], imageResId,
                    Double.parseDouble(locationWaterHeight[i]), Double.parseDouble(locationPlayerMargin[i])));
        }

        LocationsAdapter locationsAdapter = new LocationsAdapter(locations);
        locationRecyclerView.setAdapter(locationsAdapter);

        AsyncTask.execute(() -> {
            List<GameResult> gameResults = Main.gameDatabase.gameResultDao().getAllResults();
            runOnUiThread(() -> {
                StatisticsAdapter statisticsAdapter = new StatisticsAdapter(this, gameResults);
                statisticsRecyclerView.setAdapter(statisticsAdapter);
            });
        });

        buttonMusic = findViewById(R.id.buttonMusic);
        if (isMusicPlaying) buttonMusic.setImageResource(R.drawable.music_on);
        else buttonMusic.setImageResource(R.drawable.music_off);
    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
        mediaPlayer = new MediaPlayer();
        try {
            Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.background_video);
            mediaPlayer.setDataSource(this, videoUri);
            mediaPlayer.setSurface(new Surface(surface));
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();

            adjustAspectRatio(mainScreenBG, mediaPlayer.getVideoWidth(), mediaPlayer.getVideoHeight());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

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

    public void openLink(View view) {
        String url = "https://www.freeprivacypolicy.com/live/0cf6d788-e752-42b0-8b4b-e03cbc5d89da";
        Uri uri = Uri.parse(url);
        this.startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    public void openPlayWindow(View view) {
        if (locationRecyclerView.getVisibility() == View.GONE) {
            locationRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    public void openStatisticsWindow(View view) {
        if (statisticsRecyclerView.getVisibility() == View.GONE) {
            statisticsRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    public void onOffMusic(View view) {
        if (isMusicPlaying) {
            musicPlayer.pause();
            buttonMusic.setImageResource(R.drawable.music_off);
        } else {
            musicPlayer.start();
            buttonMusic.setImageResource(R.drawable.music_on);
        }
        isMusicPlaying = !isMusicPlaying;
        editor.putBoolean(MUSIC_PLAYING_KEY, isMusicPlaying);
        editor.apply();
    }

    public void exitGame(View view) {
        onDestroy();
        System.exit(0);
    }
}