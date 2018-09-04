package com.tendebit.dungeonmaster.charactercreation.classselection.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tendebit.dungeonmaster.charactercreation.classselection.model.CharacterClassDirectory
import com.tendebit.dungeonmaster.charactercreation.classselection.view.CharacterClassViewHolder
import io.reactivex.subjects.PublishSubject

class CharacterClassAdapter(private var classList: List<CharacterClassDirectory>) : RecyclerView.Adapter<CharacterClassViewHolder>() {
    val itemClicks = PublishSubject.create<CharacterClassDirectory>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterClassViewHolder {
        return CharacterClassViewHolder(LayoutInflater.from(parent.context), parent)
    }

    override fun getItemCount(): Int {
        return classList.size
    }

    override fun onBindViewHolder(holder: CharacterClassViewHolder, position: Int) {
        holder.populate(classList[position])
        holder.itemSelection.subscribe(itemClicks)
    }
}