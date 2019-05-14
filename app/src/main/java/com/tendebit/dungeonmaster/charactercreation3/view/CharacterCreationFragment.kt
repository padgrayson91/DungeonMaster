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
import com.tendebit.dungeonmaster.charactercreation3.viewmodel.CharacterCreationViewModel
import com.tendebit.dungeonmaster.core.extensions.getViewModelManager
import com.tendebit.dungeonmaster.core.view.LoadingDialog
import io.reactivex.disposables.Disposable

private const val KEY_CHARACTER_CREATION = "character_creation"

class CharacterCreationFragment : Fragment() {

	var viewModel: CharacterCreationViewModel? = null

	private lateinit var adapter: CharacterCreationSectionsAdapter
	private lateinit var viewPager: ViewPager2
	private lateinit var buttonWrapper: ViewGroup
	private lateinit var backButton: Button
	private lateinit var forwardButton: Button
	private lateinit var loadingDialog: LoadingDialog
	private var subscription: Disposable? = null
	private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
		override fun onPageSelected(position: Int) {
			super.onPageSelected(position)
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

		savedInstanceState?.getParcelable<CharacterCreation>(KEY_CHARACTER_CREATION)?.let {
			viewModel = CharacterCreationViewModel(it)
		}
		return root
	}

	override fun onResume() {
		super.onResume()
		val activity = activity ?: return
		if (viewModel == null) {
			val characterCreation = CharacterCreation()
			viewModel = CharacterCreationViewModel(characterCreation)
		}

		viewModel?.sectionsViewModel?.let { sections ->
			adapter = CharacterCreationSectionsAdapter(this, sections, activity.getViewModelManager())
			viewPager.adapter = adapter
			subscription = sections.pageChanges.subscribe {
				if (it == viewPager.currentItem) {
					val actions = sections.getPageActions(it)
					// TODO: update buttons based on page actions
				}
			}
			viewPager.registerOnPageChangeCallback(pageChangeCallback)
		}


	}

	override fun onPause() {
		super.onPause()
		adapter.dispose()
		subscription?.dispose()
		viewPager.unregisterOnPageChangeCallback(pageChangeCallback)
	}

	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		viewModel?.state?.let {
			outState.putParcelable(KEY_CHARACTER_CREATION, it)
		}
	}
}
