package com.cerenio.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;
    private ImageView profileImage;
    private EditText usernameEditText;
    private String currentEmail;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Status Bar
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.bg_color));  // or any color
        int nightModeFlags =
                getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        WindowInsetsControllerCompat insetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());

        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            insetsController.setAppearanceLightStatusBars(false); // light icons for dark mode
        } else {
            insetsController.setAppearanceLightStatusBars(true);  // dark icons for light mode
        }

        profileImage = findViewById(R.id.profile_image);
        usernameEditText = findViewById(R.id.username_edit_text);
        Button changeImageBtn = findViewById(R.id.change_image_btn);
        Button saveChangesBtn = findViewById(R.id.save_changes_btn);

        // Get current user data
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        currentEmail = prefs.getString("currentUserEmail", "");
        loadUserData();

        changeImageBtn.setOnClickListener(v -> openGallery());
        saveChangesBtn.setOnClickListener(v -> saveProfileChanges());
    }

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userJsonString = prefs.getString(currentEmail, "");

        try {
            JSONObject userJson = new JSONObject(userJsonString);
            usernameEditText.setText(userJson.getString("username"));

            // Load profile picture if exists
            if(userJson.has("profileImage")) {
                selectedImageUri = Uri.parse(userJson.getString("profileImage"));
                profileImage.setImageURI(selectedImageUri);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            selectedImageUri = data.getData();
            profileImage.setImageURI(selectedImageUri);
        }
    }

    private void saveProfileChanges() {
        String newUsername = usernameEditText.getText().toString().trim();

        if (newUsername.isEmpty()) {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        try {
            JSONObject userJson = new JSONObject(prefs.getString(currentEmail, ""));
            userJson.put("username", newUsername);

            if(selectedImageUri != null) {
                userJson.put("profileImage", selectedImageUri.toString());
            }

            prefs.edit()
                    .putString(currentEmail, userJson.toString())
                    .apply();

            Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
