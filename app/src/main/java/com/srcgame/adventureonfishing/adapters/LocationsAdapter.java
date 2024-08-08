package com.srcgame.adventureonfishing.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.srcgame.adventureonfishing.activitys.GameActivity;
import com.srcgame.adventureonfishing.R;
import com.srcgame.adventureonfishing.model.Location;

import java.util.List;

public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.LocationViewHolder> {
    private final List<Location> locations;

    public LocationsAdapter(List<Location> locations) {
        this.locations = locations;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.window_item_map, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        Location location = locations.get(position);
        holder.bind(location);
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    static class LocationViewHolder extends RecyclerView.ViewHolder {
        private final ImageView locationImage;
        private final TextView locationName;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            locationImage = itemView.findViewById(R.id.locationImage);
            locationName = itemView.findViewById(R.id.locationName);
        }

        public void bind(Location location) {
            locationName.setText(location.getName());
            locationImage.setImageResource(location.getImageResId());

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), GameActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                intent.putExtra("locationImage", location.getImageResId());
                intent.putExtra("locationWaterHeight", location.getWaterHeight());
                intent.putExtra("locationPlayerMargin", location.getPlayerMargin());

                ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(
                        itemView.getContext(), android.R.anim.fade_in, android.R.anim.fade_out);

                ActivityCompat.startActivity(itemView.getContext(), intent, options.toBundle());
            });
        }
    }
}