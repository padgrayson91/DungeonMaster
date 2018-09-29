package com.tendebit.dungeonmaster.charactercreation.pages.raceselection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tendebit.dungeonmaster.R
import com.tendebit.dungeonmaster.charactercreation.CharacterCreationStateFragment
import com.tendebit.dungeonmaster.charactercreation.CharacterCreationViewModel.Companion.ARG_VIEW_MODEL_TAG
import com.tendebit.dungeonmaster.charactercreation.STATE_FRAGMENT_TAG
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.model.CharacterRaceDirectory
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.model.CharacterRaceInfoSupplier
import com.tendebit.dungeonmaster.core.model.DnDDatabase
import com.tendebit.dungeonmaster.core.view.adapter.SelectionElementAdapter
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

/**
 * UI Fragment for character race selection
 */
class RaceSelectionFragment : Fragment() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: SelectionElementAdapter<CharacterRaceDirectory, CharacterRaceDirectory>
    private val viewModel: RaceSelectionViewModel by inject("newOrExisting") { parametersOf(this) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_generic_list, container, false)
        recycler = root.findViewById(R.id.item_list)
        recycler.layoutManager = LinearLayoutManager(activity)
        return root
    }


    override fun onResume() {
        super.onResume()
        adapter = SelectionElementAdapter(viewModel)
        recycler.adapter = adapter
    }

    override fun onPause() {
        super.onPause()
        adapter.clear()
    }
}