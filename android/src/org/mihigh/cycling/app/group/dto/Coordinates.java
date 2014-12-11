package org.mihigh.cycling.app.group.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class Coordinates implements Serializable {
    @SerializedName("lat")
    private double latitude;
    @SerializedName("lng")
    private double longitude;

    public Coordinates() {
    }

    public Coordinates(double latitude, double longitude) {
        this(latitude, longitude, null);
    }

    public Coordinates(double latitude, double longitude, Date date) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Coordinates{" +
               "latitude=" + latitude +
               ", longitude=" + longitude +
               '}';
    }
}
