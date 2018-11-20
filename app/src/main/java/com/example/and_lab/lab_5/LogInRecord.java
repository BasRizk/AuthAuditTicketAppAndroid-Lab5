package com.example.and_lab.lab_5;

public class LogInRecord {

    private String _id;
    private String username;
    private String timestamp;
    private String longitude;
    private String latitude;

    // Constructor that is used to create an instance of the Movie object
    public LogInRecord(String _id, String username, String timestamp, String longitude, String latitude) {
        this._id = _id;
        this.username = username;
        this.timestamp = timestamp;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String get_id() {
        return _id;
    }

    public String getUsername() {
        return username;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

}

