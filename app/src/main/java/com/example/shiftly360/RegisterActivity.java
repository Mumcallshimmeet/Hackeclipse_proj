package com.example.shiftly360;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button registerButton, toLoginButton;
    private Spinner employerSpinner;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        registerButton = findViewById(R.id.btn_register);
        toLoginButton = findViewById(R.id.btn_to_login);
        employerSpinner = findViewById(R.id.employer_spinner);

        // Hardcoded employer for demo
        String[] employers = {"Hack Eclipse demo!"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, employers);
        employerSpinner.setAdapter(adapter);

        registerButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String employer = employerSpinner.getSelectedItem().toString();

            if (TextUtils.isEmpty(email)) {
                emailEditText.setError("Email is required.");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                passwordEditText.setError("Password is required.");
                return;
            }
            if (password.length() < 6) {
                passwordEditText.setError("Password must be at least 6 characters.");
                return;
            }

            // Direct registration if "Hack Eclipse demo!" is selected
            if (employer.equals("Hack Eclipse demo!")) {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Registration successful! You can log in now.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                finish();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                // Save registration as pending in Firestore (for other employers)
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> user = new HashMap<>();
                user.put("email", email);
                user.put("password", password); // In production, NEVER store plain passwords!
                user.put("employer", employer);
                user.put("status", "pending");

                db.collection("pending_users")
                        .add(user)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(this, "Registration submitted. Await employer approval.", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(this, LoginActivity.class));
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });

        toLoginButton.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }
}
