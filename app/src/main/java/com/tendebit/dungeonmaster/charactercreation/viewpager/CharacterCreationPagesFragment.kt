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
import com.tendebit.dungeonmaster.charactercreation.viewpager.adapter.CharacterCreationPagerAdapter
import com.tendebit.dungeonmaster.charactercreation.pages.PageAction
import com.tendebit.dungeonmaster.core.view.BackNavigationHandler
import com.tendebit.dungeonmaster.core.view.LoadingDialog
import io.reactivex.disposables.CompositeDisposable
import org.koin.android.ext.android.inject

/**
 * UI fragment for displaying the character creation workflow to the user
 */
class CharacterCreationPagesFragment: Fragment(), BackNavigationHandler {
    private lateinit var adapter: CharacterCreationPagerAdapter
    private lateinit var viewPager: ViewPager
    private lateinit var buttonWrapper: ViewGroup
    private lateinit var backButton: Button
    private lateinit var forwardButton: Button
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var subscription: CompositeDisposable
    private val parentViewModel: CharacterCreationViewModel by inject()
    private val viewModel: CharacterCreationPagesViewModel by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_generic_viewpager, container, false)
        addFragmentIfMissing(CharacterCreationStateFragment(), STATE_FRAGMENT_TAG)
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
                if (viewModel.currentPageIndex != position) {
                    // hide the soft keyboard
                    val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(view?.windowToken, 0)
                    viewModel.onPageSelected(position)
                }
            }
        })
        adapter = CharacterCreationPagerAdapter(childFragmentManager, parentViewModel)
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
        registerSubscriptions(parentViewModel)
    }

    override fun onPause() {
        super.onPause()
        subscription.dispose()
    }

    override fun onBackPressed() : Boolean {
        if (viewPager.currentItem > 0) {
            viewPager.setCurrentItem(viewPager.currentItem - 1, true)
            return true
        }
        return false
    }

    private fun registerSubscriptions(parentViewModel: CharacterCreationViewModel) {
        subscription = CompositeDisposable()
        subscription.addAll(
                parentViewModel.loadingChanges.subscribe { updateLoadingDialog(it) },
                parentViewModel.completionChanges.distinctUntilChanged().filter{it}.subscribe{ resetState() },
                viewModel.pageChanges.subscribe { updatePagesFromViewModel(it) },
                viewModel.indexChanges.subscribe { updatePageIndex(it) }
        )
    }

    private fun resetState() {
        viewModel.resetPages()
    }

    private fun updatePagesFromViewModel(pageCollection: List<CharacterCreationPageDescriptor>) {
        adapter.update(pageCollection)
        configureActionButtonsForPage(pageCollection[viewModel.currentPageIndex])
    }

    private fun updatePageIndex(index: Int) {
        if (viewPager.currentItem != index) {
                viewPager.setCurrentItem(index, true)
                // hide the soft keyboard
                val imm = viewPager.context
                        ?.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(view?.windowToken, 0)
            configureActionButtonsForPage(viewModel.pageCollection[viewModel.currentPageIndex])
        }
    }

    private fun configureActionButtonsForPage(currentPage: CharacterCreationPageDescriptor) {
        var backAction: PageAction? = null
        if (currentPage.actions.contains(PageAction.NAVIGATE_BACK)) {
            backAction = PageAction.NAVIGATE_BACK
        }

        if (backAction != null) {
            backButton.visibility = View.VISIBLE
            backButton.isEnabled = true
            backButton.setOnClickListener { viewModel.performAction(backAction) }
            backButton.text = getTextForAction(backAction)
        } else {
            backButton.visibility = View.INVISIBLE
            backButton.isEnabled = false
        }

        backButton.visibility = if (currentPage.actions.contains(PageAction.NAVIGATE_BACK))
            View.VISIBLE else View.INVISIBLE

        var forwardAction: PageAction? = null
        if (currentPage.actions.contains(PageAction.NAVIGATE_FORWARD)) {
            forwardAction = PageAction.NAVIGATE_FORWARD
        } else if (currentPage.actions.contains(PageAction.CONFIRM)) {
            forwardAction = PageAction.CONFIRM
        }

        if (forwardAction != null) {
            forwardButton.visibility = View.VISIBLE
            forwardButton.setOnClickListener {
                if (forwardAction == PageAction.CONFIRM)
                    parentViewModel.saveCharacter()
                viewModel.performAction(forwardAction)
            }
            forwardButton.text = getTextForAction(forwardAction)
            // TODO: the enabled state should be part of the action object
            forwardButton.isEnabled = forwardAction == PageAction.CONFIRM ||
									  viewModel.currentPageIndex < viewModel.pageCollection.size - 1
        } else {
            forwardButton.visibility = View.INVISIBLE
            forwardButton.isEnabled = false
        }

        // If neither of the navigation buttons are enabled, hide them
        if (backButton.isEnabled || forwardButton.isEnabled) {
            buttonWrapper.visibility = View.VISIBLE
        } else {
            buttonWrapper.visibility = View.GONE
        }
    }

    private fun getTextForAction(action: PageAction) : String {
        return when(action) {
            PageAction.NAVIGATE_BACK -> getString(R.string.back)
            PageAction.NAVIGATE_FORWARD -> getString(R.string.next)
            PageAction.CONFIRM -> getString(R.string.confirm)
        }
    }

    private fun updateLoadingDialog(isLoading: Boolean) {
        // For now, block the whole UI while anything is loading, but in the future
        // the user should still be allowed to interact
        loadingDialog.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}