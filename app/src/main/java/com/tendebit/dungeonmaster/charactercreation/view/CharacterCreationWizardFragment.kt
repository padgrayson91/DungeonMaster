package com.tendebit.dungeonmaster.charactercreation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.tendebit.dungeonmaster.R
import com.tendebit.dungeonmaster.charactercreation.classselection.view.ClassSelectionFragment
import com.tendebit.dungeonmaster.charactercreation.proficiencyselection.view.ProficiencySelectionFragment
import com.tendebit.dungeonmaster.charactercreation.view.adapter.CharacterCreationPagerAdapter
import com.tendebit.dungeonmaster.charactercreation.viewmodel.CharacterCreationPageDescriptor
import com.tendebit.dungeonmaster.charactercreation.viewmodel.CharacterCreationState
import com.tendebit.dungeonmaster.charactercreation.view.statefragment.CharacterCreationStateFragment
import com.tendebit.dungeonmaster.charactercreation.view.statefragment.STATE_FRAGMENT_TAG
import com.tendebit.dungeonmaster.core.BackNavigationHandler
import io.reactivex.disposables.Disposable

class CharacterCreationWizardFragment: Fragment(), BackNavigationHandler {
    private lateinit var adapter: CharacterCreationPagerAdapter
    private lateinit var viewPager: ViewPager
    private lateinit var stateFragment: CharacterCreationStateFragment
    private lateinit var subscription: Disposable

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
        viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                stateFragment.onPageSelected(position)
            }
        })
        fragmentManager?.let {
            adapter = CharacterCreationPagerAdapter(it)
            viewPager.adapter = adapter
        }


        return root
    }

    override fun onResume() {
        super.onResume()
        subscription = stateFragment.stateChanges.subscribe({
            updatePages(it)
        })
    }

    override fun onBackPressed() : Boolean {
        if (viewPager.currentItem > 0) {
            viewPager.setCurrentItem(viewPager.currentItem - 1, true)
            return true
        }
        return false
    }

    private fun updatePages(creationState: CharacterCreationState) {
        adapter.removePage(creationState.availablePages.size)
        for (i in adapter.count until creationState.availablePages.size) {
            adapter.addPage(getPageForDescriptor(creationState.availablePages[i]))
        }
        // ... etc ...
        if (viewPager.currentItem != creationState.currentPage) {
            viewPager.setCurrentItem(creationState.currentPage, true)
        }
    }

    private fun getPageForDescriptor(pageDescriptor: CharacterCreationPageDescriptor) : Fragment {
        return when(pageDescriptor.type) {
            CharacterCreationPageDescriptor.PageType.CLASS_SELECTION -> ClassSelectionFragment()
            CharacterCreationPageDescriptor.PageType.PROFICIENCY_SELECTION -> ProficiencySelectionFragment()
        }
    }

    override fun onPause() {
        super.onPause()
        subscription.dispose()
    }
}