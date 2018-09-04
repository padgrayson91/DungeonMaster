package com.tendebit.dungeonmaster.sandbox

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import com.tendebit.dungeonmaster.R
import com.tendebit.dungeonmaster.sandbox.model.SandboxServiceImpl
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.cancelAndJoin
import kotlinx.coroutines.experimental.launch

class SandboxActivity : AppCompatActivity() {

    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sandbox)
        attemptFetchClasses()
    }

    override fun onStop() {
        super.onStop()
        launch(UI) {
            job?.cancelAndJoin()
        }
    }

    private fun attemptFetchClasses() {
        val service = SandboxServiceImpl()
        job = launch {
            try {
                val result = service.getCharacterClasses()
                Log.d("SANDBOX", "Got " + result.characterClassDirectories.size + " character classes. The first one is " + result.characterClassDirectories[0].name)
            } catch (e: Exception) {
                Toast.makeText(this@SandboxActivity, e.message, LENGTH_LONG).show()
                Log.e("SANDBOX", "Got an error", e)
            }
        }
    }
}
