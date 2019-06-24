package com.tendebit.dungeonmaster

import android.app.Application
import com.tendebit.dungeonmastercore.debug.DebugUtils
import org.koin.android.ext.android.startKoin

@Suppress("Unused")
class App : Application() {
    companion object {
        @JvmStatic
        lateinit var instance: App
    }

//    private val appModule : Module = module {
//        single { DnDDatabase.getInstance(get()) }
//        single<StoredCharacterSupplier> {
//            val db: DnDDatabase = get()
//            StoredCharacterSupplier.Impl(db.characterDao())
//        }
//        single {
//            val db: DnDDatabase = get()
//            db.responseDao()
//        }
//        single<CharacterClassInfoSupplier> { CharacterClassInfoSupplier.Impl(get()) }
//        single<CharacterRaceInfoSupplier> { CharacterRaceInfoSupplier.Impl(get()) }
//
//        module("CharacterCreation") {
//            // ViewModels
//            single { CharacterCreationPagesViewModel() }
//            single { CharacterCreationViewModel(get(), get()) } bind (ViewModelParent::class)
//
//            factory("newInstance") { CharacterListViewModel(get()) }
//            factory("newOrExisting") { (fragment: Fragment) ->
//                val viewModelTag = fragment.arguments!![CharacterCreationViewModel.ARG_VIEW_MODEL_TAG] as String
//                ViewModels.get<CharacterListViewModel>(viewModelTag, get(), get("newInstance"))
//            }
//
//            factory("newInstance") { RaceSelectionViewModel(get())}
//            factory("newOrExisting") { (fragment: Fragment) ->
//                val viewModelTag = fragment.arguments!![CharacterCreationViewModel.ARG_VIEW_MODEL_TAG] as String
//                ViewModels.get<RaceSelectionViewModel>(viewModelTag, get(), get("newInstance"))
//            }
//
//            factory("newInstance") { ClassSelectionViewModel(get())}
//            factory("newOrExisting") { (fragment: Fragment) ->
//                val viewModelTag = fragment.arguments!![CharacterCreationViewModel.ARG_VIEW_MODEL_TAG] as String
//                ViewModels.get<ClassSelectionViewModel>(viewModelTag, get(), get("newInstance"))
//            }
//
//            factory("newInstance") {
//                val parentViewModel: CharacterCreationViewModel = get()
//                ProficiencySelectionViewModel(parentViewModel.selectedClass!!)}
//            factory("newOrExisting") { (fragment: Fragment) ->
//                val viewModelTag = fragment.arguments!![CharacterCreationViewModel.ARG_VIEW_MODEL_TAG] as String
//                ViewModels.get<ProficiencySelectionViewModel>(viewModelTag, get(), get("newInstance"))
//            }
//
//            factory("newInstance") { CustomInfoEntryViewModel()}
//            factory("newOrExisting") { (fragment: Fragment) ->
//                val viewModelTag = fragment.arguments!![CharacterCreationViewModel.ARG_VIEW_MODEL_TAG] as String
//                ViewModels.get<CustomInfoEntryViewModel>(viewModelTag, get(), get("newInstance"))
//            }
//        }
//
//    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        DebugUtils.logger.debug = BuildConfig.DEBUG
        DebugUtils.logger.writeDebug("Creating application")
        startKoin(this, emptyList())
    }
}