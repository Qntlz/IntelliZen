package com.cerenio.notes;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.Executors;

public class CreateNoteActivity extends AppCompatActivity {
    private NoteDao noteDao;
    private EditText etTitle, etContent;
    private Integer editingId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_note);

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
