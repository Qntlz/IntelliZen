package com.cerenio.flashcards;

import android.app.Application;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Flashcard.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;
    public abstract FlashcardDao flashcardDao();

    public static synchronized AppDatabase getInstance(Application context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "flashcard_database")
                    .build();
        }
        return instance;
    }
}
