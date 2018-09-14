package com.tendebit.dungeonmaster.charactercreation.viewpager

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.tendebit.dungeonmaster.R
import com.tendebit.dungeonmaster.charactercreation.CharacterCreationStateFragment
import com.tendebit.dungeonmaster.charactercreation.CharacterCreationViewModel
import com.tendebit.dungeonmaster.charactercreation.STATE_FRAGMENT_TAG
import com.tendebit.dungeonmaster.charactercreation.viewpager.adapter.CharacterCreationPageCollection
import com.tendebit.dungeonmaster.charactercreation.viewpager.adapter.CharacterCreationPagerAdapter
import com.tendebit.dungeonmaster.core.model.DnDDatabase
import com.tendebit.dungeonmaster.core.view.BackNavigationHandler
import com.tendebit.dungeonmaster.core.view.LoadingDialog
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext


class CharacterCreationPagesFragment: Fragment(), BackNavigationHandler {
    private lateinit var adapter: CharacterCreationPagerAdapter
    private lateinit var viewPager: ViewPager
    private lateinit var buttonWrapper: ViewGroup
    private lateinit var backButton: Button
    private lateinit var forwardButton: Button
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var stateFragment: CharacterCreationStateFragment
    private lateinit var subscription: CompositeDisposable
    private var configured = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_generic_viewpager, container, false)
        stateFragment = addFragmentIfMissing(CharacterCreationStateFragment(), STATE_FRAGMENT_TAG)
        initializeViews(root)


        return root
    }

    private fun initializeViews(root: View) {
        viewPager = root.findViewById(R.id.view_pager)
        buttonWrapper = root.findViewById(R.id.button_wrapper)
        backButton = root.findViewById(R.id.button_back)
        forwardButton = root.findViewById(R.id.button_forward)
        loadingDialog = root.findViewById(R.id.loading_dialog)
        backButton.setOnClickListener { onBackPressed() }
        viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (stateFragment.viewModel.pagesViewModel.pageCollection.currentPageIndex != position) {
                    // hide the soft keyboard
                    val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(view?.windowToken, 0)
                    stateFragment.viewModel.pagesViewModel.onPageSelected(position)
                }
            }
        })
        adapter = CharacterCreationPagerAdapter(childFragmentManager)
        viewPager.adapter = adapter
    }

    private fun <T:Fragment> addFragmentIfMissing(fragment: T, tag: String) : T {
        @Suppress("UNCHECKED_CAST")
        val addedFragment = fragmentManager?.findFragmentByTag(tag) as? T
        if (addedFragment == null) {
            fragmentManager?.beginTransaction()
                    ?.add(fragment, tag)
                    ?.commit()
            return fragment
        }
        return addedFragment
    }

    override fun onResume() {
        super.onResume()
        registerSubscriptions()
    }

    override fun onBackPressed() : Boolean {
        if (viewPager.currentItem > 0) {
            viewPager.setCurrentItem(viewPager.currentItem - 1, true)
            return true
        }
        return false
    }

    private fun registerSubscriptions() {
        subscription = CompositeDisposable()
        subscription.addAll(stateFragment.viewModel.changes.subscribe{ updateViewFromViewModel(it) },
                stateFragment.viewModel.pagesViewModel.pageChanges.subscribe { updatePagesFromViewModel(it) })
    }

    private fun resetState() {
        subscription.dispose()
        val clearedState = CharacterCreationStateFragment()
        fragmentManager?.beginTransaction()
                ?.remove(stateFragment)
                ?.add(clearedState, STATE_FRAGMENT_TAG)
                ?.commit()
        fragmentManager?.executePendingTransactions()
        stateFragment = clearedState
        registerSubscriptions()
    }

    private fun updatePagesFromViewModel(pageCollection: CharacterCreationPageCollection) {
        adapter.update(pageCollection)
        // ... etc ...
        if (viewPager.currentItem != pageCollection.currentPageIndex) {
            val previouslyConfigured = configured
            launch(UI) {
                // TODO: the delay should only happen when switching to newly added pages
                if (previouslyConfigured) withContext(DefaultDispatcher) { Thread.sleep(200) }
                viewPager.setCurrentItem(pageCollection.currentPageIndex, true)
            }
        }

        val currentPage = pageCollection.getCurrentPage()
        backButton.isEnabled = pageCollection.currentPageIndex != 0
        backButton.visibility = if (pageCollection.currentPageIndex != 0) View.VISIBLE else View.INVISIBLE
        forwardButton.isEnabled = currentPage.isLastPage || pageCollection.currentPageIndex < pageCollection.size - 1
        forwardButton.text = if (currentPage.isLastPage) getString(R.string.confirm) else getString(R.string.next)
        // TODO: page descriptors should expose logic to determine which buttons to show/what they do when clicked
        forwardButton.setOnClickListener {
            if (currentPage.isLastPage) stateFragment.viewModel.saveCharacter(DnDDatabase.getInstance(activity!!))
            else viewPager.setCurrentItem(viewPager.currentItem + 1, true)
        }
        // If neither of the navigation buttons are enabled, hide them
        if (backButton.isEnabled || forwardButton.isEnabled) {
            buttonWrapper.visibility = View.VISIBLE
        } else {
            buttonWrapper.visibility = View.GONE
        }

        configured = true
    }

    private fun updateViewFromViewModel(creationViewModel: CharacterCreationViewModel) {
        if (creationViewModel.isComplete) {
            // Character was created, start the whole flow over by replacing the state fragment
            resetState()
        } else {
            // For now, block the whole UI while anything is loading, but in the future
            // the user should still be allowed to interact
            loadingDialog.visibility = if (creationViewModel.isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun onPause() {
        super.onPause()
        subscription.dispose()
    }
}