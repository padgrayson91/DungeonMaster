package com.tendebit.dungeonmastercore.view.adapter

import androidx.recyclerview.widget.RecyclerView
import com.tendebit.dungeonmastercore.viewmodel3.SingleSelectViewModel
import io.reactivex.disposables.Disposable

abstract class SingleSelectAdapter<T, V : RecyclerView.ViewHolder>(private val viewModel: SingleSelectViewModel<T>?) : RecyclerView.Adapter<V>() {

	private var mainDisposable: Disposable? = null
	private var childDisposable: Disposable? = null

	init {
		resume()
	}

	private fun subscribeToSelection(selection: SingleSelectViewModel<*>) {
		childDisposable?.dispose()
		childDisposable = selection.itemChanges.subscribe {
			notifyItemChanged(it)
		}
	}

	override fun getItemCount() = viewModel?.itemCount ?: 0

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
