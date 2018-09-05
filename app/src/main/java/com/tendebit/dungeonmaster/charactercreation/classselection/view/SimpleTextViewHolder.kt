package com.tendebit.dungeonmaster.charactercreation.classselection.view

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tendebit.dungeonmaster.R
import com.tendebit.dungeonmaster.charactercreation.classselection.model.CharacterClassDirectory
import com.tendebit.dungeonmaster.charactercreation.classselection.model.CharacterClassInfo
import com.tendebit.dungeonmaster.core.SelectionElement
import io.reactivex.subjects.PublishSubject

class SimpleTextViewHolder<T : SelectionElement>(inflater: LayoutInflater, parent: ViewGroup, private val view: View = inflater.inflate(R.layout.list_item_character_class, parent, false)) : RecyclerView.ViewHolder(view) {
    private val classNameText = view.findViewById<TextView>(R.id.class_name)
    val itemSelection = PublishSubject.create<T>()

    fun populate(element: T, currentlySelected: SelectionElement?) {
        view.setOnClickListener {
            itemSelection.onNext(element)
        }
        classNameText.text = element.primaryText()
        if (element.primaryId() == currentlySelected?.primaryId()) {
            classNameText.setTextColor(ContextCompat.getColor(view.context, R.color.colorAccent))
        } else {
            classNameText.setTextColor(Color.LTGRAY)
        }
    }

}