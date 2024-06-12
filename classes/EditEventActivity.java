package com.amtrustdev.localeventfinder;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.amtrustdev.localeventfinder.models.Event;
import com.amtrustdev.localeventfinder.models.Location;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditEventActivity extends AppCompatActivity {
    // UI components
    private EditText editTextName2, editTextDateTime2, editTextDescription2, editTextDetails2, editTextStreet2, editTextCity2, editTextCountry2;
    private CheckBox checkBoxIsOnline2;
    private Button buttonSaveEvent2;
    private Spinner spinnerCategory2;

    // Firebase Firestore instance
    private FirebaseFirestore firestore;

    // Event ID to be edited
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content view to the edit event layout
        setContentView(R.layout.activity_edit_event);

        // Apply window insets to the main view
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set the title of the activity
        setTitle(R.string.activity_edit_event_title);

        // Initialize UI components
        editTextName2 = findViewById(R.id.editTextEventName2);
        editTextDateTime2 = findViewById(R.id.editTextEventDateTime2);
        editTextDescription2 = findViewById(R.id.editTextEventDescription2);
        editTextDetails2 = findViewById(R.id.editTextEventDetails2);
        editTextStreet2 = findViewById(R.id.editTextStreet2);
        editTextCity2 = findViewById(R.id.editTextCity2);
        editTextCountry2 = findViewById(R.id.editTextCountry2);
        checkBoxIsOnline2 = findViewById(R.id.checkBoxIsOnline2);
        buttonSaveEvent2 = findViewById(R.id.buttonSaveEvent2);
        spinnerCategory2 = findViewById(R.id.spinnerCategory2);

        // Initialize Firestore instance
        firestore = FirebaseFirestore.getInstance();

        // Get the event ID from the intent
        eventId = getIntent().getStringExtra("eventId");

        // Load event data for editing
        loadEventData(eventId);

        // Handle checkbox state changes
        checkBoxIsOnline2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editTextStreet2.setEnabled(!isChecked);
            editTextCity2.setEnabled(!isChecked);
            editTextCountry2.setEnabled(!isChecked);
        });

        // Set the save button click listener
        buttonSaveEvent2.setOnClickListener(this::updateEvent);
    }

    // Load event data from Firestore
    private void loadEventData(String eventId) {
        firestore.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Event event = documentSnapshot.toObject(Event.class);
                    if (event != null) {
                        editTextName2.setText(event.getEventName());
                        editTextDateTime2.setText(event.getDateTime());
                        editTextDescription2.setText(event.getDescription());
                        editTextDetails2.setText(event.getDetails());
                        checkBoxIsOnline2.setChecked(event.isOnline());

                        if (event.isOnline()) {
                            editTextStreet2.setEnabled(false);
                            editTextCity2.setEnabled(false);
                            editTextCountry2.setEnabled(false);
                        } else {
                            firestore.collection("locations").document(event.getLocationId()).get()
                                    .addOnSuccessListener(locationSnapshot -> {
                                        Location location = locationSnapshot.toObject(Location.class);
                                        if (location != null) {
                                            editTextStreet2.setText(location.getStreet());
                                            editTextCity2.setText(location.getCity());
                                            editTextCountry2.setText(location.getCountry());
                                        }
                                    });
                        }

                        String[] categories = getResources().getStringArray(R.array.add_event_categories);
                        for (int i = 0; i < categories.length; i++) {
                            if (categories[i].equalsIgnoreCase(event.getCategory())) {
                                spinnerCategory2.setSelection(i);
                                break;
                            }
                        }
                    }
                });
    }

    // Update the event data
    public void updateEvent(View view) {
        String name = editTextName2.getText().toString();
        String dateTime = editTextDateTime2.getText().toString();
        String description = editTextDescription2.getText().toString();
        String details = editTextDetails2.getText().toString();
        String category = spinnerCategory2.getSelectedItem().toString();
        boolean isOnline = checkBoxIsOnline2.isChecked();

        if (isOnline) {
            firestore.collection("events").document(eventId)
                    .update("eventName", name, "dateTime", dateTime, "description", description, "details", details, "category", category, "isOnline", true, "locationId", null)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(EditEventActivity.this, "Event updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        } else {
            String street = editTextStreet2.getText().toString();
            String city = editTextCity2.getText().toString();
            String country = editTextCountry2.getText().toString();

            if (TextUtils.isEmpty(street) || TextUtils.isEmpty(city) || TextUtils.isEmpty(country)) {
                Toast.makeText(this, "Please fill all the location fields", Toast.LENGTH_SHORT).show();
                return;
            }

            firestore.collection("locations").whereEqualTo("street", street).whereEqualTo("city", city).whereEqualTo("country", country)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Location location = new Location(street, city, country);
                            firestore.collection("locations").add(location)
                                    .addOnSuccessListener(locationDocumentReference -> {
                                        String locationId = locationDocumentReference.getId();
                                        updateEventInFirestore(eventId, name, dateTime, description, details, category, isOnline, locationId);
                                    });
                        } else {
                            String locationId = queryDocumentSnapshots.getDocuments().get(0).getId();
                            updateEventInFirestore(eventId, name, dateTime, description, details, category, isOnline, locationId);
                        }
                    });
        }
    }

    // Update event data in Firestore
    private void updateEventInFirestore(String eventId, String name, String dateTime, String description, String details, String category, boolean isOnline, String locationId) {
        firestore.collection("events").document(eventId)
                .update("eventName", name, "dateTime", dateTime, "description", description, "details", details, "category", category, "isOnline", isOnline, "locationId", locationId)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditEventActivity.this, "Event updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }
}
