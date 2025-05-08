package com.cerenio.notes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.concurrent.Executors;

public class NotesActivity extends AppCompatActivity implements NoteAdapter.OnItemClickListener {
    private NoteAdapter adapter;
    private AppDatabase db;
    private LinearLayout emptyState; // Add this
    private RecyclerView recyclerNotes; // Add this

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        db = AppDatabase.getInstance(this);
        adapter = new NoteAdapter(this);

        // Initialize views
        emptyState = findViewById(R.id.emptyState);
        recyclerNotes = findViewById(R.id.recyclerNotes);

        recyclerNotes.setLayoutManager(new LinearLayoutManager(this));
        recyclerNotes.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fabAddNote);
        fab.setOnClickListener(v -> openCreate(null));

        // Set the backButton to redirect to the Main Page
        findViewById(R.id.backBtn).setOnClickListener(v -> finish());

        loadNotes();
    }

    private void loadNotes() {
        Executors.newSingleThreadExecutor().execute(() -> {
            final java.util.List<Note> list = db.noteDao().getAllNotes();
            //Log.d("NotesActivity", "Loaded notes: " + list.size());
            runOnUiThread(() -> {
                adapter.submitList(list);

                // Toggle visibility based on list size
                if (list.isEmpty()) {
                    emptyState.setVisibility(View.VISIBLE);
                    recyclerNotes.setVisibility(View.GONE);
                } else {
                    emptyState.setVisibility(View.GONE);
                    recyclerNotes.setVisibility(View.VISIBLE);
                }
            });
        });
    }

    private void openCreate(@Nullable Note existing) {
        Intent i = new Intent(this, CreateNoteActivity.class);
        if (existing != null) {
            i.putExtra("id", existing.id);
            i.putExtra("title", existing.title);
            i.putExtra("content", existing.content);
        }
        startActivityForResult(i, 100);
    }

    @Override
    public void onEdit(Note note) {
        openCreate(note);
    }

    @Override
    public void onDelete(Note note) {
        Executors.newSingleThreadExecutor().execute(() -> {
            db.noteDao().delete(note);
            loadNotes();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) loadNotes();
    }

}