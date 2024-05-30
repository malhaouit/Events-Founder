package com.amtrustdev.localeventfinder;

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
import com.google.firebase.firestore.FirebaseFirestore;


public class AddEventActivity extends AppCompatActivity {
    private EditText editTextEventName, editTextEventDateTime, editTextEventDescription, editTextEventDetails, editTextStreet, editTextCity, editTextCountry;
    private CheckBox checkBoxIsOnline;
    private Button buttonSaveEvent;
    private FirebaseFirestore firestore;
    private Spinner spinnerCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_event);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setTitle(R.string.activity_add_event_title);

        editTextEventName = findViewById(R.id.editTextEventName);
        editTextEventDateTime = findViewById(R.id.editTextEventDateTime);
        editTextEventDescription = findViewById(R.id.editTextEventDescription);
        editTextEventDetails = findViewById(R.id.editTextEventDetails);
        editTextStreet = findViewById(R.id.editTextStreet);
        editTextCity = findViewById(R.id.editTextCity);
        editTextCountry = findViewById(R.id.editTextCountry);
        checkBoxIsOnline = findViewById(R.id.checkBoxIsOnline);
        buttonSaveEvent = findViewById(R.id.buttonSaveEvent);
        firestore = FirebaseFirestore.getInstance();

        spinnerCategory = findViewById(R.id.spinnerCategory);

        checkBoxIsOnline.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editTextStreet.setEnabled(!isChecked);
            editTextCity.setEnabled(!isChecked);
            editTextCountry.setEnabled(!isChecked);
        });

        buttonSaveEvent.setOnClickListener(view -> saveEvent());
    }

    private void saveEvent() {
        String eventName = editTextEventName.getText().toString().trim();
        String eventDateTime = editTextEventDateTime.getText().toString().trim();
        String eventDescription = editTextEventDescription.getText().toString().trim();
        String eventDetails = editTextEventDetails.getText().toString().trim();
        boolean isOnline = checkBoxIsOnline.isChecked();

        String selectedCategory = spinnerCategory.getSelectedItem().toString();

        if (TextUtils.isEmpty(eventName) || TextUtils.isEmpty(eventDateTime) || TextUtils.isEmpty(eventDescription) || TextUtils.isEmpty(eventDetails)) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isOnline) {
            Event event = new Event(eventName, eventDateTime, eventDescription, eventDetails, selectedCategory, null, true);
            firestore.collection("events").add(event)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Event added successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error adding event", Toast.LENGTH_SHORT).show());
        } else {
            String street = editTextStreet.getText().toString().trim();
            String city = editTextCity.getText().toString().trim();
            String country = editTextCountry.getText().toString().trim();

            if (TextUtils.isEmpty(street) || TextUtils.isEmpty(city) || TextUtils.isEmpty(country)) {
                Toast.makeText(this, "Please fill all the location fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Location location = new Location(street, city, country);
            firestore.collection("locations").add(location)
                    .addOnSuccessListener(documentReference -> {
                        String locationId = documentReference.getId();
                        Event event = new Event(eventName, eventDateTime, eventDescription, eventDetails, selectedCategory, locationId, false);
                        firestore.collection("events").add(event)
                                .addOnSuccessListener(eventDocumentReference -> {
                                    Toast.makeText(this, "Event added successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Error adding event", Toast.LENGTH_SHORT).show());
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error adding location", Toast.LENGTH_SHORT).show());
        }
    }
}
