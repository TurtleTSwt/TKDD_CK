package com.example.tbdd_cki.domain.model;

public class ClassLocation {
    private double latitude;
    private double longitude;
    private int allowedRadius; // in meters

    public ClassLocation(double latitude, double longitude, int allowedRadius) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.allowedRadius = allowedRadius;
    }

    // Getters and Setters
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public int getAllowedRadius() { return allowedRadius; }
    public void setAllowedRadius(int allowedRadius) { this.allowedRadius = allowedRadius; }
}