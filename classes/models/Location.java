package com.amtrustdev.localeventfinder.models;

import com.google.firebase.firestore.Exclude;

public class Location {
    @Exclude
    private String locationId;
    private String street;
    private String city;
    private String country;

    // Default constructor (required for Firebase)
    public Location() {
    }

    // Parameterized constructor
    public Location(String locationId, String street, String city, String country) {
        this.locationId = locationId;
        this.street = street;
        this.city = city;
        this.country = country;
    }

    public Location(String street, String city, String country) {
        this.street = street;
        this.city = city;
        this.country = country;
    }

    // Getters and setters
    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}

