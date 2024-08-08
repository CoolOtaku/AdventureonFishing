package com.srcgame.adventureonfishing.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "game_results")
public class GameResult {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int pufferFishCount;
    public int carpFishCount;
    public int clownFishCount;
    public int goldFishCount;

    public int coins;
    public int diamonds;
    public int redDiamonds;

    public int mapImage;
    public long timestamp;

    public GameResult(int pufferFishCount, int carpFishCount, int clownFishCount, int goldFishCount,
                      int coins, int diamonds, int redDiamonds, int mapImage, long timestamp) {
        this.pufferFishCount = pufferFishCount;
        this.carpFishCount = carpFishCount;
        this.clownFishCount = clownFishCount;
        this.goldFishCount = goldFishCount;

        this.coins = coins;
        this.diamonds = diamonds;
        this.redDiamonds = redDiamonds;

        this.mapImage = mapImage;
        this.timestamp = timestamp;
    }
}