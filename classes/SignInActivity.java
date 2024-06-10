package com.amtrustdev.localeventfinder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignInActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001; // A constant to identify the sign-in activity request
    private FirebaseAuth firebaseAuth; // An instance of FirebaseAuth for handling authentication
    private GoogleSignInClient googleSignInClient; // A GoogleSignInClient instance for handling Google Sign-In operations
    private ProgressBar signInProgressBar; // A ProgressBar to give visual feedback during sign-in and sign-out processes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set the activity title
        setTitle("User Authentication");

        String action = getIntent().getStringExtra("action");
        // Get the boolean value from the main activity to identify sign out process
        boolean isSigningOut = getIntent().getBooleanExtra("isSigningOut", false);

        // Initialize the sign in progress bar
        signInProgressBar = findViewById(R.id.sign_in_progress);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        if (isSigningOut) {
            signOut();
        } else if ("signIn".equals(action) || "addEvent".equals(action)) {
            signIn();
        }
    }

    // Starts the sign-in process with Google
    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent(); // This line fetch has the Google Sign-In intent from the GoogleSignInClient instance
        startActivityForResult(signInIntent, RC_SIGN_IN); // Starts the activity defined by the intent (signInIntent), which is the Google Sign-In flow
        signInProgressBar.setVisibility(View.VISIBLE);
    }

    // Signs out the user from both Firebase and Google
    private void signOut() {
        // Sign out from FirebaseAuth
        FirebaseAuth.getInstance().signOut();

        // Sign out from GoogleSignInClient
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN);
        googleSignInClient.signOut().addOnCompleteListener(this, task -> {
            signInProgressBar.setVisibility(View.GONE);
            // Update the UI to show sign-out success message
            showSignOutSuccess();

            // Show the "Go Back" button
            showGoBackButton();
        });
    }

    private void showSignOutSuccess() {
        TextView signOutMessage = findViewById(R.id.sign_out_message);
        signOutMessage.setVisibility(View.VISIBLE);
        signOutMessage.setText("Signed out successfully");
    }

    private void showGoBackButton() {
        Button goBackButton = findViewById(R.id.go_back_button);
        goBackButton.setVisibility(View.VISIBLE);
        goBackButton.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    // Handles the result after a Google Sign-In activity completes
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // Getting the Google Sign-In Account
            // This line extracts the sign-in result from the intent data returned by the Google Sign-In activity
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    // It processes the sign-in result, handles success or failure, and updates the UI accordingly
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            signInProgressBar.setVisibility(View.GONE);
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, update UI with the signed-in user's information
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            // Sign in was unsuccessful
            updateUIForFailedSignIn();
        }
    }

    // Integrates Firebase Authentication with Google Sign-In, allowing your Android app to authenticate users with their Google accounts
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        // Converts the Google sign-in results into a format understood by Firebase
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential) // Initiates the sign-in process with Firebase using the credentials obtained from Google
                .addOnCompleteListener(this, task -> { // The listener is provided with a Task<AuthResult> object, which can be used to determine if the sign-in was successful or not
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        updateUIForSuccessfulSignIn();
                    } else {
                        // If sign in fails, display a message to the user.
                        updateUIForFailedSignIn();
                    }
                });
    }

    // Updates the user interface when sign-in is successful, such as displaying success messages or buttons.
    private void updateUIForSuccessfulSignIn() {
        String action = getIntent().getStringExtra("action");
         // Reusing the same button for going back after sign in or sign out.

        if ("addEvent".equals(action)) {
            TextView messageView = findViewById(R.id.sign_in_success_message);
            messageView.setText("Signed in successfully.");
            messageView.setVisibility(View.VISIBLE);
            findViewById(R.id.proceed_to_add_event_button).setVisibility(View.VISIBLE);
            findViewById(R.id.proceed_to_add_event_button).setOnClickListener(v -> {
                startActivity(new Intent(SignInActivity.this, AddEventActivity.class));
            });
        } else {
            TextView messageView = findViewById(R.id.sign_out_message);  // Reusing the same TextView for sign in and sign out messages.
            Button goBackButton = findViewById(R.id.go_back_button);
            messageView.setText("Signed in successfully.");
            messageView.setVisibility(View.VISIBLE);
            goBackButton.setText("Go back to main");
            goBackButton.setVisibility(View.VISIBLE);
            goBackButton.setOnClickListener(v -> {
                startActivity(new Intent(SignInActivity.this, MainActivity.class));
            });
        }
    }

    // Updates the user interface in case of a sign-in failure, showing error messages and retry options
    private void updateUIForFailedSignIn() {
        // Display error message and retry option
        findViewById(R.id.sign_in_error_message).setVisibility(View.VISIBLE);
        findViewById(R.id.retry_sign_in_button).setVisibility(View.VISIBLE);
        signInProgressBar.setVisibility(View.GONE);
    }
}
