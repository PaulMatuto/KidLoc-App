package com.example.kidloc_app.services;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import com.example.kidloc_app.utils.PermissionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BluetoothService {

    // Core Bluetooth components
    private final BluetoothAdapter bluetoothAdapter;
    private final List<BluetoothDevice> discoveredDevices = new ArrayList<>();

    // Communication streams
    static OutputStream outputStream;
    InputStream inputStream;

    // Receiver and context
    private BroadcastReceiver bluetoothReceiver;
    private final Context context;

    // Callback interface
    private final DiscoveryCallback callback;


    public interface DiscoveryCallback {
       void onDeviceFound(BluetoothDevice device);
       void onDiscoveryFinished();
       void onPermissionDenied();
    }

    public BluetoothService(Context context, DiscoveryCallback callback)
    {
       this.context = context;
       this.callback = callback;
       bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
       setupReceiver();
    }

    private void setupReceiver()
    {
       bluetoothReceiver = new BroadcastReceiver() {
           @Override
           public void onReceive(Context context, Intent intent) {
               String action = intent.getAction();

               if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                   BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                   if (device != null) {
                       // Safely get device name handling potential SecurityException
                       String deviceName = null;
                       try {
                           deviceName = device.getName();
                       } catch (SecurityException e) {
                           Log.e("BluetoothService", "Permission denied: " + e.getMessage());
                           if (callback != null) {
                               callback.onPermissionDenied();
                           }
                       }

                       // Process device if it has a name and is not a duplicate
                       if (deviceName != null && !discoveredDevices.contains(device)) {
                           discoveredDevices.add(device);
                           if (callback != null) callback.onDeviceFound(device);
                       }
                   }
               } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                   if (callback != null) callback.onDiscoveryFinished();
               }
           }
       };

       IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
       filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED); // Combined filters
       try {
           context.registerReceiver(bluetoothReceiver, filter);
       } catch (IllegalArgumentException e) {
           Log.e("BluetoothService", "Failed to register receiver: " + e.getMessage());
       }
    }

    public void startDiscovery() {
        // Check for required permissions before starting discovery
        if (PermissionUtils.hasRequiredPermissions(context)) {
            discoveredDevices.clear();
            if (bluetoothAdapter != null) {
                try {
                    if (bluetoothAdapter.isDiscovering()) {  // This call also requires permission
                        bluetoothAdapter.cancelDiscovery();
                    }
                    bluetoothAdapter.startDiscovery();
                } catch (SecurityException e) {
                    Log.e("BluetoothService", "Permission denied when starting discovery: " + e.getMessage());
                    if (callback != null) {
                        callback.onPermissionDenied();
                        callback.onDiscoveryFinished();
                    }
                }
            }
        } else {
            // Notify that discovery can't proceed due to missing permissions
            Log.w("BluetoothService", "Cannot start discovery - missing permissions");
            if (callback != null) {
                callback.onPermissionDenied();
                callback.onDiscoveryFinished();
            }
        }
    }

    public void stopDiscovery() {
        if (bluetoothAdapter != null) {
            try {
                if (bluetoothAdapter.isDiscovering()) {  // This call also requires permission
                    bluetoothAdapter.cancelDiscovery();
                }
            } catch (SecurityException e) {
                Log.e("BluetoothService", "Permission denied when stopping discovery: " + e.getMessage());
                if (callback != null) {
                    callback.onPermissionDenied();
                }
            }
        }
    }

    static BluetoothSocket socket;

    public void connectToDevice(String address) {
       BluetoothDevice targetDevice = bluetoothAdapter.getRemoteDevice(address);
       if (PermissionUtils.checkBluetoothConnectPermission(context)){
           try {
               socket = targetDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
               socket.connect();
           } catch (IOException e) {
               Log.e("Bluetooth", "Socket connection failed", e);
               connectToDevice(address);
           }
       }
    }

    public void pairDevice(String address) {
       BluetoothDevice targetDevice = bluetoothAdapter.getRemoteDevice(address);
       if (PermissionUtils.checkBluetoothConnectPermission(context)){
           targetDevice.createBond();
           Toast.makeText(context, BluetoothDevice.ACTION_BOND_STATE_CHANGED, Toast.LENGTH_SHORT).show();
       }
    }

    // Code to send data to ESP32
    public static void sendToDevice(String data){
        try {
            outputStream = socket.getOutputStream();
            outputStream.write(data.getBytes());
        } catch (IOException e) {
            Log.e("BluetoothService", "Failed to send data to device: " + e.getMessage());
        }
    }

    public void cleanup() {
        stopDiscovery();
        if (bluetoothReceiver != null) {
            try {
                context.unregisterReceiver(bluetoothReceiver);
                bluetoothReceiver = null;
            } catch (IllegalArgumentException e) {
                Log.e("BluetoothService", "Failed to unregister receiver: " + e.getMessage());
            }
        }
    }

}
