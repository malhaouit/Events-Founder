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
import com.google.firebase.firestore.FirebaseFirestore;

public class EventDetailActivity extends AppCompatActivity {

    private static final String TAG = "EventDetailActivity";
    private TextView eventName, eventDateTime, eventDescription, eventDetails, eventLocation;
    private FirebaseFirestore firestore;
    private Button edit_event_button, delete_event_button;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setTitle(R.string.activity_event_details_title);

        eventName = findViewById(R.id.eventName);
        eventDateTime = findViewById(R.id.eventDateTime);
        eventDescription = findViewById(R.id.eventDescription);
        eventDetails = findViewById(R.id.eventDetails);
        eventLocation = findViewById(R.id.eventLocation);
        edit_event_button = findViewById(R.id.edit_event_button);
        delete_event_button = findViewById(R.id.delete_event_button);

        firestore = FirebaseFirestore.getInstance();

        eventId = getIntent().getStringExtra("eventId");

        fetchEventDetails(eventId);

        edit_event_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventDetailActivity.this, EditEventActivity.class);
                intent.putExtra("eventId", eventId);
                startActivity(intent);
            }
        });

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
        fetchEventDetails(eventId);
    }

    private void deleteEvent(String eventId) {
        firestore.collection("events").document(eventId).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EventDetailActivity.this, "Event deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(EventDetailActivity.this, "Error deleting event", Toast.LENGTH_SHORT).show());
    }

    private void fetchEventDetails(String eventId) {
        firestore.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Event event = documentSnapshot.toObject(Event.class);
                    if (event != null) {
                        Log.d(TAG, "Event fetched from Firestore: " + event.toString()); // Log the entire event object
                        eventName.setText(event.getEventName());
                        eventDateTime.setText(event.getDateTime());
                        eventDescription.setText(event.getDescription());
                        eventDetails.setText(event.getDetails());

                        if (event.isOnline()) {
                            eventLocation.setText("Online");
                        }
                        else {
                            Toast.makeText(this, event.isOnline()+"", Toast.LENGTH_SHORT).show();
                            fetchLocationDetails(event.getLocationId());
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "Error getting event details", e);
                });
    }

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
