package com.example.flashy

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContentProviderCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.flashcardapp.FlashcardDatabaseHelper
import com.example.flashy.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okio.IOException
import org.json.JSONArray
import org.json.JSONObject
import com.example.flashy.BuildConfig


class AddFlashcardFragment : Fragment(R.layout.fragment_add_flashcard) {
    private lateinit var dbHelper: FlashcardDatabaseHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = FlashcardDatabaseHelper(requireContext())

        val questionEdit = view.findViewById<EditText>(R.id.inputQuestion)
        val answerEdit = view.findViewById<EditText>(R.id.inputAnswer)
        val saveButton = view.findViewById<Button>(R.id.btnAddManual)

        val spinner = view.findViewById<Spinner>(R.id.spinnerCount)
        val options = (1..10).map { it.toString() }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

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

        val inputTopic = view.findViewById<EditText>(R.id.inputTopic)
        val btnGenerateAI = view.findViewById<Button>(R.id.btnGenerateAI)
        val spinnerTopic = view.findViewById<Spinner>(R.id.spinnerCount)

        btnGenerateAI.setOnClickListener {
            val topic = inputTopic.text.toString().trim()
            val selectedCount = spinner.selectedItem.toString().trim()
            if (topic.isNotEmpty()) {
                generateFlashcardsFromAI(topic, selectedCount)
            }
        }
    }

    private fun generateFlashcardsFromAI(topic: String, selectedCount: String) {
        val apiKey = BuildConfig.HUGGINGFACE_API_KEY
        val url = "https://router.huggingface.co/fireworks-ai/inference/v1/chat/completions"

        val json = JSONObject().apply {
            put("model", "accounts/fireworks/models/llama-v3p1-8b-instruct")
            put("stream", false)

            val messages = JSONArray()
            messages.put(JSONObject().apply {
                put("role", "user")
                put("content", "Generate exactly $selectedCount short quiz questions and their short answers about $topic. Do NOT number or label questions. Do NOT add any extra text or explanations. Just output the question followed by the answer on the next line. Keep both question and answer very concise and direct. this is an api call do not make a conversation, just give a question followed with the answer.")

            })
            put("messages", messages)
        }

        val body = RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            json.toString()
        )

        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $apiKey")
            .header("Content-Type", "application/json")
            .post(body)
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("AI_ERROR", "Failed: $e")
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    Log.e("AI_ERROR", "Request failed with code: ${response.code}, message: ${response.message}")
                    Log.e("AI_ERROR", "Response body: ${response.body?.string()}")
                    return
                }

                val bodyString = response.body?.string()
                Log.d("AI_RESPONSE_RAW", bodyString ?: "null response")

                try {
                    val jsonResponse = JSONObject(bodyString)
                    val choices = jsonResponse.getJSONArray("choices")
                    val content = choices.getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")

                    // Parse content into question/answer pairs
                    val lines = content.split("\n").map { it.trim() }.filter { it.isNotEmpty() }

                    val dbHelper = FlashcardDatabaseHelper(requireContext())
                    val db = dbHelper.writableDatabase

                    db.beginTransaction()
                    try {
                        for (i in 0 until lines.size step 2) {
                            if (i + 1 >= lines.size) break
                            val question = lines[i]
                            val answer = lines[i + 1]
                            db.execSQL(
                                "INSERT INTO flashcards (question, answer) VALUES (?, ?)",
                                arrayOf(question, answer)
                            )
                        }
                        db.setTransactionSuccessful()
                    } finally {
                        db.endTransaction()
                        db.close()
                    }

                    activity?.runOnUiThread {
                        Toast.makeText(requireContext(), "Flashcards added", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }

                } catch (e: Exception) {
                    Log.e("AI_PARSE", "Error parsing: $e")
                }
            }
        })
    }


}

