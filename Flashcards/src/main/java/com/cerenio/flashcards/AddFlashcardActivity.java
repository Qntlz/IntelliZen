package com.cerenio.flashcards;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.Executors;

public class AddFlashcardActivity extends AppCompatActivity {
    private EditText titleInput, descriptionInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_flashcard);

        // Link components
        titleInput = findViewById(R.id.titleInput);
        descriptionInput = findViewById(R.id.descriptionInput);

        findViewById(R.id.createBtn).setOnClickListener(v -> saveFlashcard());

        // Set the backButton to redirect to the Main Page
        findViewById(R.id.backBtn).setOnClickListener(v -> finish());
    }

    private void saveFlashcard() {
        String title = titleInput.getText().toString();
        String description = descriptionInput.getText().toString();
        long timestamp = System.currentTimeMillis();

        Flashcard flashcard = new Flashcard();
        flashcard.setTitle(title);
        flashcard.setDescription(description);
        flashcard.setTimestamp(timestamp);

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase.getInstance(this.getApplication()).flashcardDao().insert(flashcard);
            runOnUiThread(this::finish);
        });
    }
}

