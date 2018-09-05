package com.tendebit.dungeonmaster.core.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tendebit.dungeonmaster.core.view.SimpleTextViewHolder
import com.tendebit.dungeonmaster.core.model.SelectionElement
import com.tendebit.dungeonmaster.core.model.SelectionState
import io.reactivex.subjects.PublishSubject

class SelectionElementAdapter<T : SelectionElement, SelectedType : SelectionElement>(private val state: SelectionState<T, SelectedType>) : RecyclerView.Adapter<SimpleTextViewHolder<T>>() {
    val itemClicks = PublishSubject.create<T>()
    private val options = state.options

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleTextViewHolder<T> {
        return SimpleTextViewHolder(LayoutInflater.from(parent.context), parent)
    }

    override fun getItemCount(): Int {
        return options.size
    }

    override fun onBindViewHolder(holder: SimpleTextViewHolder<T>, position: Int) {
        holder.populate(options[position], state.selection)
        holder.itemSelection.subscribe(itemClicks)
    }
}