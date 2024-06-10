package com.amtrustdev.localeventfinder;

import static com.amtrustdev.localeventfinder.R.id.*;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amtrustdev.localeventfinder.models.Event;
import com.amtrustdev.localeventfinder.models.Location;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private FirebaseFirestore firestore;
    private Map<String, Location> locationMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.primary_dark));

        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Spinner categorySpinner = findViewById(R.id.categorySpinner);

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.categories, R.layout.spinner_item);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        categorySpinner.setAdapter(arrayAdapter);

        firestore = FirebaseFirestore.getInstance();
        locationMap = new HashMap<>();

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = parent.getItemAtPosition(position).toString();
                fetchLocations(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                fetchLocations("All Events");
            }
        });

    }

    private void fetchLocations(String category) {
        firestore.collection("locations")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Location location = document.toObject(Location.class);
                            locationMap.put(document.getId(), location);
                        }
                        fetchEvents(category);
                    }
                    else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    private void fetchEvents(String category) {
        firestore.collection("events")
                        .addSnapshotListener((queryDocumentSnapshots, e) -> {
                            if (e != null) {
                                Log.w(TAG, "Listen failed.", e);
                                return;
                            }

                            List<Event> eventList = new ArrayList<>();
                            assert queryDocumentSnapshots != null;
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                Event event = doc.toObject(Event.class);
                                event.setDocumentId(doc.getId());
                                String eventCategory = event.getCategory();

                                if (category.equalsIgnoreCase("All Events") || category.equalsIgnoreCase(eventCategory)) {
                                    eventList.add(event);
                                }
                            }

                            adapter = new EventAdapter(eventList, event -> {
                                Intent intent = new Intent(MainActivity.this, EventDetailActivity.class);
                                intent.putExtra("eventId", event.getDocumentId());
                                startActivity(intent);
                            });
                            recyclerView.setAdapter(adapter);
                        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_event) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                startActivity(new Intent(MainActivity.this, AddEventActivity.class));
            } else {
                startActivity(new Intent(MainActivity.this, SignInActivity.class).putExtra("action", "addEvent"));
            }
            return true;
        }
        else if (item.getItemId() == R.id.action_sign_out) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, SignInActivity.class).putExtra("isSigningOut", true));
                finish();
            } else {
                Toast.makeText(this, "No user is signed in!", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (item.getItemId() == action_sign_in) {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                startActivity(new Intent(MainActivity.this, SignInActivity.class).putExtra("action", "signIn"));
            } else {
                Toast.makeText(this, "User already signed in!", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
