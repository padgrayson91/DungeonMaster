package com.tendebit.dungeonmaster.charactercreation.classselection.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tendebit.dungeonmaster.charactercreation.classselection.model.CharacterClassDirectory
import com.tendebit.dungeonmaster.charactercreation.classselection.view.CharacterClassViewHolder
import com.tendebit.dungeonmaster.charactercreation.classselection.viewmodel.CharacterClassSelectionState
import io.reactivex.subjects.PublishSubject

class CharacterClassAdapter(private val state: CharacterClassSelectionState) : RecyclerView.Adapter<CharacterClassViewHolder>() {
    val itemClicks = PublishSubject.create<CharacterClassDirectory>()
    val classList = state.characterClassOptions

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterClassViewHolder {
        return CharacterClassViewHolder(LayoutInflater.from(parent.context), parent)
    }

    override fun getItemCount(): Int {
        return classList.size
    }

    override fun onBindViewHolder(holder: CharacterClassViewHolder, position: Int) {
        holder.populate(classList[position], state.selectedClass)
        holder.itemSelection.subscribe(itemClicks)
    }
}