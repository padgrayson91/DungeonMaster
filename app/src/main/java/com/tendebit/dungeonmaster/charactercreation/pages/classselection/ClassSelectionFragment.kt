package com.tendebit.dungeonmaster.charactercreation.pages.classselection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tendebit.dungeonmaster.App
import com.tendebit.dungeonmaster.R
import com.tendebit.dungeonmaster.charactercreation.CharacterCreationStateFragment
import com.tendebit.dungeonmaster.charactercreation.CharacterCreationViewModel
import com.tendebit.dungeonmaster.charactercreation.STATE_FRAGMENT_TAG
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassDirectory
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassInfo
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassInfoSupplier
import com.tendebit.dungeonmaster.core.model.DnDDatabase
import com.tendebit.dungeonmaster.core.view.adapter.SelectionElementAdapter

/**
 * UI fragment for character class selection
 */
class ClassSelectionFragment : Fragment() {

    private lateinit var recycler: RecyclerView
    private lateinit var stateProvider: CharacterCreationStateFragment
    private lateinit var adapter: SelectionElementAdapter<CharacterClassDirectory, CharacterClassInfo>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_generic_list, container, false)
        recycler = root.findViewById(R.id.item_list)
        recycler.layoutManager = LinearLayoutManager(activity)
        return root
    }


    override fun onResume() {
        super.onResume()
        val addedFragment = activity?.supportFragmentManager?.findFragmentByTag(STATE_FRAGMENT_TAG)
        if (addedFragment is CharacterCreationStateFragment) {
            stateProvider = addedFragment
            val viewModelTag = arguments!![CharacterCreationViewModel.ARG_VIEW_MODEL_TAG] as String
            var viewModel = stateProvider.viewModel
                    .getChildViewModel<ClassSelectionViewModel>(viewModelTag)
            if (viewModel == null) {
                viewModel = ClassSelectionViewModel(CharacterClassInfoSupplier.Impl(
                        DnDDatabase.getInstance(App.instance.applicationContext).responseDao()))
                stateProvider.viewModel.addClassSelection(viewModelTag, viewModel)
            }
            adapter = SelectionElementAdapter(viewModel)
            recycler.adapter = adapter
        } else {
            throw IllegalStateException(ClassSelectionFragment::class.java.simpleName + " expects a state manager to be provided")
        }
    }

    override fun onPause() {
        super.onPause()
        adapter.clear()
    }

}