package com.amtrustdev.localeventfinder.models;

import com.google.firebase.firestore.Exclude;

// Location class responsible for data modeling of each event's location
public class Location {
    @Exclude
    private String locationId; // @Exclude notation is used here to avoid serialization of the location ID
    private String street;
    private String city;
    private String country;

    // Location constructor for initializing location fields
    public Location(String street, String city, String country) {
        this.street = street;
        this.city = city;
        this.country = country;
    }
    
    // Get location ID
    @Exclude
    public String getLocationId() {
        return locationId;
    }

    // Set location ID
    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    // Get location street
    public String getStreet() {
        return street;
    }

    // Set location street
    public void setStreet(String street) {
        this.street = street;
    }

    // Get location city
    public String getCity() {
        return city;
    }
    
    // Set location city
    public void setCity(String city) {
        this.city = city;
    }

    // Get location country
    public String getCountry() {
        return country;
    }

    // Set location country
    public void setCountry(String country) {
        this.country = country;
    }
}
