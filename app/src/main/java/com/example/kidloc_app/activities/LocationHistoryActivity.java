package com.example.kidloc_app.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kidloc_app.database.DatabaseHelper;
import com.example.kidloc_app.database.LocationLog;
import com.example.kidloc_app.adapters.LocationLogAdapter;
import com.example.kidloc_app.R;

import java.util.List;

public class LocationHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView textViewEmpty;
    private DatabaseHelper databaseHelper;
    private LocationLogAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_history);

        recyclerView = findViewById(R.id.recyclerViewLogs);
        textViewEmpty = findViewById(R.id.textViewEmpty);
        databaseHelper = new DatabaseHelper(this);

        List<LocationLog> logs = databaseHelper.getAllLocationLogs();

        if (logs.isEmpty()) {
            textViewEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            textViewEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new LocationLogAdapter(logs);
            recyclerView.setAdapter(adapter);
        }
    }
}
