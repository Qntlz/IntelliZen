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

public class SignUpActivity extends AppCompatActivity {

    private EditText emailEditText, usernameEditText, passwordEditText, confirmPasswordEditText;

    private TextView loginText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivty_sign_up);

        // Status Bar
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.bg_color));  // or any color
        int nightModeFlags =
                getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        WindowInsetsControllerCompat insetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());

        // dark icons for light mode
        insetsController.setAppearanceLightStatusBars(nightModeFlags != Configuration.UI_MODE_NIGHT_YES); // light icons for dark mode

        emailEditText = findViewById(R.id.emailEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        Button signUpButton = findViewById(R.id.signUpButton);
        loginText = findViewById(R.id.loginText);

        signUpButton.setOnClickListener(v -> attemptSignUp());
        loginText.setOnClickListener(v -> startActivity(new Intent(this,LoginActivity.class)));
    }

    private void attemptSignUp() {
        String email = emailEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        if (prefs.contains(email)) {
            Toast.makeText(this, "Email exists", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject userJson = new JSONObject();
            userJson.put("email", email);
            userJson.put("username", username);
            userJson.put("password", password);
            prefs.edit().putString(email, userJson.toString()).apply();

            // Auto-login
            prefs.edit()
                    .putBoolean("isLoggedIn", true)
                    .putString("currentUserEmail", email)
                    .apply();

            Intent i = new Intent();
            i.setClassName(this, "com.cerenio.mindvault.MainActivity");
            startActivity(i);
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
