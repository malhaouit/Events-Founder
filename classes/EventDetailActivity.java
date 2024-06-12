package com.amtrustdev.localeventfinder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.amtrustdev.localeventfinder.models.Event;
import com.amtrustdev.localeventfinder.models.Location;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventDetailActivity extends AppCompatActivity {

    private static final String TAG = "EventDetailActivity";
    // UI components for displaying event details
    private TextView eventName, eventDateTime, eventDescription, eventDetails, eventLocation;
    // Firebase Firestore instance
    private FirebaseFirestore firestore;
    // Buttons for editing and deleting the event
    private Button edit_event_button, delete_event_button;
    // Event ID for the event being displayed
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content view to the event detail layout
        setContentView(R.layout.activity_event_detail);

        // Apply window insets to the main view
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set the title of the activity
        setTitle(R.string.activity_event_details_title);

        // Initialize UI components
        eventName = findViewById(R.id.eventName);
        eventDateTime = findViewById(R.id.eventDateTime);
        eventDescription = findViewById(R.id.eventDescription);
        eventDetails = findViewById(R.id.eventDetails);
        eventLocation = findViewById(R.id.eventLocation);
        edit_event_button = findViewById(R.id.edit_event_button);
        delete_event_button = findViewById(R.id.delete_event_button);

        // Initialize Firestore instance
        firestore = FirebaseFirestore.getInstance();

        // Get the event ID from the intent
        eventId = getIntent().getStringExtra("eventId");

        // Fetch event details for displaying
        fetchEventDetails(eventId);

        // Set the edit button click listener
        edit_event_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventDetailActivity.this, EditEventActivity.class);
                intent.putExtra("eventId", eventId);
                startActivity(intent);
            }
        });

        // Set the delete button click listener
        delete_event_button.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Event")
                    .setMessage("Are you sure you want to delete this event?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> deleteEvent(eventId))
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Fetch event details again when the activity is resumed
        fetchEventDetails(eventId);
    }

    // Delete the event from Firestore
    private void deleteEvent(String eventId) {
        firestore.collection("events").document(eventId).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EventDetailActivity.this, "Event deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(EventDetailActivity.this, "Error deleting event", Toast.LENGTH_SHORT).show());
    }

    // Fetch event details from Firestore
    private void fetchEventDetails(String eventId) {
        firestore.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Event event = documentSnapshot.toObject(Event.class);
                    if (event != null) {
                        updateUIWithEventDetails(event);
                        // Check if the current user is the creator of the event
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (currentUser != null && event.getUserId().equals(currentUser.getUid())) {
                            // User is the creator, show edit and delete buttons
                            edit_event_button.setVisibility(View.VISIBLE);
                            delete_event_button.setVisibility(View.VISIBLE);
                        } else {
                            // User is not the creator, hide edit and delete buttons
                            edit_event_button.setVisibility(View.GONE);
                            delete_event_button.setVisibility(View.GONE);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "Error getting event details", e);
                });
    }

    // Update the UI with event details
    private void updateUIWithEventDetails(Event event) {
        eventName.setText(event.getEventName());
        eventDateTime.setText(event.getDateTime());
        eventDescription.setText(event.getDescription());
        eventDetails.setText(event.getDetails());

        if (event.isOnline()) {
            eventLocation.setText("Online");
        } else {
            fetchLocationDetails(event.getLocationId());
        }
    }

    // Fetch location details from Firestore
    private void fetchLocationDetails(String locationId) {
        firestore.collection("locations").document(locationId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Location location = documentSnapshot.toObject(Location.class);
                    if (location != null) {
                        String locationText = location.getStreet() + ", " + location.getCity() + ", " + location.getCountry();
                        eventLocation.setText(locationText);
                    }
                })
                .addOnFailureListener(e -> Log.d(TAG, "Error getting location details", e));
    }
}
