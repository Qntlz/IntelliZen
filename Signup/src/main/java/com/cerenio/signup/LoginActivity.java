package com.cerenio.signup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView signUpTextView, forgotPasswordTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Status Bar
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.bg_color));  // or any color
        int nightModeFlags =
                getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        WindowInsetsControllerCompat insetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());

        // dark icons for light mode
        insetsController.setAppearanceLightStatusBars(nightModeFlags != Configuration.UI_MODE_NIGHT_YES); // light icons for dark mode

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signUpTextView = findViewById(R.id.signUpTextView);
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);

        loginButton.setOnClickListener(v -> attemptLogin());
        signUpTextView.setOnClickListener(v -> startActivity(new Intent(this, SignUpActivity.class)));
        forgotPasswordTextView.setOnClickListener(v -> startActivity(new Intent(this, ForgotPasswordActivity.class)));
    }

    private void attemptLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userJsonString = prefs.getString(email, null);

        if (userJsonString == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject userJson = new JSONObject(userJsonString);
            if (password.equals(userJson.getString("password"))) {
                // Login success
                prefs.edit()
                        .putBoolean("isLoggedIn", true)
                        .putString("currentUserEmail", email)
                        .apply();

                Intent i = new Intent();
                i.setClassName(this, "com.cerenio.mindvault.MainActivity");
                startActivity(i);
                finish();
            } else {
                Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
    }
}

// Created Accounts
// test@gmail.com
// test


