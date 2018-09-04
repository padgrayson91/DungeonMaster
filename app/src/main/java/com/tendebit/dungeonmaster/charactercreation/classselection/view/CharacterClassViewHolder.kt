package com.tendebit.dungeonmaster.charactercreation.classselection.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tendebit.dungeonmaster.R
import com.tendebit.dungeonmaster.charactercreation.classselection.model.CharacterClassDirectory
import io.reactivex.subjects.PublishSubject

class CharacterClassViewHolder(inflater: LayoutInflater, parent: ViewGroup, private val view: View = inflater.inflate(R.layout.list_item_character_class, parent, false)) : RecyclerView.ViewHolder(view) {
    private val classNameText = view.findViewById<TextView>(R.id.class_name)
    val itemSelection = PublishSubject.create<CharacterClassDirectory>()

    fun populate(characterClass: CharacterClassDirectory) {
        view.setOnClickListener {
            itemSelection.onNext(characterClass)
        }
        classNameText.text = characterClass.primaryText()
    }

}