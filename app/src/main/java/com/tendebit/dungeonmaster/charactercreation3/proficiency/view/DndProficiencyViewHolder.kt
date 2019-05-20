package com.tendebit.dungeonmaster.charactercreation3.proficiency.view

import android.content.Context
import android.view.View
import com.google.android.material.chip.Chip
import com.tendebit.dungeonmaster.core.viewmodel3.CheckableViewModel
import io.reactivex.disposables.Disposable

class DndProficiencyViewHolder(val context: Context?, private val viewModel: CheckableViewModel) {

	private var view: Chip? = null
	private var disposable: Disposable? = null

	fun getView(): View = view ?: createView()

	private fun createView(): View {
		val createdView = Chip(context)
		createdView.isCheckable = true
		createdView.setOnCheckedChangeListener { _, checked -> viewModel.changeSelection(checked) }
		bindView(createdView)

		view = createdView
		return createdView
	}

	private fun bindView(createdView: Chip) {
		createdView.isChecked = viewModel.checked
		createdView.isEnabled = viewModel.enabled
		createdView.text = viewModel.text

		disposable?.dispose()
		disposable = viewModel.changes.subscribe {
			bindView(createdView)
		}
	}

}