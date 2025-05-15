package com.cerenio.mindvault;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.cerenio.flashcards.FlashcardsActivity;
import com.cerenio.notes.NotesActivity;
import com.cerenio.planner.PlannerActivity;
import com.cerenio.profile.ProfileActivity;

public class MainActivity extends AppCompatActivity {

    LinearLayout navHome;
    LinearLayout navNotes;
    LinearLayout navPlanner;
    LinearLayout navFlashcards;
    LinearLayout navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        navHome = findViewById(R.id.navHome);
        navNotes = findViewById(R.id.navNotes);
        navPlanner = findViewById(R.id.navPlanner);
        navFlashcards = findViewById(R.id.navFlashcards);
        navProfile = findViewById(R.id.navProfile);

        // Opens Flashcard Window
        navFlashcards.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, FlashcardsActivity.class));
        });

        // Opens Notes Window
        navNotes.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, NotesActivity.class));
        });

        // Opens Planner Window
        navPlanner.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, PlannerActivity.class));
        });

        navProfile.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        });


        // Loads Home Fragment
        loadFragment(new HomeFragment());
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

}