package com.tendebit.dungeonmaster.core.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tendebit.dungeonmaster.core.view.SelectableCardViewHolder
import com.tendebit.dungeonmaster.core.viewmodel.SelectableElement
import com.tendebit.dungeonmaster.core.viewmodel.SelectionViewModel
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class SelectionElementAdapter<T : SelectableElement, SelectedType : SelectableElement>(val viewModel: SelectionViewModel<T, SelectedType>) : RecyclerView.Adapter<SelectableCardViewHolder<T>>() {
    private val disposable = CompositeDisposable()
    private val options = ArrayList<T>()
    private var selection: SelectedType? = null

    init {
        disposable.add(viewModel.options.subscribe { updateOptions(it) })
        disposable.add(viewModel.selection.subscribe { updateSelection(it) })
    }

    private fun updateOptions(options: List<T>) {
        this.options.clear()
        this.options.addAll(options)
        launch(UI) { notifyDataSetChanged() }
    }

    private fun updateSelection(selection: SelectedType?) {
        this.selection = selection
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectableCardViewHolder<T> {
        return SelectableCardViewHolder(LayoutInflater.from(parent.context), parent)
    }

    override fun getItemCount(): Int {
        return options.size
    }

    override fun onBindViewHolder(holder: SelectableCardViewHolder<T>, position: Int) {
        holder.populate(options[position], selection)
        disposable.add(holder.itemActions.subscribe {viewModel.performActions(it.first, it.second)})
    }

    fun clear() {
        disposable.dispose()
    }
}