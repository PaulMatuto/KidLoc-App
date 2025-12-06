package com.example.kidloc_app.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.kidloc_app.R;
import com.example.kidloc_app.receivers.SmsReceiver;

public class MapActivity extends AppCompatActivity {

    private WebView webView;
    private String lastLat = "";
    private String lastLng = "";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        webView = findViewById(R.id.map_webView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient());

        webView.loadUrl("https://maps.google.com/?q=" + SmsReceiver.latitude + "," + SmsReceiver.longitude);

        updateMap();
    }

    private final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            String newLat = SmsReceiver.latitude;
            String newLng = SmsReceiver.longitude;

            if (newLat != null && newLng != null &&
                    (!newLat.equals(lastLat) || !newLng.equals(lastLng))) {

                String url = "https://maps.google.com/?q=" + newLat + "," + newLng;
                webView.loadUrl(url);

                lastLat = newLat;
                lastLng = newLng;
            }

            // Re-run every 5 seconds (adjust as needed)
            webView.postDelayed(this, 5000);
        }
    };

    private void updateMap() {
        // Call initially or after SMS is first received
        webView.post(updateRunnable);
    }

}