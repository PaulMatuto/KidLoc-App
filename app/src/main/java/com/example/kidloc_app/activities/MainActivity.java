package com.example.kidloc_app.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;

import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.kidloc_app.R;
import com.example.kidloc_app.utils.PermissionUtils;

public class MainActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> enableBtLauncher;
    public ActivityResultLauncher<String> requestConnectPermissionLauncher;
    private ActivityResultLauncher<String> requestSendSmsPermissionLauncher;
    private ActivityResultLauncher<String> requestReceiveSmsPermissionLauncher;
    private ActivityResultLauncher<String> requestAccessFineLocationPermissionLauncher;
    private ActivityResultLauncher<String> requestAccessCoarseLocationPermissionLauncher;

    @SuppressLint("InlinedApi")
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ActivityCompat.requestPermissions(this, new String[]{
                android.Manifest.permission.BLUETOOTH_CONNECT,
                android.Manifest.permission.BLUETOOTH_SCAN,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.READ_SMS,
                android.Manifest.permission.RECEIVE_SMS
        }, 1);

        enableBtLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Toast.makeText(MainActivity.this, "Bluetooth enabled successfully", Toast.LENGTH_SHORT).show();
                        // Continue with Bluetooth operations
                    } else {
                        Toast.makeText(MainActivity.this, "Bluetooth is required for this app", Toast.LENGTH_LONG).show();
                    }
                });

        // Separate ActivityResultLaunchers for each permission request
        requestConnectPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        // BLUETOOTH_CONNECT permission granted, now enable Bluetooth
                        enableBluetooth();
                    } else {
                        Toast.makeText(MainActivity.this, "Bluetooth Connect permission is required", Toast.LENGTH_SHORT).show();
                    }
                });

        requestSendSmsPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        // Send SMS permission granted
                        Toast.makeText(MainActivity.this, "Send SMS permission is granted!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Send SMS permission is required", Toast.LENGTH_SHORT).show();
                    }
                });

        requestReceiveSmsPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        // Receive SMS permission granted
                        Toast.makeText(MainActivity.this, "Receive SMS permission is granted!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Receive SMS permission is required", Toast.LENGTH_SHORT).show();
                    }
                });

        requestAccessFineLocationPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        // Access Fine Location permission granted
                        Toast.makeText(MainActivity.this, "Access Fine Location permission is granted!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Access Fine Location permission is required", Toast.LENGTH_SHORT).show();
                    }
                });

        requestAccessCoarseLocationPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        // Access Coarse Location permission granted
                        Toast.makeText(MainActivity.this, "Access Coarse Location permission is granted!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Access Coarse Location permission is required", Toast.LENGTH_SHORT).show();
                    }
                });

        // Now check permissions and request them separately
        checkAndRequestPermissions();

        Button setupBtn = findViewById(R.id.setupBtn);
        setupBtn.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, BluetoothActivity.class);
            startActivity(i);
        });

    }

    private void checkAndRequestPermissions() {
        if (!PermissionUtils.checkBluetoothConnectPermission(this)) {
            PermissionUtils.requestBluetoothConnectPermission(requestConnectPermissionLauncher);
        } else {
            if (!PermissionUtils.checkBluetoothEnabled(this)) {
                PermissionUtils.enableBluetooth(this, enableBtLauncher);
            } else {
                Toast.makeText(this, "Bluetooth is already on", Toast.LENGTH_SHORT).show();
            }
        }

        if (!PermissionUtils.checkSendSmsPermission(this)) {
            PermissionUtils.requestSendSmsPermission(requestSendSmsPermissionLauncher);
        }

        if (!PermissionUtils.checkReceiveSmsPermission(this)) {
            PermissionUtils.requestReceiveSmsPermission(requestReceiveSmsPermissionLauncher);
        }

        if (!PermissionUtils.checkAccessFineLocationPermission(this)) {
            PermissionUtils.requestAccessFineLocationPermission(requestAccessFineLocationPermissionLauncher);
        }

        if (!PermissionUtils.checkAccessCoarseLocationPermission(this)) {
            PermissionUtils.requestAccessCoarseLocationPermission(requestAccessCoarseLocationPermissionLauncher);
        }
    }

    private void enableBluetooth()
    {
        Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        enableBtLauncher.launch(enableBTIntent);
    }

}