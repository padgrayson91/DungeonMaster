package com.tendebit.dungeonmaster.charactercreation3.race

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tendebit.dungeonmaster.charactercreation3.feature.storage.CharacterCreationDb
import com.tendebit.dungeonmaster.charactercreation3.race.data.storage.DndRaceStorage
import com.tendebit.dungeonmaster.charactercreation3.race.data.storage.RoomRaceStorage
import com.tendebit.dungeonmaster.testhelpers.CharacterCreationViewRobots
import com.tendebit.dungeonmaster.testhelpers.TestConcurrencyUi
import io.reactivex.observers.TestObserver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class TestRaceSelectionDb {

	private lateinit var db: CharacterCreationDb
	private lateinit var storage: DndRaceStorage

	@Before
	fun setup() {
		val context = ApplicationProvider.getApplicationContext<Context>()
		db = Room.inMemoryDatabaseBuilder(context, CharacterCreationDb::class.java).build()
		storage = RoomRaceStorage(db.raceDao(), TestConcurrencyUi)
	}

	@After
	@Throws(IOException::class)
	fun cleanup() {
		db.close()
	}

	@Test
	fun testSaveSelection() {
		val selection = DndRaceSelection(CharacterCreationViewRobots.blankRaceStateList)
		val id = storage.storeSelection(selection)
		assert(id.isNotEmpty())
		val testObserver = TestObserver<DndRaceSelection>()
		val maybe = storage.findSelectionById(id)
		maybe.subscribe(testObserver)
		testObserver.await()
		testObserver.assertNoErrors()
		testObserver.assertValueCount(1)
		testObserver.assertValue { it.options == selection.options }
	}

	@Test
	fun testSaveSelectionWithSelectedItem() {
		val selection = DndRaceSelection(CharacterCreationViewRobots.blankRaceStateList)
		selection.select(0)
		val id = storage.storeSelection(selection)
		assert(id.isNotEmpty())
		val testObserver = TestObserver<DndRaceSelection>()
		val maybe = storage.findSelectionById(id)
		maybe.subscribe(testObserver)
		testObserver.await()
		testObserver.assertNoErrors()
		testObserver.assertValueCount(1)
		testObserver.assertValue { it.options == selection.options }
		testObserver.assertValue { it.selectedItem != null && it.selectedItem == selection.selectedItem }
	}

}
