package com.amtrustdev.localeventfinder;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class AddEventActivity extends AppCompatActivity {
    // Edit texts to enter event's details
    private EditText editTextEventName, editTextEventDateTime, editTextEventDescription, editTextEventDetails, editTextStreet, editTextCity, editTextCountry;
    private Spinner spinnerCategory; // Used to select one of the event's categories by the user
    private CheckBox checkBoxIsOnline; // If the checkbox is checked then event is online, if not, the event has physical location
    private FirebaseFirestore firestore; // Using Firestore for data retrieval and storage
    private FirebaseAuth firebaseAuth; // The entry point of the Firebase Authentication

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set the title of the activity to "Add New Event"
        setTitle(R.string.activity_add_event_title);

        // Finds the views identified by the id attributes from the XML layout resources
        editTextEventName = findViewById(R.id.editTextEventName);
        editTextEventDateTime = findViewById(R.id.editTextEventDateTime);
        editTextEventDescription = findViewById(R.id.editTextEventDescription);
        editTextEventDetails = findViewById(R.id.editTextEventDetails);
        editTextStreet = findViewById(R.id.editTextStreet);
        editTextCity = findViewById(R.id.editTextCity);
        editTextCountry = findViewById(R.id.editTextCountry);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        checkBoxIsOnline = findViewById(R.id.checkBoxIsOnline);
        Button buttonSaveEvent = findViewById(R.id.buttonSaveEvent);

        // Returns the default FirebaseFirestore instance
        firestore = FirebaseFirestore.getInstance();
        // Returns an instance of the class FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        // If the user click the checkbox, the checkbox become checked and verse versa
        checkBoxIsOnline.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editTextStreet.setEnabled(!isChecked);
            editTextCity.setEnabled(!isChecked);
            editTextCountry.setEnabled(!isChecked);
        });

        // Handles Save Event button action
        buttonSaveEvent.setOnClickListener(view -> {
            // Gets the current user
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            // Check if the current user is authenticated before saving a new event
            if (currentUser != null) {
                // Passe the current user id as parameter
                saveEvent(currentUser.getUid());
            }
        });
    }

    private void saveEvent(String userId) {
        // Gets the edit texts contents entered by the user and store them in variables
        String eventName = editTextEventName.getText().toString().trim();
        String eventDateTime = editTextEventDateTime.getText().toString().trim();
        String eventDescription = editTextEventDescription.getText().toString().trim();
        String eventDetails = editTextEventDetails.getText().toString().trim();
        // Get the selected item for the spinner of categories and store it into selectedCategory variable
        String selectedCategory = spinnerCategory.getSelectedItem().toString();
        // Store the returned boolean value of the checkbox
        boolean isOnline = checkBoxIsOnline.isChecked();

        // Checks if the edit texts are not empty
        if (TextUtils.isEmpty(eventName) || TextUtils.isEmpty(eventDateTime) || TextUtils.isEmpty(eventDescription) || TextUtils.isEmpty(eventDetails)) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Checks if the event is online before processing adding the event
        if (isOnline) {
            // Creates new instance of the event and initialize the values
            Event event = new Event(userId, eventName, eventDateTime, eventDescription, eventDetails, selectedCategory, null, true);
            // Adds the new event to the firestore "events" collection
            firestore.collection("events").add(event)
                    // Handles the action when adding an event to the firestore succeed
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Event added successfully", Toast.LENGTH_SHORT).show();
                        // Starts a new transition from the current activity to the Main Activity after the adding new event to firestore succeed
                        Intent intent = new Intent(AddEventActivity.this, MainActivity.class);
                        startActivity(intent);
                    })
                    // handles the action when adding an event fail
                    .addOnFailureListener(e -> Toast.makeText(this, "Error adding event", Toast.LENGTH_SHORT).show());
        }
        else { // Handles the physical location details of the event when the location is not online
            String street = editTextStreet.getText().toString().trim();
            String city = editTextCity.getText().toString().trim();
            String country = editTextCountry.getText().toString().trim();

            // Checks if the edit texts are not empty
            if (TextUtils.isEmpty(street) || TextUtils.isEmpty(city) || TextUtils.isEmpty(country)) {
                Toast.makeText(this, "Please fill all the location fields", Toast.LENGTH_SHORT).show();
                return;
            }
            // Creates new instance of the event's location and initialize the values
            Location location = new Location(street, city, country);
            // Adds the new location details to the corresponding firestore "locations" collection
            firestore.collection("locations").add(location)
                    // Handles the action when adding the associated event's location to the firestore succeed
                    .addOnSuccessListener(documentReference -> {
                        // Gets the location ID from firestore 
                        String locationId = documentReference.getId();
                        // Creates new instance of the event and initialize the values
                        Event event = new Event(userId, eventName, eventDateTime, eventDescription, eventDetails, selectedCategory, locationId, false);
                        // Adds the new event to the firestore "events" collection with the associate location ID
                        firestore.collection("events").add(event)
                                // If the operation succeed
                                .addOnSuccessListener(eventDocumentReference -> {
                                    Toast.makeText(this, "Event added successfully", Toast.LENGTH_SHORT).show();
                                    // Start the activity transition from AddEventActivity to MainActivity after success
                                    Intent intent = new Intent(AddEventActivity.this, MainActivity.class);
                                    startActivity(intent);
                                })
                                // If the operation fails
                                .addOnFailureListener(e -> Toast.makeText(this, "Error adding event", Toast.LENGTH_SHORT).show());
                    })
                    // Handles any occurrence of failure when adding the associated event's location to the firestore
                    .addOnFailureListener(e -> Toast.makeText(this, "Error adding location", Toast.LENGTH_SHORT).show());
        }
    }
}
