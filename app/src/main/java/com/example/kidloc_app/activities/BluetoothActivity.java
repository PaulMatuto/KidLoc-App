package com.example.kidloc_app.activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.kidloc_app.R;
import com.example.kidloc_app.services.BluetoothService;
import com.example.kidloc_app.utils.PermissionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothActivity extends AppCompatActivity implements BluetoothService.DiscoveryCallback {

    private BluetoothService bluetoothService;
    private List<String> availableDeviceNamesList;
    private List<String> availableDeviceAddressList;
    private List<String> pairedDeviceNamesList;
    private List<String> pairedDeviceAddressList;
    private ArrayAdapter<String> deviceAdapter;
    ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bluetooth);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize BluetoothService
        bluetoothService = new BluetoothService(this, this);

        availableDeviceNamesList = new ArrayList<>();
        availableDeviceAddressList = new ArrayList<>();
        pairedDeviceNamesList = new ArrayList<>();
        pairedDeviceAddressList = new ArrayList<>();
        deviceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, availableDeviceNamesList);

        List<String> pairedDeviceNames = new ArrayList<>();
        ArrayAdapter<String> pairedAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, pairedDeviceNames);
        ListView pairedDevicesList = findViewById(R.id.paired_device_list);
        pairedDevicesList.setOnItemClickListener((adapterView, view, i, l) -> {
            String targetAddress = pairedDeviceAddressList.get(i);
            String targetDeviceName = pairedDeviceNamesList.get(i);
            Toast.makeText(BluetoothActivity.this, "Connecting to " + targetDeviceName, Toast.LENGTH_LONG).show();
            bluetoothService.connectToDevice(targetAddress);
            Intent intent = new Intent(BluetoothActivity.this, MenuActivity.class);
            intent.putExtra("is_Recipient", false);
            startActivity(intent);
        });
        pairedDevicesList.setAdapter(pairedAdapter);

        if (PermissionUtils.checkBluetoothConnectPermission(this)) {
            loadPairedDevices(pairedDeviceNames, pairedAdapter);
        } else {
            PermissionUtils.requestBluetoothConnectPermission(requestPermissionLauncher);
        }

        ListView availableDevicesList = findViewById(R.id.available_device_list);
        availableDevicesList.setOnItemClickListener((adapterView, view, i, l) -> {
            String targetAddress = availableDeviceAddressList.get(i);
            String targetDeviceName = availableDeviceNamesList.get(i);
            Toast.makeText(BluetoothActivity.this, "Pairing with " + targetDeviceName, Toast.LENGTH_LONG).show();
            bluetoothService.pairDevice(targetAddress);
        });
        availableDevicesList.setAdapter(deviceAdapter);

        // Start discovery when appropriate (e.g., when a button is clicked)
        Button scanButton = findViewById(R.id.setup_refresh_btn);
        scanButton.setOnClickListener(view -> {
            // Clear previous results
            availableDeviceNamesList.clear();
            deviceAdapter.notifyDataSetChanged();
            // Start new discovery
            if (PermissionUtils.checkBluetoothScanPermission(this)) {
                bluetoothService.startDiscovery();
            } else {
                PermissionUtils.requestBluetoothScanPermission(requestPermissionLauncher);
            }
        });

        Button recipientBtn = findViewById(R.id.recipient_btn);
        recipientBtn.setOnClickListener(view -> {
            Intent intent = new Intent(BluetoothActivity.this, MenuActivity.class);
            intent.putExtra("is_Recipient", true);
            startActivity(intent);
        });
    }

    // DiscoveryCallback implementation
    @Override
    public void onDeviceFound(BluetoothDevice device) {
        try {
            // Get device name or address
            String deviceName = device.getName();
            String displayName = (deviceName != null && !deviceName.isEmpty())
                    ? deviceName + " (" + device.getAddress() + ")"
                    : device.getAddress();
            String deviceAddress = device.getAddress();

            if (pairedDeviceAddressList.contains(deviceAddress)) {
                return;
            }

            // Update UI on the main thread
            runOnUiThread(() -> {
                availableDeviceNamesList.add(displayName);
                availableDeviceAddressList.add(deviceAddress);
                deviceAdapter.notifyDataSetChanged();
            });
        } catch (SecurityException e) {
            Log.e("BluetoothDemo", "Permission denied: " + e.getMessage());
        }
    }

    @Override
    public void onDiscoveryFinished() {
        Log.d("BluetoothDemo", "Discovery finished");
        runOnUiThread(() -> Toast.makeText(this, "Device scan complete", Toast.LENGTH_SHORT).show());
    }

    private void loadPairedDevices(List<String> list, ArrayAdapter<String> adapter) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Bluetooth connect permission not granted", Toast.LENGTH_SHORT).show();
        }

        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter != null) {
                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                list.clear();
                for (BluetoothDevice device : pairedDevices) {
                    String name = device.getName();
                    String address = device.getAddress();
                    if ("KidLoc".equals(name)) {
                        list.add(name + " (" + address + ")");
                        pairedDeviceNamesList.add(name);
                        pairedDeviceAddressList.add(address);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        } catch (SecurityException e) {
            Toast.makeText(this, "Permission error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("BluetoothActivity", "SecurityException while accessing paired devices", e);
        }
    }

    @Override
    public void onPermissionDenied() {
        runOnUiThread(() -> Toast.makeText(this, "Bluetooth permissions denied", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onDestroy() {
        if (bluetoothService != null) {
            bluetoothService.cleanup();
        }
        super.onDestroy();
    }
}