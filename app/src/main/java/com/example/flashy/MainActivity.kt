package com.example.flashy

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.flashcardapp.Flashcard
import com.example.flashcardapp.FlashcardDatabaseHelper

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FlashcardAdapter
    private lateinit var dbHelper: FlashcardDatabaseHelper
    private val flashcards = mutableListOf<Flashcard>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = FlashcardDatabaseHelper(this)

        recyclerView = findViewById(R.id.recyclerView)
        adapter = FlashcardAdapter(flashcards)

        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = adapter

        PagerSnapHelper().attachToRecyclerView(recyclerView)

        loadFlashcardsFromDB()

        val fab = findViewById<View>(R.id.fabAdd)
        fab.setOnClickListener {
            val intent = Intent(this, AddCardActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadFlashcardsFromDB() {
        flashcards.clear()
        val db: SQLiteDatabase = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM flashcards", null)

        while (cursor.moveToNext()) {
            val card = Flashcard(
                id = cursor.getInt(0),
                question = cursor.getString(1),
                answer = cursor.getString(2)
            )
            flashcards.add(card)
        }

        cursor.close()
        adapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        loadFlashcardsFromDB()
    }

}