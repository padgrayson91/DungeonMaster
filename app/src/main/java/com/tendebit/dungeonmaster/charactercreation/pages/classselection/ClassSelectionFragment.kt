package com.tendebit.dungeonmaster.charactercreation.pages.classselection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tendebit.dungeonmaster.R
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassDirectory
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassInfo
import com.tendebit.dungeonmaster.core.view.adapter.SelectionElementAdapter
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

/**
 * UI fragment for character class selection
 */
class ClassSelectionFragment : Fragment() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: SelectionElementAdapter<CharacterClassDirectory, CharacterClassInfo>
    private val viewModel: ClassSelectionViewModel by inject("newOrExisting") { parametersOf(this) }

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