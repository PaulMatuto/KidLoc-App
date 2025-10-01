package com.example.kidloc_app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.kidloc_app.R;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();

        Button safezoneActBtn = findViewById(R.id.safezone_act_btn);
        safezoneActBtn.setOnClickListener(view ->{
            Intent i = new Intent(MenuActivity.this, SafezoneActivity.class);
            startActivity(i);
        });

        Button mapActBtn = findViewById(R.id.map_act_btn);
        mapActBtn.setOnClickListener(view -> {
            Intent i = new Intent(MenuActivity.this, MapActivity.class);
            startActivity(i);
        });

        Button wearerInfo = findViewById(R.id.wearer_info_btn);
        wearerInfo.setOnClickListener(view -> {
            Intent i = new Intent(MenuActivity.this, WearerInfoActivity.class);
            startActivity(i);
        });

        Button bluetoothStatusBtn = findViewById(R.id.bt_status_btn);
        bluetoothStatusBtn.setOnClickListener(view -> {
            Intent i = new Intent(MenuActivity.this, BluetoothActivity.class);
            startActivity(i);
        });

        Button locationHistoryBtn = findViewById(R.id.location_history_btn);
        locationHistoryBtn.setOnClickListener(view -> {
            Intent i = new Intent(MenuActivity.this, LocationHistoryActivity.class);
            startActivity(i);
        });

        boolean isRecipient = intent.getBooleanExtra("is_Recipient", false);
        if (isRecipient){
            safezoneActBtn.setEnabled(false);
            wearerInfo.setEnabled(false);
            bluetoothStatusBtn.setEnabled(false);
        }
    }
}