package com.example.flashy

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.flashcardapp.Flashcard
import com.example.flashcardapp.FlashcardDatabaseHelper
import com.example.flashy.FlashcardAdapter
import com.example.flashy.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FlashcardListFragment : Fragment(R.layout.fragment_flashcard_list) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FlashcardAdapter
    private lateinit var dbHelper: FlashcardDatabaseHelper
    private val flashcards = mutableListOf<Flashcard>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = FlashcardDatabaseHelper(requireContext())

        recyclerView = view.findViewById(R.id.recyclerView)
        adapter = FlashcardAdapter(
            flashcards,
            onDelete = { deleted ->
                dbHelper.deleteFlashcard(deleted.id)
            },
            onEmpty = {
                findNavController().navigate(R.id.action_flashcardList_to_addFlashcard)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        PagerSnapHelper().attachToRecyclerView(recyclerView)

        // Swipe to delete setup
        val itemTouchHelper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                adapter.deleteFlashcardAt(position)
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)

        view.findViewById<FloatingActionButton>(R.id.fabAdd).setOnClickListener {
            findNavController().navigate(R.id.action_flashcardList_to_addFlashcard)
        }

        loadFlashcards()
    }

    override fun onResume() {
        super.onResume()
        loadFlashcards()
    }

    private fun loadFlashcards() {
        flashcards.clear()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM flashcards", null)
        while (cursor.moveToNext()) {
            flashcards.add(
                Flashcard(
                    id = cursor.getInt(0),
                    question = cursor.getString(1),
                    answer = cursor.getString(2)
                )
            )
        }
        cursor.close()

        if (flashcards.isEmpty()) {
            findNavController().navigate(R.id.action_flashcardList_to_addFlashcard)
        } else {
            adapter.notifyDataSetChanged()
        }
    }
}
