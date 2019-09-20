package com.tendebit.dungeonmaster.charactercreation3.ability.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tendebit.dungeonmaster.charactercreation3.ability.viewmodel.DndAbilitySelectionViewModel
import io.reactivex.disposables.Disposable

class DndAbilitySlotAdapter(private val viewModel: DndAbilitySelectionViewModel?) : RecyclerView.Adapter<DndAbilitySlotViewHolder>() {

	private var mainDisposable: Disposable? = null
	private var childDisposable: Disposable? = null

	init {
		resume()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DndAbilitySlotViewHolder {
		return DndAbilitySlotViewHolder(LayoutInflater.from(parent.context), parent)
	}

	override fun getItemCount(): Int = viewModel?.children?.size ?: 0

	override fun onBindViewHolder(holder: DndAbilitySlotViewHolder, position: Int) {
		holder.populate(viewModel!!.children[position])
	}

	private fun subscribeToSelection(selection: DndAbilitySelectionViewModel) {
		childDisposable?.dispose()
		childDisposable = selection.abilitySlotChanges.subscribe {
			notifyItemChanged(it)
		}
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
			subscribeToSelection(viewModel)
		}
	}

}
