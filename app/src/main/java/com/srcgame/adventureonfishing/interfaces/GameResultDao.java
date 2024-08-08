package com.srcgame.adventureonfishing.interfaces;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.srcgame.adventureonfishing.model.GameResult;

import java.util.List;

@Dao
public interface GameResultDao {
    @Insert
    void insert(GameResult gameResult);

    @Query("SELECT * FROM game_results ORDER BY timestamp DESC")
    List<GameResult> getAllResults();
}