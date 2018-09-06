package com.tendebit.dungeonmaster.core.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tendebit.dungeonmaster.core.view.SimpleTextViewHolder
import com.tendebit.dungeonmaster.core.model.SelectionElement
import com.tendebit.dungeonmaster.core.model.SelectionState
import io.reactivex.subjects.PublishSubject

class SelectionElementAdapter<T : SelectionElement, SelectedType : SelectionElement>(private var state: SelectionState<T, SelectedType>?) : RecyclerView.Adapter<SimpleTextViewHolder<T>>() {
    val itemClicks = PublishSubject.create<T>()

    fun update(newState: SelectionState<T, SelectedType>) {
        state = newState
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleTextViewHolder<T> {
        return SimpleTextViewHolder(LayoutInflater.from(parent.context), parent)
    }

    override fun getItemCount(): Int {
        state?.options?.let { return it.size }
        return 0
    }

    override fun onBindViewHolder(holder: SimpleTextViewHolder<T>, position: Int) {
        holder.populate(state!!.options[position], state!!.selection)
        holder.itemSelection.subscribe(itemClicks)
    }
}