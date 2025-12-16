package com.example.tbdd_cki.domain.model;

public class AttendanceLocation {
    private double latitude;
    private double longitude;
    private Float distance; // distance from class location in meters

    public AttendanceLocation(double latitude, double longitude, Float distance) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
    }

    // Getters and Setters
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public Float getDistance() { return distance; }
    public void setDistance(Float distance) { this.distance = distance; }
}