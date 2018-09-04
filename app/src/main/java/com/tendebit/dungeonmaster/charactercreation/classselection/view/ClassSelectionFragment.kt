package com.tendebit.dungeonmaster.charactercreation.classselection.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tendebit.dungeonmaster.R
import com.tendebit.dungeonmaster.charactercreation.classselection.view.adapter.CharacterClassAdapter
import com.tendebit.dungeonmaster.charactercreation.classselection.viewmodel.CharacterClassSelectionState
import com.tendebit.dungeonmaster.charactercreation.classselection.viewmodel.statefragment.CLASS_SELECTION_FRAGMENT_TAG
import com.tendebit.dungeonmaster.charactercreation.classselection.viewmodel.statefragment.ClassSelectionStateFragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


class ClassSelectionFragment : Fragment() {

    private var subscriptions: CompositeDisposable? = null
    private var adapterSubscription: Disposable? = null
    private lateinit var recycler: RecyclerView
    private lateinit var stateProvider: ClassSelectionStateFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_generic_list, container, false)
        recycler = root.findViewById(R.id.class_list)
        recycler.layoutManager = LinearLayoutManager(activity)

        return root
    }


    override fun onResume() {
        super.onResume()
        pageEnter()
    }

    override fun onPause() {
        super.onPause()
        pageExit()
    }

    fun pageEnter() {
        subscriptions = CompositeDisposable()
        val addedFragment = fragmentManager?.findFragmentByTag(CLASS_SELECTION_FRAGMENT_TAG)
        if (addedFragment is ClassSelectionStateFragment) {
            stateProvider = addedFragment
        } else {
            stateProvider = ClassSelectionStateFragment()
            fragmentManager?.beginTransaction()
                    ?.add(stateProvider, CLASS_SELECTION_FRAGMENT_TAG)
                    ?.commit()
        }
        subscriptions?.add(stateProvider.stateChanges.subscribe({updateViewFromState(it)}))
    }

    fun pageExit() {
        subscriptions?.dispose()
    }


    private fun updateViewFromState(state: CharacterClassSelectionState) {
        if (state.characterClassOptions.size > 0) {
            adapterSubscription?.dispose()
            val adapter = CharacterClassAdapter(state.characterClassOptions)
            adapterSubscription = adapter.itemClicks.subscribe({stateProvider.onClassSelected(it)})
            recycler.adapter = adapter
        }
    }
}