package com.tendebit.dungeonmaster.charactercreation.viewpager

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.tendebit.dungeonmaster.charactercreation.pages.characterlist.CharacterListViewModel
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.ClassSelectionViewModel
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassInfoSupplier
import com.tendebit.dungeonmaster.charactercreation.pages.custominfoentry.CustomInfoEntryViewModel
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.ProficiencySelectionViewModel
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.RaceSelectionViewModel
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.model.CharacterRaceInfoSupplier
import com.tendebit.dungeonmaster.core.model.DnDDatabase
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.cancelAndJoin
import kotlinx.coroutines.experimental.launch

const val STATE_FRAGMENT_TAG = "character_creation_state_fragment"

class CharacterCreationStateFragment : Fragment() {


    lateinit var viewModel: CharacterCreationViewModel
    private lateinit var savedCharacterListViewModel: CharacterListViewModel
    private lateinit var raceViewModel : RaceSelectionViewModel
    private lateinit var classViewModel: ClassSelectionViewModel
    private lateinit var proficiencyViewModel: ProficiencySelectionViewModel
    private val customInfoState = CustomInfoEntryViewModel()
    lateinit var db : DnDDatabase
    var job : Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        db = DnDDatabase.getInstance(activity!!)
        savedCharacterListViewModel = CharacterListViewModel(db)
        raceViewModel = RaceSelectionViewModel(CharacterRaceInfoSupplier.Impl(db))
        classViewModel = ClassSelectionViewModel(CharacterClassInfoSupplier.Impl(db))
        proficiencyViewModel = ProficiencySelectionViewModel()
        viewModel = CharacterCreationViewModel(db, savedCharacterListViewModel, raceViewModel,
                classViewModel, proficiencyViewModel, customInfoState)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.cancelAllSubscriptions()
        savedCharacterListViewModel.cancelAllCalls()
        raceViewModel.cancelAllCalls()
        classViewModel.cancelAllCalls()
        launch(UI) {
            job?.cancelAndJoin()
        }
    }

}