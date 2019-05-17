package com.tendebit.dungeonmaster.charactercreation3.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.tendebit.dungeonmaster.R
import com.tendebit.dungeonmaster.charactercreation3.CharacterCreation
import com.tendebit.dungeonmaster.charactercreation3.characterclass.ID_KEY
import com.tendebit.dungeonmaster.charactercreation3.viewmodel.CharacterCreationSectionsViewModel
import com.tendebit.dungeonmaster.charactercreation3.viewmodel.CharacterCreationViewModel
import com.tendebit.dungeonmaster.core.extensions.getViewModelManager
import com.tendebit.dungeonmaster.core.platform.ViewModels
import com.tendebit.dungeonmaster.core.view.LoadingDialog
import com.tendebit.dungeonmaster.core.viewmodel3.ViewModelFactory
import io.reactivex.disposables.Disposable

class CharacterCreationFragment : Fragment() {

	companion object {
		fun newInstance(viewModelId: Long?) = CharacterCreationFragment().apply { arguments = Bundle().apply { if (viewModelId != null) putLong(ID_KEY, viewModelId) } }
	}

	private class Factory : ViewModelFactory<CharacterCreationViewModel> {

		override fun createNew(): CharacterCreationViewModel {
			return CharacterCreationViewModel(CharacterCreation())
		}

	}

	var viewModel: CharacterCreationViewModel? = null

	private lateinit var adapter: CharacterCreationSectionsAdapter
	private lateinit var viewPager: ViewPager2
	private lateinit var buttonWrapper: ViewGroup
	private lateinit var backButton: Button
	private lateinit var forwardButton: Button
	private lateinit var loadingDialog: LoadingDialog
	private var pageChangeDisposable: Disposable? = null
	private var pagerChangeDisposable: Disposable? = null
	private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
		override fun onPageSelected(position: Int) {
			super.onPageSelected(position)
			viewModel?.sectionsViewModel?.scrolledToPage(position)
			val actions = viewModel?.sectionsViewModel?.getPageActions(position)
			// TODO: update buttons based on page actions
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val root = inflater.inflate(R.layout.fragment_generic_viewpager_2, container, false)
		viewPager = root.findViewById(R.id.view_pager)
		buttonWrapper = root.findViewById(R.id.button_wrapper)
		backButton = root.findViewById(R.id.button_back)
		forwardButton = root.findViewById(R.id.button_forward)
		loadingDialog = root.findViewById(R.id.loading_dialog)

		return root
	}

	override fun onResume() {
		super.onResume()
		val activity = activity ?: return
		if (viewModel == null) {
			val lookup = ViewModels.from(activity)?.findOrCreateViewModel(arguments?.get(ID_KEY) as? Long, Factory())
			lookup?.let {
				viewModel = lookup.viewModel
				arguments?.putLong(ID_KEY, lookup.id)
			}
		}

		viewModel?.sectionsViewModel?.let { sections ->
			adapter = CharacterCreationSectionsAdapter(this, sections, activity.getViewModelManager())
			viewPager.adapter = adapter
			pageChangeDisposable = sections.pageChanges.subscribe {
				if (it == viewPager.currentItem) {
					val actions = sections.getPageActions(it)
					// TODO: update buttons based on page actions
				}
			}
			pagerChangeDisposable = sections.changes.subscribe {
				updateFromViewModel(it)
			}
			updateFromViewModel(sections)
			viewPager.registerOnPageChangeCallback(pageChangeCallback)
		}

	}

	override fun onPause() {
		super.onPause()
		adapter.dispose()
		pageChangeDisposable?.dispose()
		pagerChangeDisposable?.dispose()
		viewPager.unregisterOnPageChangeCallback(pageChangeCallback)
	}

	private fun updateFromViewModel(viewModel: CharacterCreationSectionsViewModel) {
		loadingDialog.visibility = if (!viewModel.showLoading) View.GONE else View.VISIBLE
		viewPager.currentItem = viewModel.selectedPage
	}

}
