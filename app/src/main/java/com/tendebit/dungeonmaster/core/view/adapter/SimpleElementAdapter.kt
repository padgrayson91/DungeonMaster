package com.tendebit.dungeonmaster.core.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tendebit.dungeonmaster.core.model.DisplayableElement
import com.tendebit.dungeonmaster.core.view.SimpleTextViewHolder

class SimpleElementAdapter<T : DisplayableElement> : RecyclerView.Adapter<SimpleTextViewHolder<T>>() {
    private val items = ArrayList<T>()

    fun update(newOptions: Collection<T>) {
        items.clear()
        items.addAll(newOptions)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleTextViewHolder<T> {
        return SimpleTextViewHolder(LayoutInflater.from(parent.context), parent)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: SimpleTextViewHolder<T>, position: Int) {
        holder.populate(items[position])
    }
}