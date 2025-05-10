package com.cerenio.notes;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import java.util.concurrent.Executors;

public class CreateNoteActivity extends AppCompatActivity {
    private NoteDao noteDao;
    private EditText etTitle, etContent;
    private Integer editingId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_note);

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

        noteDao = AppDatabase.getInstance(this).noteDao();
        etTitle = findViewById(R.id.editTitle);
        etContent = findViewById(R.id.editContent);

        Intent i = getIntent();
        if (i.hasExtra("id")) {
            editingId = i.getIntExtra("id", -1);
            etTitle.setText(i.getStringExtra("title"));
            etContent.setText(i.getStringExtra("content"));
        }

        findViewById(R.id.btnSave).setOnClickListener(v -> saveNote());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void saveNote() {
        String t = etTitle.getText().toString().trim();
        String c = etContent.getText().toString().trim();
        if (TextUtils.isEmpty(t) && TextUtils.isEmpty(c)) return;

        Executors.newSingleThreadExecutor().execute(() -> {
            if (editingId != null) {
                Note n = new Note(t, c);
                n.id = editingId;
                noteDao.update(n);
            } else {
                noteDao.insert(new Note(t, c));
            }
            runOnUiThread(this::finish);
        });
    }
}
