package com.example.flashcardapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class FlashcardDatabaseHelper(context: Context) : SQLiteOpenHelper(context, "flashcards.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE flashcards (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                question TEXT NOT NULL,
                answer TEXT NOT NULL
            )
        """.trimIndent())

        // Optional starter flashcards
        db.execSQL("INSERT INTO flashcards (question, answer) VALUES ('Capital of France?', 'Paris')")
        db.execSQL("INSERT INTO flashcards (question, answer) VALUES ('5 x 6?', '30')")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS flashcards")
        onCreate(db)
    }

    fun deleteFlashcard(id: Int) {
        writableDatabase.delete("flashcards", "id = ?", arrayOf(id.toString()))
    }
}
