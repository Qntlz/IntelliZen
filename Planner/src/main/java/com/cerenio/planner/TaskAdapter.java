package com.cerenio.planner;

import android.icu.text.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> tasks = new ArrayList<>();

    public void setTasks(List<Task> newTasks) {
        tasks = newTasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        holder.bind(tasks.get(position));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleView,descView,dateView;
        private final CheckBox cbCompleted;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.taskTitle);
            descView  = itemView.findViewById(R.id.taskDesc);
            dateView  = itemView.findViewById(R.id.tvDate);
            cbCompleted = itemView.findViewById(R.id.cbCompleted);
        }

        void bind(Task task) {
            titleView.setText(task.title);
            descView.setText(task.description == null ? "" : task.description);

            // format your long to something like "May 13, 2025"
            String formatted = DateFormat
                    .getDateInstance(DateFormat.MEDIUM)
                    .format(new Date(task.date));
            dateView.setText(formatted);

            // ---- checkbox logic ----
            // avoid recycling issues
            cbCompleted.setOnCheckedChangeListener(null);
            cbCompleted.setChecked(task.completed);

            cbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
                task.completed = isChecked;
                // persist change
                Executors.newSingleThreadExecutor().execute(() -> {
                    AppDatabase.getInstance(buttonView.getContext())
                            .taskDao()
                            .update(task);
                });
            });
        }
    }
}