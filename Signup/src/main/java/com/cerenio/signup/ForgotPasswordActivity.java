package com.cerenio.signup;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailEditText, newPasswordEditText, confirmNewPasswordEditText;

    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Status Bar
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.bg_color));  // or any color
        int nightModeFlags =
                getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        WindowInsetsControllerCompat insetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());

        // dark icons for light mode
        insetsController.setAppearanceLightStatusBars(nightModeFlags != Configuration.UI_MODE_NIGHT_YES); // light icons for dark mode

        emailEditText = findViewById(R.id.emailEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmNewPasswordEditText = findViewById(R.id.confirmNewPasswordEditText);
        backBtn = findViewById(R.id.backButton);
        Button resetButton = findViewById(R.id.resetPasswordButton);

        resetButton.setOnClickListener(v -> resetPassword());
        backBtn.setOnClickListener(v -> finish());
    }

    private void resetPassword() {
        String email = emailEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmPassword = confirmNewPasswordEditText.getText().toString().trim();

        if (email.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userJsonString = prefs.getString(email, null);

        if (userJsonString == null) {
            Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject userJson = new JSONObject(userJsonString);
            userJson.put("password", newPassword);
            prefs.edit().putString(email, userJson.toString()).apply();
            Toast.makeText(this, "Password updated", Toast.LENGTH_SHORT).show();
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
