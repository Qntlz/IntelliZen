package com.cerenio.mindvault;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.cerenio.flashcards.FlashcardDao;
import com.cerenio.notes.NoteDao;
import com.cerenio.planner.TaskDao;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    private TextView notesNum, taskNum, cardsNum;
    private NoteDao noteDao;
    private FlashcardDao flashcardDao;
    private TaskDao taskDao;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @SuppressLint("SetTextI18n")
    public void onViewCreated(@NonNull View root, Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        // Add this code to show username
        TextView greeter = root.findViewById(R.id.greeter);
        SharedPreferences prefs = requireContext().getSharedPreferences("user_prefs", MODE_PRIVATE);
        String email = prefs.getString("currentUserEmail", null);

        if (email != null) {
            String userJsonString = prefs.getString(email, null);
            if (userJsonString != null) {
                try {
                    JSONObject userJson = new JSONObject(userJsonString);
                    String username = userJson.getString("username");
                    greeter.setText("Good Morning, " + username + "!");
                } catch (JSONException e) {
                    e.printStackTrace();
                    greeter.setText("Good Morning, User!");
                }
            }
        }

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

        //  Opens the Pomodoro window
        root.findViewById(R.id.pomodoro)
                .setOnClickListener(v ->
                        startActivity(new Intent(
                                requireContext(),
                                com.cerenio.pomodoro.PomodoroActivity.class
                        ))
                );

        //  Opens the Create Task window
        root.findViewById(R.id.newTaskBtn)
                .setOnClickListener(v ->
                        startActivity(new Intent(
                                requireContext(),
                                com.cerenio.planner.TaskCreationActivity.class
                        ))
                );

        // bind views
        notesNum = root.findViewById(R.id.notesNum);
        taskNum  = root.findViewById(R.id.taskNum);
        cardsNum = root.findViewById(R.id.cardsNum);

        // Notes DB
        NoteDao noteDao = com.cerenio.notes.AppDatabase.getInstance(requireContext()).noteDao();
        new Thread(() -> {
            int count = noteDao.getAllNotes().size();
            requireActivity().runOnUiThread(() ->
                    notesNum.setText(count + " active Notebooks")
            );
        }).start();

        // Flashcards DB
        FlashcardDao flashDao = com.cerenio.flashcards.AppDatabase
                .getInstance(
                        (Application) requireActivity().getApplication()
                ).flashcardDao();
        flashDao.getAllFlashcards()
                .observe(getViewLifecycleOwner(), list ->
                        cardsNum.setText(list.size() + " Decks To Review")
                );

        // Planner DB
        TaskDao taskDao = com.cerenio.planner.AppDatabase
                .getInstance(requireContext())
                .taskDao();
        taskDao.getTaskCount()
                .observe(getViewLifecycleOwner(), count ->
                        taskNum.setText(count + " tasks")
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
