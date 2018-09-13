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
    private var requiresFullRefresh = false

    override fun getItem(position: Int): Fragment {
        requiresFullRefresh = false
        return getPageForDescriptor(pageCollection.pages[position])
    }

    override fun getCount(): Int {
        return pageCollection.size
    }

    override fun getItemPosition(`object`: Any): Int {
        return if (requiresFullRefresh) {
            PagerAdapter.POSITION_NONE
        } else {
            super.getItemPosition(`object`)
        }
    }

    fun update(updatedCollection: CharacterCreationPageCollection, currentPosition: Int) {
        if (updatedCollection != pageCollection) {
            if (pageCollection.findFirstDifferingIndex(updatedCollection) <= currentPosition + 1) {
                requiresFullRefresh = true
            }
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

    // TODO: expose functionality to block page interactions temporarily
}