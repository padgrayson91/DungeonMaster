package com.tendebit.dungeonmaster

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tendebit.dungeonmaster.charactercreation.viewpager.CharacterCreationWizardFragment
import com.tendebit.dungeonmaster.core.view.BackNavigationHandler
import io.reactivex.Observable

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        switchToCharacterCreation()
    }

    private fun switchToCharacterCreation() {
        val addedFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (addedFragment == null) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, CharacterCreationWizardFragment())
                    .commit()
        }
    }

    override fun onBackPressed() {
        val navigated = Observable.fromIterable(supportFragmentManager.fragments)
                .ofType(BackNavigationHandler::class.java)
                .blockingFirst(object: BackNavigationHandler {
                    override fun onBackPressed(): Boolean {
                        super@DashboardActivity.onBackPressed()
                        return true
                    }
                }).onBackPressed()
        if (!navigated) super.onBackPressed()
    }
}
