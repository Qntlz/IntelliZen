package com.cerenio.signup;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import java.util.concurrent.Executors;

public class LoginPage extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private ImageButton togglePasswordVisibility;
    private CheckBox rememberMe;
    private TextView forgotPassword, signUp;
    private Button loginButton;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        // Status Bar
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.bg_color));  // or any color
        int nightModeFlags =
                getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        WindowInsetsControllerCompat insetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());

        // dark icons for light mode
        insetsController.setAppearanceLightStatusBars(nightModeFlags != Configuration.UI_MODE_NIGHT_YES); // light icons for dark mode

        // Bind views
        emailInput               = findViewById(R.id.email_input);
        passwordInput            = findViewById(R.id.password_input);
        togglePasswordVisibility = findViewById(R.id.toggle_password_visibility);
        forgotPassword           = findViewById(R.id.forgot_password);
        loginButton              = findViewById(R.id.login_button);
        signUp                   = findViewById(R.id.signUp);


        // Redirect to Sign Up Page
        signUp.setOnClickListener(v ->
                startActivity(new Intent(LoginPage.this, SignUpPage.class))
        );

        // Toggle password visibility
        togglePasswordVisibility.setOnClickListener(v -> {
            if (isPasswordVisible) {
                passwordInput.setInputType(
                        InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_PASSWORD
                );
                togglePasswordVisibility.setImageResource(R.drawable.ic_eye_slash);
            } else {
                passwordInput.setInputType(
                        InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                );
                togglePasswordVisibility.setImageResource(R.drawable.ic_eye_slash);
            }
            passwordInput.setSelection(passwordInput.getText().length());
            isPasswordVisible = !isPasswordVisible;
        });

        // “Forgot Password?”
        forgotPassword.setOnClickListener(v ->
                startActivity(new Intent(LoginPage.this, ForgotPasswordPage.class))
        );

        // “Log In” button ⇒ authenticate via Room
        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String pwd   = passwordInput.getText().toString();
            if (email.isEmpty() || pwd.isEmpty()) {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Run lookup on a background thread
            AppDatabase db = AppDatabase.getInstance(this);
            Executors.newSingleThreadExecutor().execute(() -> {
                User user = db.userDao().getUser(email, pwd);
                runOnUiThread(() -> {
                    if (user != null) {
                        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(LoginPage.this, MainActivity.class));
                        Intent i = new Intent();
                        i.setClassName(this, "com.cerenio.mindvault.MainActivity");
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });
    }
}

// name: signup1
// email: signuptest@email.com
// dob: 05/11/2025
// password: pass