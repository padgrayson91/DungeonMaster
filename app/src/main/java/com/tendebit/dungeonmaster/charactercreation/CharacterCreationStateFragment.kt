package com.tendebit.dungeonmaster.charactercreation

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.tendebit.dungeonmaster.App
import com.tendebit.dungeonmaster.core.model.DnDDatabase
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch

const val STATE_FRAGMENT_TAG = "character_creation_state_fragment"

/**
 * Headless fragment which maintains references to the ViewModel for character creation.  Because this
 * fragment is headless, it does not get destroyed/recreated when the UI is redrawn, so the data will
 * be preserved here across orientation change.  UI components can then access this fragment via the
 * fragment manager at any time in order to read/write data from the ViewModels
 */
class CharacterCreationStateFragment : Fragment() {


    private val db = DnDDatabase.getInstance(App.instance.applicationContext)
    val viewModel = CharacterCreationViewModel(db.characterDao())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDetach()
        viewModel.resetWorkflow()
    }

    fun reset(onComplete: (vm: CharacterCreationViewModel) -> Unit) {
        launch(UI) {
            val result = async {
                viewModel.resetWorkflow()
                Thread.sleep(500)
                return@async viewModel
            }.await()
            onComplete(result)
        }
    }

}