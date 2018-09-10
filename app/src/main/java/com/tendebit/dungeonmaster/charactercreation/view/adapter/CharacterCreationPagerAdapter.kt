package com.tendebit.dungeonmaster.charactercreation.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.view.ClassSelectionFragment
import com.tendebit.dungeonmaster.charactercreation.pages.confirmation.CharacterConfirmationFragment
import com.tendebit.dungeonmaster.charactercreation.pages.custominfoentry.view.CustomInfoEntryFragment
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.view.ProficiencySelectionFragment
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.view.RaceSelectionFragment
import com.tendebit.dungeonmaster.charactercreation.viewmodel.CharacterCreationPageCollection
import com.tendebit.dungeonmaster.charactercreation.viewmodel.CharacterCreationPageDescriptor

class CharacterCreationPagerAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {
    private var pageCollection = CharacterCreationPageCollection(ArrayList())

    override fun getItem(position: Int): Fragment {
        return getPageForDescriptor(pageCollection.pages[position])
    }

    override fun getCount(): Int {
        return pageCollection.size
    }

    fun update(updatedCollection: CharacterCreationPageCollection) {
        if (updatedCollection != pageCollection) {
            pageCollection = updatedCollection
            notifyDataSetChanged()
        }
    }

    private fun getPageForDescriptor(pageDescriptor: CharacterCreationPageDescriptor) : Fragment {
        return when(pageDescriptor.type) {
            CharacterCreationPageDescriptor.PageType.RACE_SELECTION -> RaceSelectionFragment()
            CharacterCreationPageDescriptor.PageType.CLASS_SELECTION -> ClassSelectionFragment()
            CharacterCreationPageDescriptor.PageType.PROFICIENCY_SELECTION -> ProficiencySelectionFragment.newInstance(pageDescriptor.indexInGroup)
            CharacterCreationPageDescriptor.PageType.CUSTOM_INFO -> CustomInfoEntryFragment()
            CharacterCreationPageDescriptor.PageType.CONFIRMATION -> CharacterConfirmationFragment()
        }
    }

    // TODO: expose functionality to block page interactions temporarily
}