package com.cerenio.planner;

import android.content.res.Configuration;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import java.util.concurrent.Executors;

public class TaskCreationActivity extends AppCompatActivity {
    private EditText etTitle, etDesc;
    private AppDatabase db;
    private long selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_creation);

        // Status Bar
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.bg_color));  // or any color
        int nightModeFlags =
                getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        WindowInsetsControllerCompat insetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());

        // dark icons for light mode
        insetsController.setAppearanceLightStatusBars(nightModeFlags != Configuration.UI_MODE_NIGHT_YES); // light icons for dark mode

        etTitle = findViewById(R.id.etTitle);
        etDesc  = findViewById(R.id.etDesc);
        Button btnSave = findViewById(R.id.createBtn);

        db = AppDatabase.getInstance(this);
        long raw = getIntent().getLongExtra("SELECTED_DATE", System.currentTimeMillis());
        selectedDate = getStartOfDay(raw);

        btnSave.setOnClickListener(v -> saveTask());
        findViewById(R.id.backBtn).setOnClickListener(v -> finish());
    }

    private void saveTask() {
        String title = etTitle.getText().toString().trim();
        if (title.isEmpty()) {
            etTitle.setError("Enter a title");
            return;
        }

        Task task = new Task(title, etDesc.getText().toString().trim(), selectedDate);
        Executors.newSingleThreadExecutor().execute(() -> {
            db.taskDao().insert(task);
            runOnUiThread(() -> {
                Toast.makeText(this, "Task saved", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private long getStartOfDay(long timeInMillis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeInMillis);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }
}
