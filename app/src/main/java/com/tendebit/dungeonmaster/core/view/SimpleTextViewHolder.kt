package com.tendebit.dungeonmaster.core.view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tendebit.dungeonmaster.R
import com.tendebit.dungeonmaster.core.viewmodel.DisplayableElement

class SimpleTextViewHolder<T : DisplayableElement>(inflater: LayoutInflater, parent: ViewGroup) : RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item_generic_text, parent, false)) {
    private val text = itemView.findViewById<TextView>(R.id.item_text)

    fun populate(item : T) {
        text.text = item.primaryText()
    }
}