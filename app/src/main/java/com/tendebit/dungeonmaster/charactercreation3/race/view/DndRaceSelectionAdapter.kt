package com.tendebit.dungeonmaster.charactercreation3.race.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tendebit.dungeonmaster.charactercreation3.race.DndRace
import com.tendebit.dungeonmaster.charactercreation3.race.viewmodel.DndRaceSelectionViewModel
import com.tendebit.dungeonmaster.core.viewmodel3.SingleSelectViewModel
import io.reactivex.disposables.Disposable

class DndRaceSelectionAdapter(private val viewModel: SingleSelectViewModel<DndRace>?) : RecyclerView.Adapter<DndRaceViewHolder>() {

	private var mainDisposable: Disposable? = null
	private var childDisposable: Disposable? = null

	init {
		resume()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DndRaceViewHolder {
		return DndRaceViewHolder(LayoutInflater.from(parent.context), parent)
	}

	private fun subscribeToSelection(selection: SingleSelectViewModel<*>) {
		childDisposable?.dispose()
		childDisposable = selection.itemChanges.subscribe {
			notifyItemChanged(it)
		}

	}

	override fun getItemCount() = viewModel?.itemCount ?: 0

	override fun onBindViewHolder(holder: DndRaceViewHolder, position: Int) {
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
