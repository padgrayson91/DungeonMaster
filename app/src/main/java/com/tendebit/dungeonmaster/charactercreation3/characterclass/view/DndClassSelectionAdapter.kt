package com.tendebit.dungeonmaster.charactercreation3.characterclass.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClass
import com.tendebit.dungeonmastercore.viewmodel3.SingleSelectViewModel
import io.reactivex.disposables.Disposable

class DndClassSelectionAdapter(private val viewModel: SingleSelectViewModel<DndCharacterClass>?) : RecyclerView.Adapter<DndClassViewHolder>() {

	private var mainDisposable: Disposable? = null
	private var childDisposable: Disposable? = null

	init {
		resume()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DndClassViewHolder {
		return DndClassViewHolder(LayoutInflater.from(parent.context), parent)
	}

	private fun subscribeToSelection(selection: SingleSelectViewModel<*>) {
		childDisposable?.dispose()
		childDisposable = selection.itemChanges.subscribe {
			notifyItemChanged(it)
		}
	}

	override fun getItemCount() = viewModel?.itemCount ?: 0

	override fun onBindViewHolder(holder: DndClassViewHolder, position: Int) {
		holder.populate(viewModel!!.children[position])
	}

	fun clear() {
		mainDisposable?.dispose()
		childDisposable?.dispose()
	}

	fun resume() {
		if (viewModel != null) {
			mainDisposable = viewModel.changes.subscribe {
				subscribeToSelection(it)
				notifyDataSetChanged()
			}
		}
	}

}
