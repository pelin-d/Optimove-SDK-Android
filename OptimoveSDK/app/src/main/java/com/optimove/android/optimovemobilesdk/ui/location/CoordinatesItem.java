package com.optimove.android.optimovemobilesdk.ui.location;

public class CoordinatesItem {

    private final String name, latitude, longitude;

    public CoordinatesItem(String name, String latitude, String longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public CoordinatesItem(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = String.valueOf(latitude);
        this.longitude = String.valueOf(longitude);
    }

    public String getLocationAsString() {
        return String.format("{ %s , %s }", latitude, longitude);
    }

    public String getName() {
        return name;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }
}
