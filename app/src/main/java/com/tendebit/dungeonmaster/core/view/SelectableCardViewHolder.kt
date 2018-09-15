package com.tendebit.dungeonmaster.core.view

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tendebit.dungeonmaster.R
import com.tendebit.dungeonmaster.core.model.SelectableElement
import io.reactivex.subjects.PublishSubject

class SelectableCardViewHolder<T : SelectableElement>(inflater: LayoutInflater, parent: ViewGroup) : RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item_generic_selection, parent, false)) {
    private val classNameText = itemView.findViewById<TextView>(R.id.primary_item_text)
    val itemSelection = PublishSubject.create<T>()

    fun populate(element: T, currentlySelected: SelectableElement?) {
        itemView.setOnClickListener {
            itemSelection.onNext(element)
        }
        classNameText.text = element.primaryText()
        if (element.primaryId() == currentlySelected?.primaryId()) {
            classNameText.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAccent))
        } else {
            classNameText.setTextColor(Color.LTGRAY)
        }
    }

}