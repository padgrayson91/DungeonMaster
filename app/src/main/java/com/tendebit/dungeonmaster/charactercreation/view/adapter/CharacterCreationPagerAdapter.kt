package com.tendebit.dungeonmaster.charactercreation.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import java.util.*

class CharacterCreationPagerAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {
    private val pages = LinkedList<Fragment>()

    override fun getItem(position: Int): Fragment {
        return pages[position]
    }

    override fun getCount(): Int {
        return pages.size
    }

    fun addPage(page: Fragment) {
        pages.add(page)
        notifyDataSetChanged()
    }

    fun removePagesAfter(position: Int) {
        if (position >= pages.size) return
        pages.subList(position, pages.size).clear()
        notifyDataSetChanged()
    }

    // TODO: expose functionality to block page interactions temporarily
}