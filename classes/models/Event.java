package com.amtrustdev.localeventfinder.models;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

// The Event class that represents each created or founded event
public class Event {
    private String eventName;
    private String dateTime;
    private String description;
    private String details;
    private String category;
    private String locationId;
    @Exclude
    private String documentId; // @Exclude notation is used to avoid serialization of the document ID
    private boolean isOnline;
    private String userId;

    // Default constructor (required for Firebase)
    public Event() {
    }

    // Event constructor
    public Event(String userId, String eventName, String dateTime, String description, String details, String category, String locationId, boolean isOnline) {
        this.userId = userId;
        this.eventName = eventName;
        this.dateTime = dateTime;
        this.description = description;
        this.details = details;
        this.category = category;
        this.locationId = locationId;
        this.isOnline = isOnline;
    }

    // Get user ID
    public String getUserId() {
        return userId;
    }

    // Set user ID
    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Get event name
    public String getEventName() {
        return eventName;
    }

    // Set event name
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    // Get event datetime
    public String getDateTime() {
        return dateTime;
    }

    // Set event datetime
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    // Get event description
    public String getDescription() {
        return description;
    }

    // Sett event description
    public void setDescription(String description) {
        this.description = description;
    }

    // Get event details
    public String getDetails() {
        return details;
    }

    // Set event details
    public void setDetails(String details) {
        this.details = details;
    }

    // Get event category
    public String getCategory() {
        return category;
    }

    // Set event category
    public void setCategory(String category) {
        this.category = category;
    }

    // Get location ID
    public String getLocationId() {
        return locationId;
    }

    // Set location ID
    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    // Get Firestore document ID
    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    // Set Firestore document ID
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    // Get boolean value of isOnline field depending on the event is Online or not
    @PropertyName("isOnline")
    public boolean isOnline() {
        return isOnline;
    }

    // Set boolean value to isOnline field depending on the event is Online or not
    @PropertyName("isOnline")
    public void setOnline(boolean online) {
        isOnline = online;
    }
}
