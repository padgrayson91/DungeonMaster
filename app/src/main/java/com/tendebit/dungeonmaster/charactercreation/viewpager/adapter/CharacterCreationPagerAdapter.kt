package com.tendebit.dungeonmaster.charactercreation.viewpager.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.tendebit.dungeonmaster.charactercreation.pages.characterlist.CharacterListFragment
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.ClassSelectionFragment
import com.tendebit.dungeonmaster.charactercreation.pages.confirmation.CharacterConfirmationFragment
import com.tendebit.dungeonmaster.charactercreation.pages.custominfoentry.CustomInfoEntryFragment
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.ProficiencySelectionFragment
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.RaceSelectionFragment
import com.tendebit.dungeonmaster.charactercreation.viewpager.CharacterCreationPageDescriptor

class CharacterCreationPagerAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {
    private var pageCollection = CharacterCreationPageCollection(ArrayList())

    override fun getItem(position: Int): Fragment {
        return getPageForDescriptor(pageCollection.pages[position])
    }

    override fun getCount(): Int {
        return pageCollection.size
    }

    override fun getItemPosition(`object`: Any): Int {
        val descriptor = getDescriptorForPage(`object`)
        return if (!pageCollection.pages.contains(descriptor)) {
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
        return when(pageDescriptor.type) {
            CharacterCreationPageDescriptor.PageType.CHARACTER_LIST -> CharacterListFragment()
            CharacterCreationPageDescriptor.PageType.RACE_SELECTION -> RaceSelectionFragment()
            CharacterCreationPageDescriptor.PageType.CLASS_SELECTION -> ClassSelectionFragment()
            CharacterCreationPageDescriptor.PageType.PROFICIENCY_SELECTION -> ProficiencySelectionFragment.newInstance(pageDescriptor.indexInGroup)
            CharacterCreationPageDescriptor.PageType.CUSTOM_INFO -> CustomInfoEntryFragment()
            CharacterCreationPageDescriptor.PageType.CONFIRMATION -> CharacterConfirmationFragment()
        }
    }

    private fun getDescriptorForPage(page: Any): CharacterCreationPageDescriptor? {
        return when (page) {
            is CharacterListFragment -> CharacterCreationPageDescriptor(CharacterCreationPageDescriptor.PageType.CHARACTER_LIST, emptyList())
            is RaceSelectionFragment -> CharacterCreationPageDescriptor(CharacterCreationPageDescriptor.PageType.RACE_SELECTION, emptyList())
            is ClassSelectionFragment -> CharacterCreationPageDescriptor(CharacterCreationPageDescriptor.PageType.CLASS_SELECTION, emptyList())
            is ProficiencySelectionFragment -> CharacterCreationPageDescriptor(CharacterCreationPageDescriptor.PageType.PROFICIENCY_SELECTION, emptyList(), page.arguments!![ProficiencySelectionFragment.KEY_PAGE_ID]!! as Int)
            is CustomInfoEntryFragment -> CharacterCreationPageDescriptor(CharacterCreationPageDescriptor.PageType.CUSTOM_INFO, emptyList())
            is CharacterConfirmationFragment -> CharacterCreationPageDescriptor(CharacterCreationPageDescriptor.PageType.CONFIRMATION, emptyList())
            else -> null
        }
    }

}