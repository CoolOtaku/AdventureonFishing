package com.srcgame.adventureonfishing;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.srcgame.adventureonfishing.interfaces.GameResultDao;
import com.srcgame.adventureonfishing.model.GameResult;

@Database(entities = {GameResult.class}, version = 1)
public abstract class GameDatabase extends RoomDatabase {
    private static volatile GameDatabase INSTANCE;

    public abstract GameResultDao gameResultDao();

    public static GameDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (GameDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            GameDatabase.class, "game_database").build();
                }
            }
        }
        return INSTANCE;
    }
}