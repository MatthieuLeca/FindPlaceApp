package com.matt.mentor2;

/**
 * Created by Matt on 26/01/2017.
 */

public class GeoPoint {
    private String latitude;
    private String longitude;

    public GeoPoint(double latitude, double longitude) {
        this.latitude = String.valueOf(latitude);
        this.longitude = String.valueOf(longitude);
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }
}
