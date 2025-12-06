package com.example.kidloc_app.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.kidloc_app.R;
import com.example.kidloc_app.services.BluetoothService;

public class SafezoneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_safezone);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Spinner safezoneRadiusChoices = findViewById(R.id.radius_spinner);

        String[] distanceChoices = new String[10];

        for (int j = 0; j < 10; j++)
        {
            int k = j + 1;
            if (k == 1)
                distanceChoices[j] = k + " Meter";
            else
                distanceChoices[j] = k + " Meters";
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, distanceChoices);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        safezoneRadiusChoices.setAdapter(adapter);
        final String[] safezoneRadius = new String[1];
        safezoneRadiusChoices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int selectedRadius = i + 1;
                safezoneRadius[0] = "" + selectedRadius;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Button safezoneSaveBtn = findViewById(R.id.safezone_save_btn);
        safezoneSaveBtn.setOnClickListener(view -> {
            new AlertDialog.Builder(SafezoneActivity.this)
                    .setTitle("Confirm Safezone")
                    .setMessage("Do you want to set the Safezone to " + safezoneRadius[0] + " meters?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        String setDistance = "SET_" + safezoneRadius[0] + "_X";
                        BluetoothService.sendToDevice(setDistance);
                        Toast.makeText(SafezoneActivity.this, "Saved Safezone Distance: " + safezoneRadius[0] + " Meters", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        Toast.makeText(SafezoneActivity.this, "Safezone distance not saved", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    })
                    .show();
        });

    }
}