package com.cerenio.flashcards;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

public class FlashcardsActivity extends AppCompatActivity {
    private FlashcardAdapter adapter;
    private List<Flashcard> allFlashcards = new ArrayList<>();
    private LinearLayout emptyState;
    private LinearLayout recyclerCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Status Bar Theme
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.bg_color));  // or any color
        int nightModeFlags =
                getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        WindowInsetsControllerCompat insetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());

        // dark icons for light mode
        insetsController.setAppearanceLightStatusBars(nightModeFlags != Configuration.UI_MODE_NIGHT_YES); // light icons for dark mode

        setContentView(R.layout.activity_flashcards);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.flashcardMain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        emptyState = findViewById(R.id.emptyState);
        recyclerCard = findViewById(R.id.recyclerCard);

        // Set the recycler view to have an Linear Layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create an instance of the FlashCardAdapter and set it to the recycler view
        adapter = new FlashcardAdapter();
        recyclerView.setAdapter(adapter);

        // Arranges the cards in oldest first order
        FlashcardViewModel viewModel = new ViewModelProvider(this).get(FlashcardViewModel.class);
        viewModel.getAllFlashcards().observe(this, flashcards -> {
            allFlashcards = flashcards;
            adapter.setFlashcards(flashcards);
            adapter.notifyDataSetChanged();

            // Toggle visibility based on flashcards list
            if (flashcards.isEmpty()) {
                emptyState.setVisibility(View.VISIBLE);
                recyclerCard.setVisibility(View.GONE);
            } else {
                emptyState.setVisibility(View.GONE);
                recyclerCard.setVisibility(View.VISIBLE);
            }
        });

        // Set the addButton to redirect to the AddFlashCard Page
        findViewById(R.id.addButton).setOnClickListener(v -> {
            startActivity(new Intent(FlashcardsActivity.this, AddFlashcardActivity.class));
        });

        findViewById(R.id.shuffleBtn).setOnClickListener(v -> {
            if (allFlashcards.isEmpty()) {
                Toast.makeText(this, "No flashcards to shuffle", Toast.LENGTH_SHORT).show();
                return;
            }
            // make a mutable copy & shuffle
            ArrayList<Flashcard> shuffled = new ArrayList<>(allFlashcards);
            Collections.shuffle(shuffled);

            // launch detail activity at position 0 with the shuffled list
            Intent intent = new Intent(FlashcardsActivity.this, FlashcardDetailActivity.class);
            intent.putParcelableArrayListExtra("flashcards", shuffled);
            intent.putExtra("position", 0);
            startActivity(intent);
        });

        // Go back to Home
        findViewById(R.id.backBtn).setOnClickListener(v ->
                NavUtils.navigateUpFromSameTask(FlashcardsActivity.this)
        );


        // **SWIPE TO DELETE**
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT
        ) {
            @Override public boolean onMove(
                    @NonNull RecyclerView rv,
                    @NonNull RecyclerView.ViewHolder vh,
                    @NonNull RecyclerView.ViewHolder target
            ) { return false; }

            @Override public void onSwiped(
                    @NonNull RecyclerView.ViewHolder vh,
                    int direction
            ) {
                int pos = vh.getAdapterPosition();
                Flashcard toDelete = adapter.getFlashcardAt(pos);

                // delete on background thread
                Executors.newSingleThreadExecutor().execute(() ->
                        AppDatabase.getInstance(FlashcardsActivity.this.getApplication())
                                .flashcardDao()
                                .delete(toDelete)
                );

                runOnUiThread(() ->
                        Toast.makeText(FlashcardsActivity.this,
                                "Flashcard deleted",
                                Toast.LENGTH_SHORT).show()
                );
            }
        }).attachToRecyclerView(recyclerView);

    }
}
