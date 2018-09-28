package com.tendebit.dungeonmaster

import android.app.Application
import androidx.fragment.app.Fragment
import com.tendebit.dungeonmaster.charactercreation.CharacterCreationViewModel
import com.tendebit.dungeonmaster.charactercreation.model.StoredCharacterSupplier
import com.tendebit.dungeonmaster.charactercreation.pages.characterlist.CharacterListViewModel
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassInfoSupplier
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.model.CharacterRaceInfoSupplier
import com.tendebit.dungeonmaster.core.model.DnDDatabase
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

        // ViewModels
        single { CharacterCreationViewModel(get())}
        single { (fragment: Fragment) ->
            val viewModel: CharacterCreationViewModel = get()
            val viewModelTag = fragment.arguments!![CharacterCreationViewModel.ARG_VIEW_MODEL_TAG] as String
            var childViewModel = viewModel.getChildViewModel<CharacterListViewModel>(viewModelTag)
            if (childViewModel == null) {
                childViewModel = CharacterListViewModel(get())
                viewModel.addCharacterList(viewModelTag, childViewModel)
                childViewModel
            } else {
                childViewModel
            }

        }

    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        startKoin(this, listOf(appModule))
    }
}