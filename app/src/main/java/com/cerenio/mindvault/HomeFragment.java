package com.cerenio.mindvault;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @SuppressLint("SetTextI18n")
    public void onViewCreated(@NonNull View root, Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        // Retrieves Current Date
        showTodayDate(root);

        //  Opens the Add Flashcard window
        root.findViewById(R.id.makeFlashcardBtn)
                .setOnClickListener(v ->
                        startActivity(new Intent(
                                requireContext(),
                                com.cerenio.flashcards.AddFlashcardActivity.class
                        ))
                );
        //  Opens the Add Note window
        root.findViewById(R.id.addNoteBtn)
                .setOnClickListener(v ->
                        startActivity(new Intent(
                                requireContext(),
                                com.cerenio.notes.CreateNoteActivity.class
                        ))
                );
    }

    private void showTodayDate(View root) {
        TextView dateField = root.findViewById(R.id.dateField);
        // format: full weekday name, full month name, day-of-month
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d", Locale.getDefault());
        String today = sdf.format(new Date());
        dateField.setText(today);
    }

}
