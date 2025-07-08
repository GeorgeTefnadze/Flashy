package com.example.flashy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.flashcardapp.Flashcard

class FlashcardAdapter(private val flashcards: List<Flashcard>) :
    RecyclerView.Adapter<FlashcardAdapter.FlashcardViewHolder>() {

    inner class FlashcardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val flashcardText: TextView = itemView.findViewById(R.id.flashcardText)
        private var showingQuestion = true

        fun bind(card: Flashcard) {
            flashcardText.text = card.question
            showingQuestion = true

            itemView.setOnClickListener {
                showingQuestion = !showingQuestion
                flashcardText.text = if (showingQuestion) card.question else card.answer
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlashcardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_flashcard, parent, false)
        return FlashcardViewHolder(view)
    }


    override fun getItemCount(): Int = Integer.MAX_VALUE

    override fun onBindViewHolder(holder: FlashcardViewHolder, position: Int) {
        val realPosition = position % flashcards.size
        holder.bind(flashcards[realPosition])
    }
}
