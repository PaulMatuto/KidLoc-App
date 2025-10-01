package com.example.kidloc_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kidloc_app.R;
import com.example.kidloc_app.database.LocationLog;

import java.util.List;

public class LocationLogAdapter extends RecyclerView.Adapter<LocationLogAdapter.ViewHolder> {

    private final List<LocationLog> locationLogs;

    public LocationLogAdapter(List<LocationLog> locationLogs) {
        this.locationLogs = locationLogs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location_log, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocationLog log = locationLogs.get(position);
        holder.textViewTimestamp.setText(log.getTimestamp());
        holder.textViewCoordinates.setText("Lat: " + log.getLatitude() + ", Lon: " + log.getLongitude());
    }

    @Override
    public int getItemCount() {
        return locationLogs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTimestamp;
        TextView textViewCoordinates;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTimestamp = itemView.findViewById(R.id.textViewTimestamp);
            textViewCoordinates = itemView.findViewById(R.id.textViewCoordinates);
        }
    }
}
