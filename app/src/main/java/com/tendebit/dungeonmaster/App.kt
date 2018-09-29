package com.tendebit.dungeonmaster

import android.app.Application
import androidx.fragment.app.Fragment
import com.tendebit.dungeonmaster.charactercreation.CharacterCreationViewModel
import com.tendebit.dungeonmaster.charactercreation.model.StoredCharacterSupplier
import com.tendebit.dungeonmaster.charactercreation.pages.characterlist.CharacterListViewModel
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.ClassSelectionViewModel
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassInfoSupplier
import com.tendebit.dungeonmaster.charactercreation.pages.custominfoentry.CustomInfoEntryViewModel
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.ProficiencySelectionViewModel
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.RaceSelectionViewModel
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.model.CharacterRaceInfoSupplier
import com.tendebit.dungeonmaster.charactercreation.viewpager.CharacterCreationPagesViewModel
import com.tendebit.dungeonmaster.core.model.DnDDatabase
import com.tendebit.dungeonmaster.core.viewmodel.ViewModelParent
import com.tendebit.dungeonmaster.core.viewmodel.ViewModels
import org.koin.android.ext.android.startKoin
import org.koin.dsl.module.Module
import org.koin.dsl.module.module

class App : Application() {
    companion object {
        @JvmStatic
        lateinit var instance: App
    }

    private val appModule : Module = module {
        single { DnDDatabase.getInstance(get()) }
        single<StoredCharacterSupplier> {
            val db: DnDDatabase = get()
            StoredCharacterSupplier.Impl(db.characterDao())
        }
        single {
            val db: DnDDatabase = get()
            db.responseDao()
        }
        single<CharacterClassInfoSupplier> { CharacterClassInfoSupplier.Impl(get()) }
        single<CharacterRaceInfoSupplier> { CharacterRaceInfoSupplier.Impl(get()) }

        module("CharacterCreation") {
            // ViewModels
            single { CharacterCreationPagesViewModel() }
            single { CharacterCreationViewModel(get(), get()) } bind (ViewModelParent::class)

            // TODO: the below might be possible with scoped singles
            factory("newInstance") { CharacterListViewModel(get()) }
            factory("newOrExisting") { (fragment: Fragment) ->
                val viewModelTag = fragment.arguments!![CharacterCreationViewModel.ARG_VIEW_MODEL_TAG] as String
                ViewModels.get<CharacterListViewModel>(viewModelTag, get(), get("newInstance"))
            }

            factory("newInstance") { RaceSelectionViewModel(get())}
            factory("newOrExisting") { (fragment: Fragment) ->
                val viewModelTag = fragment.arguments!![CharacterCreationViewModel.ARG_VIEW_MODEL_TAG] as String
                ViewModels.get<RaceSelectionViewModel>(viewModelTag, get(), get("newInstance"))
            }

            factory("newInstance") { ClassSelectionViewModel(get())}
            factory("newOrExisting") { (fragment: Fragment) ->
                val viewModelTag = fragment.arguments!![CharacterCreationViewModel.ARG_VIEW_MODEL_TAG] as String
                ViewModels.get<ClassSelectionViewModel>(viewModelTag, get(), get("newInstance"))
            }

            factory("newInstance") {
                val parentViewModel: CharacterCreationViewModel = get()
                ProficiencySelectionViewModel(parentViewModel.selectedClass!!)}
            factory("newOrExisting") { (fragment: Fragment) ->
                val viewModelTag = fragment.arguments!![CharacterCreationViewModel.ARG_VIEW_MODEL_TAG] as String
                ViewModels.get<ProficiencySelectionViewModel>(viewModelTag, get(), get("newInstance"))
            }

            factory("newInstance") { CustomInfoEntryViewModel()}
            factory("newOrExisting") { (fragment: Fragment) ->
                val viewModelTag = fragment.arguments!![CharacterCreationViewModel.ARG_VIEW_MODEL_TAG] as String
                ViewModels.get<CustomInfoEntryViewModel>(viewModelTag, get(), get("newInstance"))
            }
        }

    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        startKoin(this, listOf(appModule))
    }
}