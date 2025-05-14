package com.cerenio.planner;

import android.content.Intent;
import android.content.res.Configuration;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class PlannerActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private AppDatabase db;
    private long selectedDate;
    private Spinner filterSpinner;
    private ImageView filterChevron;
    private List<Task> allTasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner);

        // Status Bar
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.bg_color));  // or any color
        int nightModeFlags =
                getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        WindowInsetsControllerCompat insetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());

        // dark icons for light mode
        insetsController.setAppearanceLightStatusBars(nightModeFlags != Configuration.UI_MODE_NIGHT_YES); // light icons for dark mode

        // 1) Calendar setup
        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener((view, year, month, day) -> {
            selectedDate = getStartOfDay(year, month, day);
            observeTasks();
        });
        // initialize to today:
        Calendar today = Calendar.getInstance();
        selectedDate = getStartOfDay(
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH)
        );

//        recyclerView = findViewById(R.id.taskRecyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        adapter = new TaskAdapter();
//        recyclerView.setAdapter(adapter);
//
//        db = AppDatabase.getInstance(this);
//        selectedDate = getStartOfDay(System.currentTimeMillis());
//
//        filterChevron = findViewById(R.id.filterChevron);

        // 2) RecyclerView + Adapter
        recyclerView = findViewById(R.id.taskRecyclerView);
        adapter = new TaskAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // 3) DB instance
        db = AppDatabase.getInstance(this);

        // 4) Replace Spinner with chevron + PopupMenu
        filterChevron = findViewById(R.id.filterChevron);
        filterChevron.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            popup.getMenuInflater().inflate(R.menu.filter_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_all) {
                    applyFilter(0);
                } else if (itemId == R.id.menu_completed) {
                    applyFilter(1);
                } else if (itemId == R.id.menu_ongoing) {
                    applyFilter(2);
                }
                return true;
            });
            popup.show();
        });

        // 5) Kick off the first load
        observeTasks();



        // Observe tasks for the selected date
//        db.taskDao()
//                .getTasksByDate(selectedDate)
//                .observe(this, new Observer<List<Task>>() {
//                    @Override
//                    public void onChanged(List<Task> tasks) {
//                        allTasks = tasks;                // cache full list
//                        applyFilter(filterSpinner.getSelectedItemPosition());
//                    }
//                });


        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, TaskCreationActivity.class);
            intent.putExtra("SELECTED_DATE", selectedDate);
            startActivity(intent);
        });

        // Set the backButton to redirect to the Main Page
        findViewById(R.id.backBtn).setOnClickListener(v ->
                NavUtils.navigateUpFromSameTask(PlannerActivity.this)
        );
    }

    private void observeTasks() {
        db.taskDao()
                .getTasksByDate(selectedDate)
                .observe(this, tasks -> {
                    allTasks = tasks;       // cache full list
                    applyFilter(0);         // default = All
                });
    }

    private void applyFilter(int option) {
        List<Task> filtered = new ArrayList<>();
        for (Task t : allTasks) {
            if (option == 1 && !t.completed)   continue; // only completed
            if (option == 2 &&  t.completed)    continue; // only ongoing
            filtered.add(t);                     // otherwise include
        }
        adapter.setTasks(filtered);
    }

    private long getStartOfDay(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, day, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

//    private long getStartOfDay(long timeInMillis) {
//        Calendar cal = Calendar.getInstance();
//        cal.setTimeInMillis(timeInMillis);
//        cal.set(Calendar.HOUR_OF_DAY, 0);
//        cal.set(Calendar.MINUTE, 0);
//        cal.set(Calendar.SECOND, 0);
//        cal.set(Calendar.MILLISECOND, 0);
//        return cal.getTimeInMillis();
//    }

//    private void applyFilter(int filterOption) {
//        List<Task> filtered = new ArrayList<>();
//
//        switch (filterOption) {
//            case 1: // Completed
//                for (Task t : allTasks) {
//                    if (t.completed) filtered.add(t);
//                }
//                break;
//
//            case 2: // Ongoing
//                for (Task t : allTasks) {
//                    if (!t.completed) filtered.add(t);
//                }
//                break;
//
//            default: // 0 = All
//                filtered.addAll(allTasks);
//        }
//
//        adapter.setTasks(filtered);
//    }

}

