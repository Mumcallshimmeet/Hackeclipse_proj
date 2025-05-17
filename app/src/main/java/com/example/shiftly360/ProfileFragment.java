package com.example.shiftly360;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.example.shiftly360.utils.LocaleHelper;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {
    private static final String PREFS_NAME = "ProfilePrefs";
    private ImageView profileImage;
    private TextView nameText;
    private TextView roleText;
    private TextView emailText;
    private TextView phoneText;
    private Spinner languageSpinner;
    private Button audioHelpButton;
    private Button helpButton;
    private Button logoutButton;
    private ActivityResultLauncher<Intent> editProfileLauncher;
    private TextInputEditText emergencyContactName;
    private TextInputEditText emergencyContactNumber;
    private Button saveEmergencyContact;
    private SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize SharedPreferences
        prefs = requireActivity().getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE);

        // Initialize views
        profileImage = view.findViewById(R.id.profile_image);
        nameText = view.findViewById(R.id.name_text);
        roleText = view.findViewById(R.id.role_text);
        emailText = view.findViewById(R.id.email_text);
        phoneText = view.findViewById(R.id.phone_text);
        languageSpinner = view.findViewById(R.id.language_spinner);
        audioHelpButton = view.findViewById(R.id.audio_help_button);
        helpButton = view.findViewById(R.id.help_button);
        logoutButton = view.findViewById(R.id.logout_button);
        ImageButton editProfileButton = view.findViewById(R.id.edit_profile_button);
        emergencyContactName = view.findViewById(R.id.emergency_contact_name);
        emergencyContactNumber = view.findViewById(R.id.emergency_contact_number);
        saveEmergencyContact = view.findViewById(R.id.save_emergency_contact);

        // Initialize activity launcher for edit profile
        editProfileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == android.app.Activity.RESULT_OK) {
                        loadProfileData();
                    }
                }
        );

        // Load profile data
        loadProfileData();

        // Setup language spinner
        setupLanguageSpinner();

        // Setup button listeners
        setupButtonListeners();

        // Load saved emergency contact details
        emergencyContactName.setText(prefs.getString("emergency_contact_name", ""));
        emergencyContactNumber.setText(prefs.getString("emergency_contact_number", ""));

        // Set up click listeners
        saveEmergencyContact.setOnClickListener(v -> saveEmergencyContactDetails());
        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            editProfileLauncher.launch(intent);
        });

        return view;
    }

    private void loadProfileData() {
        String name = prefs.getString("name", "John Doe");
        String email = prefs.getString("email", "john.doe@example.com");
        String phone = prefs.getString("phone", "+91 9876543210");
        String imageUri = prefs.getString("profile_image", "");

        nameText.setText(name);
        emailText.setText(email);
        phoneText.setText(phone);

        if (!imageUri.isEmpty()) {
            profileImage.setImageURI(Uri.parse(imageUri));
        }
    }

    private void setupLanguageSpinner() {
        String[] languages = {"English", "हिन्दी (Hindi)", "বাংলা (Bengali)", "தமிழ் (Tamil)", "मराठी (Marathi)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                languages
        );
        languageSpinner.setAdapter(adapter);

        // Set current language selection
        String currentLang = LocaleHelper.getLanguage(requireContext());
        for (int i = 0; i < languages.length; i++) {
            if (LocaleHelper.getLanguageCode(languages[i]).equals(currentLang)) {
                languageSpinner.setSelection(i);
                break;
            }
        }

        // Add language change listener
        languageSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selectedLanguage = languages[position];
                String langCode = LocaleHelper.getLanguageCode(selectedLanguage);

                if (!langCode.equals(LocaleHelper.getLanguage(requireContext()))) {
                    LocaleHelper.setLocale(requireContext(), langCode);
                    ((MainActivity) requireActivity()).recreateActivity();
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });
    }

    private void setupButtonListeners() {
        audioHelpButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Playing audio instructions...", Toast.LENGTH_SHORT).show();
            // In a real app, this would play audio instructions
        });

        helpButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Opening help center...", Toast.LENGTH_SHORT).show();
            // In a real app, this would open a help center
        });

        logoutButton.setOnClickListener(v -> {
            // Log out with FirebaseAuth and go to LoginActivity
            FirebaseAuth.getInstance().signOut();
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    private void saveEmergencyContactDetails() {
        String name = emergencyContactName.getText().toString().trim();
        String number = emergencyContactNumber.getText().toString().trim();

        if (name.isEmpty() || number.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in both name and number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save to SharedPreferences
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("emergency_contact_name", name);
        editor.putString("emergency_contact_number", number);
        editor.apply();

        Toast.makeText(getContext(), "Emergency contact saved", Toast.LENGTH_SHORT).show();
    }
}
