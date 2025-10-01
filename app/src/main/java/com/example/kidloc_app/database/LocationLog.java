package com.example.kidloc_app.database;

public class LocationLog {
    private final String latitude;
    private final String longitude;
    private final String timestamp;

    public LocationLog(String latitude, String longitude, String timestamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    public String getLatitude() { return latitude; }
    public String getLongitude() { return longitude; }
    public String getTimestamp() { return timestamp; }
}
