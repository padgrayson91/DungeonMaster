package com.tendebit.dungeonmaster.core.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tendebit.dungeonmaster.core.model.SelectableElement
import com.tendebit.dungeonmaster.core.model.SelectionState
import com.tendebit.dungeonmaster.core.view.SelectableCardViewHolder
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class SelectionElementAdapter<T : SelectableElement, SelectedType : SelectableElement>(val state: SelectionState<T, SelectedType>) : RecyclerView.Adapter<SelectableCardViewHolder<T>>() {
    val itemClicks = PublishSubject.create<T>()
    private val disposable = CompositeDisposable()
    private val options = ArrayList<T>()
    private var selection: SelectedType? = null

    init {
        disposable.addAll(
                state.options.subscribe { updateOptions(it) },
                state.selection.subscribe { updateSelection(it) }
                )
    }

    private fun updateOptions(options: List<T>) {
        this.options.clear()
        this.options.addAll(options)
        launch(UI) { notifyDataSetChanged() }
    }

    private fun updateSelection(selection: SelectedType?) {
        this.selection = selection
        launch(UI) { notifyDataSetChanged() }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectableCardViewHolder<T> {
        return SelectableCardViewHolder(LayoutInflater.from(parent.context), parent)
    }

    override fun getItemCount(): Int {
        return options.size
    }

    override fun onBindViewHolder(holder: SelectableCardViewHolder<T>, position: Int) {
        holder.populate(options[position], selection)
        holder.itemSelection.subscribe(itemClicks)
    }

    fun clear() {
        disposable.dispose()
    }
}