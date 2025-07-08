package com.example.flashy

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.flashcardapp.Flashcard

class FlashcardAdapter(
    private val cards: MutableList<Flashcard>,
    private val onDelete: (Flashcard) -> Unit,
    private val onEmpty: () -> Unit  // callback to handle "no cards left"
) : RecyclerView.Adapter<FlashcardAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardText: TextView = itemView.findViewById(R.id.cardText)
        val cardView: CardView = itemView.findViewById(R.id.cardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_flashcard, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = if (cards.isEmpty()) 0 else Int.MAX_VALUE

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (cards.isEmpty()) return

        val realPos = position % cards.size
        val flashcard = cards[realPos]
        var showingQuestion = true

        holder.cardText.text = flashcard.question

        holder.cardView.setOnClickListener {
            val flipOut = ObjectAnimator.ofFloat(holder.cardView, "rotationY", 0f, 90f)
            val flipIn = ObjectAnimator.ofFloat(holder.cardView, "rotationY", -90f, 0f)

            flipOut.duration = 150
            flipIn.duration = 150

            flipOut.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    holder.cardText.text =
                        if (showingQuestion) flashcard.answer else flashcard.question
                    flipIn.start()
                    showingQuestion = !showingQuestion
                }
            })

            flipOut.start()
        }
    }

    fun deleteFlashcardAt(position: Int) {
        if (cards.isEmpty()) return

        val realPos = position % cards.size
        val deleted = cards.removeAt(realPos)
        notifyDataSetChanged()
        onDelete(deleted)

        if (cards.isEmpty()) {
            onEmpty()
        }
    }
}
