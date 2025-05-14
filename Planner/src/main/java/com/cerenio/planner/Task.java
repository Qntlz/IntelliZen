package com.cerenio.planner;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks")
public class Task {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @NonNull
    public String title;

    public String description;

    // Milliseconds at start of day
    public long date;

    /** new field to track completion status */
    @ColumnInfo(defaultValue = "0")
    public boolean completed;

    /** keep this constructor for creating new tasks */
    public Task(@NonNull String title, String description, long date) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.completed = false;
    }

    /** no-arg constructor so Room can instantiate existing rows */
    public Task() { }
}


