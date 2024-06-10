package com.amtrustdev.localeventfinder.models;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

public class Event {
    private String eventName;
    private String dateTime;
    private String description;
    private String details;
    private String category;
    private String locationId;
    @Exclude
    private String documentId; // Use @Exclude to avoid serialization
    private boolean isOnline;
    private String userId;

    // Default constructor (required for Firebase)
    public Event() {
    }

    // Parameterized constructor


    public Event(String eventName, String dateTime, String description, String details, String category, String locationId, boolean isOnline, String userId) {
        this.eventName = eventName;
        this.dateTime = dateTime;
        this.description = description;
        this.details = details;
        this.category = category;
        this.locationId = locationId;
        this.isOnline = isOnline;
        this.userId = userId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    @PropertyName("isOnline")
    public boolean isOnline() {
        return isOnline;
    }

    @PropertyName("isOnline")
    public void setOnline(boolean online) {
        isOnline = online;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
