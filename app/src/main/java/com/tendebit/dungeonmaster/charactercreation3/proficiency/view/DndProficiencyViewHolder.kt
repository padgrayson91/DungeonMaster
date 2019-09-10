package com.tendebit.dungeonmaster.charactercreation3.proficiency.view

import android.content.Context
import android.view.View
import com.google.android.material.chip.Chip
import com.tendebit.dungeonmaster.charactercreation3.proficiencycore.logger
import com.tendebit.dungeonmastercore.viewmodel3.CheckableViewModel
import io.reactivex.disposables.Disposable
import java.lang.ref.WeakReference

class DndProficiencyViewHolder(val context: Context?, private val viewModel: CheckableViewModel) {

	private var view: WeakReference<Chip>? = null
	private var disposable: Disposable? = null

	fun getView(): View = view?.get() ?: createView()

	private fun createView(): View {
		logger.writeDebug("Creating view for $viewModel")
		val createdView = Chip(context)
		createdView.isCheckable = true
		createdView.setOnCheckedChangeListener { _, checked -> viewModel.changeSelection(checked) }
		bindView(createdView)
		disposable?.dispose()
		disposable = viewModel.changes.subscribe { bindView(createdView) }

		view = WeakReference(createdView)
		return createdView
	}

	private fun bindView(createdView: Chip) {
		logger.writeDebug("Binding view for $viewModel")
		createdView.isChecked = viewModel.checked
		createdView.isEnabled = viewModel.enabled
		createdView.text = viewModel.text
	}

}
