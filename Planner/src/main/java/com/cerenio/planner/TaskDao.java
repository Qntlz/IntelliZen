package com.cerenio.planner;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    void insert(Task task);

    @Update
    void update(Task task);   // <— new method

    @Query("SELECT * FROM tasks WHERE date = :date ORDER BY id DESC")
    LiveData<List<Task>> getTasksByDate(long date);
}
