package com.cerenio.signup;

import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.BreakIterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ForgotPasswordPage extends AppCompatActivity {

    private EditText emailInput, password1, password2;
    private Button submitButton;
    private ImageView backButton;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_page);

        // bind views
        emailInput   = findViewById(R.id.emailInput);
        password1    = findViewById(R.id.password1);
        password2    = findViewById(R.id.password2);
        backButton   = findViewById(R.id.backButton);
        submitButton = findViewById(R.id.submitButton);

        // setup database
        userDao = AppDatabase.getInstance(getApplication()).userDao();

        // enable fields
        enablePasswordField(password1);
        enablePasswordField(password2);
        emailInput.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        // back action
        backButton.setOnClickListener(v -> finish());

        // submit action
        submitButton.setOnClickListener(v -> handleSubmit());
    }

    private void handleSubmit() {
        String email = emailInput.getText().toString().trim();
        String pass1 = password1.getText().toString();
        String pass2 = password2.getText().toString();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Valid email required");
            return;
        }
        if (pass1.isEmpty() || pass2.isEmpty()) {
            Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!pass1.equals(pass2)) {
            Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show();
            return;
        }

        // update password in background
        executor.execute(() -> {
            userDao.updatePassword(email, pass1);
            runOnUiThread(() ->
                    Toast.makeText(ForgotPasswordPage.this, "Password updated", Toast.LENGTH_SHORT).show()
            );
        });
    }

    private void enablePasswordField(EditText et) {
        et.setEnabled(true);
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.setCursorVisible(true);
        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        et.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}