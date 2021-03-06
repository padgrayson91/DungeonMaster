package com.tendebit.dungeonmaster.charactercreation3.race.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tendebit.dungeonmaster.R
import com.tendebit.dungeonmaster.charactercreation3.ID_KEY
import com.tendebit.dungeonmaster.charactercreation3.race.DndRace
import com.tendebit.dungeonmastercore.platform.ViewModels
import com.tendebit.dungeonmastercore.viewmodel3.SingleSelectViewModel
import com.tendebit.uicore.view.LoadingDialog
import io.reactivex.disposables.Disposable

class DndRaceSelectionFragment : Fragment() {

	companion object {
		fun newInstance(viewModelId: Long) = DndRaceSelectionFragment().apply { arguments = Bundle().apply { putLong(ID_KEY, viewModelId) } }
	}

	private lateinit var recycler: RecyclerView
	private lateinit var loadingDialog: LoadingDialog
	private var disposable: Disposable? = null
	private var adapter: DndRaceSelectionAdapter? = null
	private var viewModel: SingleSelectViewModel<DndRace>? = null

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val root = inflater.inflate(R.layout.fragment_generic_list, container, false)
		recycler = root.findViewById(R.id.item_list)
		recycler.layoutManager = LinearLayoutManager(activity)
		loadingDialog = root.findViewById(R.id.loading_dialog)
		return root
	}

	override fun onResume() {
		super.onResume()
		val oldViewModel = viewModel
		viewModel = ViewModels.from(activity)?.findViewModel(arguments?.getLong(ID_KEY))
		if (viewModel != oldViewModel) {
			onAttachViewModel(viewModel)
		}
		adapter?.resume()
		recycler.adapter = adapter
		disposable = viewModel?.changes?.subscribe { onViewModelChanged(it) }
		onViewModelChanged(viewModel)
	}

	override fun onPause() {
		super.onPause()
		adapter?.clear()
		disposable?.dispose()
	}

	private fun onAttachViewModel(viewModel: SingleSelectViewModel<DndRace>?) {
		disposable?.dispose()
		adapter?.clear()
		adapter = DndRaceSelectionAdapter(viewModel)
	}

	private fun onViewModelChanged(viewModel: SingleSelectViewModel<DndRace>?) {
		loadingDialog.visibility = if (viewModel?.showLoading == false) View.GONE else View.VISIBLE
	}

}
