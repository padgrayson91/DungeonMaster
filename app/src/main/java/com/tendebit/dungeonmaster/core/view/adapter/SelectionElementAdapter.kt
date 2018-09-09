package com.tendebit.dungeonmaster.core.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tendebit.dungeonmaster.core.model.SelectableElement
import com.tendebit.dungeonmaster.core.model.SelectionState
import com.tendebit.dungeonmaster.core.view.SelectableCardViewHolder
import io.reactivex.subjects.PublishSubject

class SelectionElementAdapter<T : SelectableElement, SelectedType : SelectableElement>(private var state: SelectionState<T, SelectedType>?) : RecyclerView.Adapter<SelectableCardViewHolder<T>>() {
    val itemClicks = PublishSubject.create<T>()

    fun update(newState: SelectionState<T, SelectedType>) {
        state = newState
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectableCardViewHolder<T> {
        return SelectableCardViewHolder(LayoutInflater.from(parent.context), parent)
    }

    override fun getItemCount(): Int {
        state?.options?.let { return it.size }
        return 0
    }

    override fun onBindViewHolder(holder: SelectableCardViewHolder<T>, position: Int) {
        holder.populate(state!!.options[position], state!!.selection)
        holder.itemSelection.subscribe(itemClicks)
    }
}