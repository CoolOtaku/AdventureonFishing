package com.srcgame.adventureonfishing.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.srcgame.adventureonfishing.R;
import com.srcgame.adventureonfishing.model.GameResult;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class StatisticsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_OVERALL = 0;
    private static final int VIEW_TYPE_GAME_RESULT = 1;

    private final Context context;
    private final List<GameResult> gameResults;

    public StatisticsAdapter(Context context, List<GameResult> gameResults) {
        this.context = context;
        this.gameResults = gameResults;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_OVERALL;
        } else {
            return VIEW_TYPE_GAME_RESULT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_OVERALL) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_overall_statistics, parent, false);
            return new OverallStatsViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_game_statistics, parent, false);
            return new GameResultViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_OVERALL) {
            OverallStatsViewHolder overallStatsViewHolder = (OverallStatsViewHolder) holder;
            int totalPufferFish = 0;
            int totalCarpFish = 0;
            int totalClownFish = 0;
            int totalGoldFish = 0;

            int totalCoins = 0;
            int totalDiamonds = 0;
            int totalRedDiamonds = 0;

            for (GameResult gameResult : gameResults) {
                totalPufferFish += gameResult.pufferFishCount;
                totalCarpFish += gameResult.carpFishCount;
                totalClownFish += gameResult.clownFishCount;
                totalGoldFish += gameResult.goldFishCount;

                totalCoins += gameResult.coins;
                totalDiamonds += gameResult.diamonds;
                totalRedDiamonds += gameResult.redDiamonds;
            }

            overallStatsViewHolder.bind(totalPufferFish, totalCarpFish, totalClownFish, totalGoldFish,
                    totalCoins, totalDiamonds, totalRedDiamonds);
        } else {
            GameResultViewHolder gameResultViewHolder = (GameResultViewHolder) holder;
            GameResult gameResult = gameResults.get(position - 1);

            gameResultViewHolder.bind(gameResult);
        }
    }

    @Override
    public int getItemCount() {
        return gameResults.size() + 1;
    }

    static class OverallStatsViewHolder extends RecyclerView.ViewHolder {
        private final TextView textOverallPufferFishCount;
        private final TextView textOverallCarpFishCount;
        private final TextView textOverallClownFishCount;
        private final TextView textOverallGoldFishCount;

        private final TextView textOverallCoins;
        private final TextView textOverallDiamonds;
        private final TextView textOverallRedDiamonds;

        public OverallStatsViewHolder(@NonNull View itemView) {
            super(itemView);
            textOverallPufferFishCount = itemView.findViewById(R.id.textOverallPufferFishCount);
            textOverallCarpFishCount = itemView.findViewById(R.id.textOverallCarpFishCount);
            textOverallClownFishCount = itemView.findViewById(R.id.textOverallClownFishCount);
            textOverallGoldFishCount = itemView.findViewById(R.id.textOverallGoldFishCount);

            textOverallCoins = itemView.findViewById(R.id.textOverallCoins);
            textOverallDiamonds = itemView.findViewById(R.id.textOverallDiamonds);
            textOverallRedDiamonds = itemView.findViewById(R.id.textOverallRedDiamonds);
        }

        public void bind(int totalPufferFish, int totalCarpFish, int totalClownFish, int totalGoldFish,
                         int totalCoins, int totalDiamonds, int totalRedDiamonds) {
            textOverallPufferFishCount.setText(String.valueOf(totalPufferFish));
            textOverallCarpFishCount.setText(String.valueOf(totalCarpFish));
            textOverallClownFishCount.setText(String.valueOf(totalClownFish));
            textOverallGoldFishCount.setText(String.valueOf(totalGoldFish));

            textOverallCoins.setText(String.valueOf(totalCoins));
            textOverallDiamonds.setText(String.valueOf(totalDiamonds));
            textOverallRedDiamonds.setText(String.valueOf(totalRedDiamonds));
        }
    }

    static class GameResultViewHolder extends RecyclerView.ViewHolder {
        private final ImageView viewMapImage;
        private final TextView textGameTimestamp;

        private final TextView textPufferFishCount;
        private final TextView textCarpFishCount;
        private final TextView textClownFishCount;
        private final TextView textGoldFishCount;

        private final TextView textCoins;
        private final TextView textDiamonds;
        private final TextView textRedDiamonds;

        public GameResultViewHolder(@NonNull View itemView) {
            super(itemView);
            viewMapImage = itemView.findViewById(R.id.viewMapImage);
            textGameTimestamp = itemView.findViewById(R.id.textGameTimestamp);

            textPufferFishCount = itemView.findViewById(R.id.textPufferFishCount);
            textCarpFishCount = itemView.findViewById(R.id.textCarpFishCount);
            textClownFishCount = itemView.findViewById(R.id.textClownFishCount);
            textGoldFishCount = itemView.findViewById(R.id.textGoldFishCount);

            textCoins = itemView.findViewById(R.id.textCoins);
            textDiamonds = itemView.findViewById(R.id.textDiamonds);
            textRedDiamonds = itemView.findViewById(R.id.textRedDiamonds);
        }

        public void bind(GameResult gameResult) {
            viewMapImage.setImageResource(gameResult.mapImage);
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
            String formattedDate = sdf.format(gameResult.timestamp);
            textGameTimestamp.setText(formattedDate);

            textPufferFishCount.setText(String.valueOf(gameResult.pufferFishCount));
            textCarpFishCount.setText(String.valueOf(gameResult.carpFishCount));
            textClownFishCount.setText(String.valueOf(gameResult.clownFishCount));
            textGoldFishCount.setText(String.valueOf(gameResult.goldFishCount));

            textCoins.setText(String.valueOf(gameResult.coins));
            textDiamonds.setText(String.valueOf(gameResult.diamonds));
            textRedDiamonds.setText(String.valueOf(gameResult.redDiamonds));
        }
    }
}