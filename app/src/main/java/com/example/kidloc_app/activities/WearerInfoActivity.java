package com.example.kidloc_app.activities;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.kidloc_app.R;
import com.example.kidloc_app.services.BluetoothService;

public class WearerInfoActivity extends AppCompatActivity {

    private static final String PREF_NAME = "WearerDevice Info";
    private static final String KEY_DEVICE_LOAD_NUMBER = "Device_Load_Number";
    private static final String KEY_NETWORK_TYPE = "Network_Type";
    private static final String KEY_WEARER_NAME = "Wearer_Name";
    private static final String KEY_EMERGENCY_CONTACT = "Emergency_Contact";
    private static final String KEY_RECIPIENT_NUMBER = "Recipient_Number";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_wearer_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText deviceNum = findViewById(R.id.device_num);
        deviceNum.setEnabled(false);

        EditText networkType = findViewById(R.id.network_type);
        networkType.setEnabled(false);

        Button editDeviceNumNetworkTypeBtn = findViewById(R.id.edit_deviceNum_networkType_btn);
        editDeviceNumNetworkTypeBtn.setOnClickListener(view -> {
            deviceNum.setEnabled(true);
            networkType.setEnabled(true);
        });

        EditText wearerName = findViewById(R.id.wearer_name);
        wearerName.setEnabled(false);

        EditText emergencyContact = findViewById(R.id.emergency_contact);
        emergencyContact.setEnabled(false);

        Button editWearerNameEmergencyContactBtn = findViewById(R.id.edit_wearerName_emergencyContact_btn);
        editWearerNameEmergencyContactBtn.setOnClickListener(view -> {
            wearerName.setEnabled(true);
            emergencyContact.setEnabled(true);
        });

        EditText smsRecipient = findViewById(R.id.sms_recipient);
        smsRecipient.setEnabled(false);

        Button editSmsRecipient = findViewById(R.id.edit_smsRecipient_btn);
        editSmsRecipient.setOnClickListener(view -> smsRecipient.setEnabled(true));

        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        deviceNum.setText(prefs.getString(KEY_DEVICE_LOAD_NUMBER, ""));
        networkType.setText(prefs.getString(KEY_NETWORK_TYPE, ""));
        wearerName.setText(prefs.getString(KEY_WEARER_NAME, ""));
        emergencyContact.setText(prefs.getString(KEY_EMERGENCY_CONTACT, ""));
        smsRecipient.setText(prefs.getString(KEY_RECIPIENT_NUMBER, ""));

        Button saveWearerDeviceInfo = findViewById(R.id.save_wearerDeviceInfo_btn);
        saveWearerDeviceInfo.setOnClickListener(view -> {
            String deviceNumber = deviceNum.getText().toString().trim();
            String network = networkType.getText().toString().trim();
            String nameWearer = wearerName.getText().toString().trim();
            String phone1 = emergencyContact.getText().toString().trim();
            String phone2 = smsRecipient.getText().toString().trim();

            if (deviceNumber.isEmpty() || network.isEmpty() || nameWearer.isEmpty() || phone1.isEmpty() || phone2.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields before saving.", Toast.LENGTH_SHORT).show();
                return;
            }

            new AlertDialog.Builder(WearerInfoActivity.this)
                    .setTitle("Confirm Information")
                    .setMessage("Are you sure you want to save this information?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(KEY_DEVICE_LOAD_NUMBER, deviceNumber);
                        editor.putString(KEY_NETWORK_TYPE, network);
                        editor.putString(KEY_WEARER_NAME, nameWearer);
                        editor.putString(KEY_EMERGENCY_CONTACT, phone1);
                        editor.putString(KEY_RECIPIENT_NUMBER, phone2);
                        editor.apply();

                        Toast.makeText(WearerInfoActivity.this, "Saved Information", Toast.LENGTH_LONG).show();

                        BluetoothService.sendToDevice("INFO_" + nameWearer + "_" + phone1 + "_" + phone2 + "_" + deviceNumber + "_X");
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        Toast.makeText(WearerInfoActivity.this, "Information not saved", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    })
                    .show();

        });

    }
}