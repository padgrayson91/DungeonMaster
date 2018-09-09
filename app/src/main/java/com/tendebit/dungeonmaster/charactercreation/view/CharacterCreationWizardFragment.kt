package com.tendebit.dungeonmaster.charactercreation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.tendebit.dungeonmaster.R
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.view.statefragment.CLASS_SELECTION_FRAGMENT_TAG
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.view.statefragment.ClassSelectionStateFragment
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.view.statefragment.PROFICIENCY_SELECTION_FRAGMENT_TAG
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.view.statefragment.ProficiencySelectionStateFragment
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.view.statefragment.RACE_SELECTION_FRAGMENT_TAG
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.view.statefragment.RaceSelectionStateFragment
import com.tendebit.dungeonmaster.charactercreation.view.adapter.CharacterCreationPagerAdapter
import com.tendebit.dungeonmaster.charactercreation.view.statefragment.CharacterCreationStateFragment
import com.tendebit.dungeonmaster.charactercreation.view.statefragment.STATE_FRAGMENT_TAG
import com.tendebit.dungeonmaster.charactercreation.viewmodel.CharacterCreationState
import com.tendebit.dungeonmaster.core.view.BackNavigationHandler
import com.tendebit.dungeonmaster.core.view.LoadingDialog
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext

class CharacterCreationWizardFragment: Fragment(), BackNavigationHandler {
    private lateinit var adapter: CharacterCreationPagerAdapter
    private lateinit var viewPager: ViewPager
    private lateinit var backButton: Button
    private lateinit var forwardButton: Button
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var stateFragment: CharacterCreationStateFragment
    private lateinit var subscription: CompositeDisposable
    private var configured = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_generic_viewpager, container, false)
        addStateManagers()
        initializeViews(root)


        return root
    }

    private fun addStateManagers() {
        stateFragment = addFragmentIfMissing(CharacterCreationStateFragment(), STATE_FRAGMENT_TAG)
        addFragmentIfMissing(RaceSelectionStateFragment(), RACE_SELECTION_FRAGMENT_TAG)
        addFragmentIfMissing(ClassSelectionStateFragment(), CLASS_SELECTION_FRAGMENT_TAG)
        addFragmentIfMissing(ProficiencySelectionStateFragment(), PROFICIENCY_SELECTION_FRAGMENT_TAG)
    }

    private fun initializeViews(root: View) {
        viewPager = root.findViewById(R.id.view_pager)
        backButton = root.findViewById(R.id.button_back)
        forwardButton = root.findViewById(R.id.button_forward)
        loadingDialog = root.findViewById(R.id.loading_dialog)
        backButton.setOnClickListener { viewPager.setCurrentItem(viewPager.currentItem - 1, true) }
        forwardButton.setOnClickListener { viewPager.setCurrentItem(viewPager.currentItem + 1, true) }
        viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                stateFragment.state.onPageSelected(position)

            }
        })
        adapter = CharacterCreationPagerAdapter(childFragmentManager)
        viewPager.adapter = adapter
    }

    private fun <T:Fragment> addFragmentIfMissing(fragment: T, tag: String) : T {
        @Suppress("UNCHECKED_CAST")
        val addedFragment = fragmentManager?.findFragmentByTag(tag) as? T
        if (addedFragment == null) {
            fragmentManager?.beginTransaction()
                    ?.add(fragment, tag)
                    ?.commit()
            return fragment
        }
        return addedFragment
    }

    override fun onResume() {
        super.onResume()
        subscription = CompositeDisposable()
        subscription.addAll(stateFragment.state.changes.subscribe{ updateViewFromState(it) })
    }

    override fun onBackPressed() : Boolean {
        if (viewPager.currentItem > 0) {
            viewPager.setCurrentItem(viewPager.currentItem - 1, true)
            return true
        }
        return false
    }

    private fun updateViewFromState(creationState: CharacterCreationState) {
        adapter.update(creationState.pageCollection)
        // ... etc ...
        if (viewPager.currentItem != creationState.currentPage) {
            val previouslyConfigured = configured
            launch(UI) {
                if (previouslyConfigured) withContext(DefaultDispatcher) { Thread.sleep(200) }
                viewPager.setCurrentItem(creationState.currentPage, true)
            }
        }

        backButton.isEnabled = creationState.currentPage != 0
        forwardButton.isEnabled = creationState.currentPage < creationState.pageCollection.size -1
        // For now, block the whole UI while anything is loading, but in the future
        // the user should still be allowed to interact
        loadingDialog.visibility = if(creationState.isLoading) View.VISIBLE else View.GONE
        configured = true
    }

    override fun onPause() {
        super.onPause()
        subscription.dispose()
    }
}