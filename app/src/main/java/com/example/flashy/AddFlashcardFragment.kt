package com.example.flashy

import android.content.ContentValues
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.flashcardapp.FlashcardDatabaseHelper
import com.example.flashy.R

class AddFlashcardFragment : Fragment(R.layout.fragment_add_flashcard) {
    private lateinit var dbHelper: FlashcardDatabaseHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = FlashcardDatabaseHelper(requireContext())

        val questionEdit = view.findViewById<EditText>(R.id.editQuestion)
        val answerEdit = view.findViewById<EditText>(R.id.editAnswer)
        val saveButton = view.findViewById<Button>(R.id.btnSave)

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
                Toast.makeText(requireContext(), "Flashcard added!", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } else {
                Toast.makeText(requireContext(), "Both fields are required.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}