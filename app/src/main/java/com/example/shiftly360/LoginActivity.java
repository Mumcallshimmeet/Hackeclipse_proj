package com.example.shiftly360;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailEditText, passwordEditText;
    private Button loginButton, toRegisterButton, partnerBusinessButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Find views by ID
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        toRegisterButton = findViewById(R.id.btn_to_register);
        partnerBusinessButton = findViewById(R.id.btn_partner_business);

        // "Partner as a Business" button
        partnerBusinessButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, PartnerBusinessActivity.class);
            startActivity(intent);
        });

        // Login button logic
        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            // Input validation
            if (email.isEmpty()) {
                emailEditText.setError("Email cannot be empty");
                emailEditText.requestFocus();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.setError("Please enter a valid email");
                emailEditText.requestFocus();
                return;
            }
            if (password.isEmpty()) {
                passwordEditText.setError("Password cannot be empty");
                passwordEditText.requestFocus();
                return;
            }

            // Check approval status in Firestore before attempting login
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("pending_users")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        boolean approved = false;
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            String status = doc.getString("status");
                            if ("approved".equals(status)) {
                                approved = true;
                                break;
                            } else {
                                Toast.makeText(LoginActivity.this, "Your account is not yet approved by your employer.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        if (approved || queryDocumentSnapshots.isEmpty()) {
                            // Proceed with sign-in
                            signInWithFirebase(email, password);
                        }
                        // If not approved, message already shown above
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(LoginActivity.this, "Error checking approval: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        // Sign up button logic
        toRegisterButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void signInWithFirebase(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
