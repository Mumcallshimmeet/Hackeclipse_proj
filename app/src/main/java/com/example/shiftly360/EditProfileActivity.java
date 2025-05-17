package com.example.shiftly360;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "ProfilePrefs";
    private ImageView profileImageView;
    private EditText nameEdit;
    private EditText emailEdit;
    private EditText phoneEdit;
    private Uri selectedImageUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize views
        profileImageView = findViewById(R.id.edit_profile_image);
        nameEdit = findViewById(R.id.edit_name);
        emailEdit = findViewById(R.id.edit_email);
        phoneEdit = findViewById(R.id.edit_phone);
        Button saveButton = findViewById(R.id.save_button);
        Button cancelButton = findViewById(R.id.cancel_button);

        // Load existing data
        loadProfileData();

        // Initialize image picker
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    profileImageView.setImageURI(selectedImageUri);
                }
            }
        );

        // Set click listeners
        profileImageView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        saveButton.setOnClickListener(v -> saveProfile());
        cancelButton.setOnClickListener(v -> finish());
    }

    private void loadProfileData() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        nameEdit.setText(prefs.getString("name", ""));
        emailEdit.setText(prefs.getString("email", ""));
        phoneEdit.setText(prefs.getString("phone", ""));
        
        String savedImageUri = prefs.getString("profile_image", "");
        if (!savedImageUri.isEmpty()) {
            selectedImageUri = Uri.parse(savedImageUri);
            profileImageView.setImageURI(selectedImageUri);
        }
    }

    private void saveProfile() {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        
        editor.putString("name", nameEdit.getText().toString());
        editor.putString("email", emailEdit.getText().toString());
        editor.putString("phone", phoneEdit.getText().toString());
        
        if (selectedImageUri != null) {
            editor.putString("profile_image", selectedImageUri.toString());
        }
        
        editor.apply();

        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }
} 