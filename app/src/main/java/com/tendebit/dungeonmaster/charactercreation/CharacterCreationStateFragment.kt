package com.tendebit.dungeonmaster.charactercreation

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.tendebit.dungeonmaster.charactercreation.pages.characterlist.CharacterListViewModel
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.ClassSelectionViewModel
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassInfoSupplier
import com.tendebit.dungeonmaster.charactercreation.pages.custominfoentry.CustomInfoEntryViewModel
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.ProficiencySelectionViewModel
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.RaceSelectionViewModel
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.model.CharacterRaceInfoSupplier
import com.tendebit.dungeonmaster.charactercreation.viewpager.CharacterCreationPagesViewModel
import com.tendebit.dungeonmaster.core.model.DnDDatabase

const val STATE_FRAGMENT_TAG = "character_creation_state_fragment"

/**
 * Headless fragment which maintains references to the ViewModels for character creation.  Because this
 * fragment is headless, it does not get destroyed/recreated when the UI is redrawn, so the data will
 * be preserved here across orientation change.  UI components can then access this fragment via the
 * fragment manager at any time in order to read/write data from the ViewModels
 */
class CharacterCreationStateFragment : Fragment() {


    lateinit var viewModel: CharacterCreationViewModel
    private lateinit var pagesViewModel: CharacterCreationPagesViewModel
    private lateinit var savedCharacterListViewModel: CharacterListViewModel
    private lateinit var raceViewModel : RaceSelectionViewModel
    private lateinit var classViewModel: ClassSelectionViewModel
    private lateinit var proficiencyViewModel: ProficiencySelectionViewModel
    private lateinit var customInfoViewModel: CustomInfoEntryViewModel
    lateinit var db : DnDDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        db = DnDDatabase.getInstance(activity!!.applicationContext)
        pagesViewModel = CharacterCreationPagesViewModel()
        savedCharacterListViewModel = CharacterListViewModel(db)
        raceViewModel = RaceSelectionViewModel(CharacterRaceInfoSupplier.Impl(db))
        classViewModel = ClassSelectionViewModel(CharacterClassInfoSupplier.Impl(db))
        proficiencyViewModel = ProficiencySelectionViewModel()
        customInfoViewModel = CustomInfoEntryViewModel()
        viewModel = CharacterCreationViewModel(db, pagesViewModel, savedCharacterListViewModel, raceViewModel,
                classViewModel, proficiencyViewModel, customInfoViewModel)
    }

    override fun onDestroy() {
        super.onDestroy()
        clear()
    }

    // TODO: this should be done asynchronously, then the UI shouldn't need a delay
    fun reset() {
        clear()

        pagesViewModel = CharacterCreationPagesViewModel()
        raceViewModel = RaceSelectionViewModel(CharacterRaceInfoSupplier.Impl(db))
        classViewModel = ClassSelectionViewModel(CharacterClassInfoSupplier.Impl(db))
        proficiencyViewModel = ProficiencySelectionViewModel()
        customInfoViewModel = CustomInfoEntryViewModel()
        viewModel = CharacterCreationViewModel(db, pagesViewModel, savedCharacterListViewModel, raceViewModel,
                classViewModel, proficiencyViewModel, customInfoViewModel)
    }

    private fun clear() {
        viewModel.cancelAllSubscriptions()
        savedCharacterListViewModel.cancelAllCalls()
        raceViewModel.cancelAllCalls()
        classViewModel.cancelAllCalls()
    }

}