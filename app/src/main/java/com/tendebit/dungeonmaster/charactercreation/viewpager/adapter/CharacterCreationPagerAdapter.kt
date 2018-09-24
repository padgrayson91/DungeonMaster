package com.tendebit.dungeonmaster.charactercreation.viewpager.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.tendebit.dungeonmaster.charactercreation.CharacterCreationViewModel
import com.tendebit.dungeonmaster.charactercreation.pages.characterlist.CharacterListFragment
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.ClassSelectionFragment
import com.tendebit.dungeonmaster.charactercreation.pages.confirmation.CharacterConfirmationFragment
import com.tendebit.dungeonmaster.charactercreation.pages.custominfoentry.CustomInfoEntryFragment
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.ProficiencySelectionFragment
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.RaceSelectionFragment
import com.tendebit.dungeonmaster.charactercreation.viewpager.CharacterCreationPageDescriptor

class CharacterCreationPagerAdapter(fragmentManager: FragmentManager, private val viewModel: CharacterCreationViewModel) : FragmentStatePagerAdapter(fragmentManager) {
    private var pageCollection = CharacterCreationPageCollection(ArrayList())

    override fun getItem(position: Int): Fragment {
        return getPageForDescriptor(pageCollection.pages[position])
    }

    override fun getCount(): Int {
        return pageCollection.size
    }

    override fun getItemPosition(`object`: Any): Int {
        val descriptor = getDescriptorForPage(`object` as Fragment)
        // if the page is not available, or if the page no longer has a viewmodel, let the adapter know it must be created
        return if (!pageCollection.pages.contains(descriptor) || viewModel.getChildViewModel<Any>(descriptor?.viewModelTag ?: "") == null) {
            PagerAdapter.POSITION_NONE
        } else {
            pageCollection.pages.indexOf(descriptor)
        }
    }

    fun update(updatedCollection: CharacterCreationPageCollection) {
        if (updatedCollection != pageCollection) {
            pageCollection = updatedCollection
            notifyDataSetChanged()
        }
    }

    private fun getPageForDescriptor(pageDescriptor: CharacterCreationPageDescriptor) : Fragment {
        val fragment = when(pageDescriptor.type) {
            CharacterCreationPageDescriptor.PageType.CHARACTER_LIST -> CharacterListFragment()
            CharacterCreationPageDescriptor.PageType.RACE_SELECTION -> RaceSelectionFragment()
            CharacterCreationPageDescriptor.PageType.CLASS_SELECTION -> ClassSelectionFragment()
            CharacterCreationPageDescriptor.PageType.PROFICIENCY_SELECTION -> ProficiencySelectionFragment.newInstance(pageDescriptor.indexInGroup)
            CharacterCreationPageDescriptor.PageType.CUSTOM_INFO -> CustomInfoEntryFragment()
            CharacterCreationPageDescriptor.PageType.CONFIRMATION -> CharacterConfirmationFragment()
        }
        val args = fragment.arguments ?: Bundle()
        args.putString(CharacterCreationViewModel.ARG_VIEW_MODEL_TAG, pageDescriptor.viewModelTag)
        fragment.arguments = args
        return fragment
    }

    private fun getDescriptorForPage(page: Fragment): CharacterCreationPageDescriptor? {
        val viewModelTag = page.arguments!![CharacterCreationViewModel.ARG_VIEW_MODEL_TAG] as String
        return when (page) {
            is CharacterListFragment -> CharacterCreationPageDescriptor(CharacterCreationPageDescriptor.PageType.CHARACTER_LIST,
                    emptyList(), viewModelTag = viewModelTag)
            is RaceSelectionFragment -> CharacterCreationPageDescriptor(CharacterCreationPageDescriptor.PageType.RACE_SELECTION,
                    emptyList(), viewModelTag = viewModelTag)
            is ClassSelectionFragment -> CharacterCreationPageDescriptor(CharacterCreationPageDescriptor.PageType.CLASS_SELECTION,
                    emptyList(), viewModelTag = viewModelTag)
            is ProficiencySelectionFragment -> CharacterCreationPageDescriptor(CharacterCreationPageDescriptor.PageType.PROFICIENCY_SELECTION, emptyList(), page.arguments!![ProficiencySelectionFragment.KEY_PAGE_ID]!! as Int, viewModelTag = viewModelTag)
            is CustomInfoEntryFragment -> CharacterCreationPageDescriptor(CharacterCreationPageDescriptor.PageType.CUSTOM_INFO,
                    emptyList(), viewModelTag = viewModelTag)
            is CharacterConfirmationFragment -> CharacterCreationPageDescriptor(CharacterCreationPageDescriptor.PageType.CONFIRMATION,
                    emptyList(), viewModelTag = viewModelTag)
            else -> null
        }
    }

}