package com.example.flashy

import android.content.ContentValues
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flashcardapp.FlashcardDatabaseHelper

class AddCardActivity : AppCompatActivity() {
    private lateinit var dbHelper: FlashcardDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)

        dbHelper = FlashcardDatabaseHelper(this)

        val questionEdit: EditText = findViewById(R.id.editQuestion)
        val answerEdit: EditText = findViewById(R.id.editAnswer)
        val saveButton: Button = findViewById(R.id.btnSave)

        saveButton.setOnClickListener {
            val question = questionEdit.text.toString().trim()
            val answer = answerEdit.text.toString().trim()

            if (question.isNotEmpty() && answer.isNotEmpty()) {
                val db = dbHelper.writableDatabase
                val values = ContentValues().apply {
                    put("question", question)
                    put("answer", answer)
                }
                db.insert("flashcards", null, values)
                Toast.makeText(this, "Flashcard saved!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Both fields required", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
