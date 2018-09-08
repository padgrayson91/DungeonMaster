package com.tendebit.dungeonmaster.charactercreation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.tendebit.dungeonmaster.R
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.view.ClassSelectionFragment
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.view.ProficiencySelectionFragment
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.view.RaceSelectionFragment
import com.tendebit.dungeonmaster.charactercreation.view.adapter.CharacterCreationPagerAdapter
import com.tendebit.dungeonmaster.charactercreation.view.statefragment.CharacterCreationStateFragment
import com.tendebit.dungeonmaster.charactercreation.view.statefragment.STATE_FRAGMENT_TAG
import com.tendebit.dungeonmaster.charactercreation.viewmodel.CharacterCreationPageDescriptor
import com.tendebit.dungeonmaster.charactercreation.viewmodel.CharacterCreationState
import com.tendebit.dungeonmaster.core.view.BackNavigationHandler
import com.tendebit.dungeonmaster.core.view.LoadingDialog
import io.reactivex.disposables.Disposable
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
    private lateinit var subscription: Disposable
    private var configured = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_generic_viewpager, container, false)
        val addedFragment = fragmentManager?.findFragmentByTag(STATE_FRAGMENT_TAG) as? CharacterCreationStateFragment
        if (addedFragment == null) {
            stateFragment = CharacterCreationStateFragment()
            fragmentManager?.beginTransaction()
                    ?.add(stateFragment, STATE_FRAGMENT_TAG)
                    ?.commit()
        } else {
            stateFragment = addedFragment
        }

        viewPager = root.findViewById(R.id.view_pager)
        backButton = root.findViewById(R.id.button_back)
        forwardButton = root.findViewById(R.id.button_forward)
        loadingDialog = root.findViewById(R.id.loading_dialog)
        backButton.setOnClickListener { viewPager.setCurrentItem(viewPager.currentItem - 1, true) }
        forwardButton.setOnClickListener { viewPager.setCurrentItem(viewPager.currentItem + 1, true) }
        viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                stateFragment.onPageSelected(position)
                backButton.visibility = if (position == 0) View.GONE else View.VISIBLE
                forwardButton.visibility = if (position < adapter.count -1 ) View.VISIBLE else View.GONE

            }
        })
        // TODO: should use child fragment manager because using the parent leads to bugs
        fragmentManager?.let {
            adapter = CharacterCreationPagerAdapter(it)
            viewPager.adapter = adapter
        }


        return root
    }

    override fun onResume() {
        super.onResume()
        subscription = stateFragment.stateChanges.subscribe{
            updateViewFromState(it)
        }
    }

    override fun onBackPressed() : Boolean {
        if (viewPager.currentItem > 0) {
            viewPager.setCurrentItem(viewPager.currentItem - 1, true)
            return true
        }
        return false
    }

    private fun updateViewFromState(creationState: CharacterCreationState) {
        adapter.removePagesAfter(creationState.availablePages.size)
        for (i in adapter.count until creationState.availablePages.size) {
            adapter.addPage(getPageForDescriptor(creationState.availablePages[i]))
        }
        // ... etc ...
        if (viewPager.currentItem != creationState.currentPage) {
            val previouslyConfigured = configured
            launch(UI) {
                if (previouslyConfigured) withContext(DefaultDispatcher) { Thread.sleep(200) }
                viewPager.setCurrentItem(creationState.currentPage, true)
            }
        }
        loadingDialog.visibility = if(creationState.isLoading) View.VISIBLE else View.GONE
        configured = true
    }

    private fun getPageForDescriptor(pageDescriptor: CharacterCreationPageDescriptor) : Fragment {
        return when(pageDescriptor.type) {
            CharacterCreationPageDescriptor.PageType.RACE_SELECTION -> RaceSelectionFragment()
            CharacterCreationPageDescriptor.PageType.CLASS_SELECTION -> ClassSelectionFragment()
            CharacterCreationPageDescriptor.PageType.PROFICIENCY_SELECTION -> ProficiencySelectionFragment.newInstance(pageDescriptor.indexInGroup)
        }
    }

    override fun onPause() {
        super.onPause()
        subscription.dispose()
    }
}