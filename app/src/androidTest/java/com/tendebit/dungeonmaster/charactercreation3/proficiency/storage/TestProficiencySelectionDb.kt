package com.tendebit.dungeonmaster.charactercreation3.proficiency.storage

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tendebit.dungeonmaster.charactercreation3.feature.storage.CharacterCreationDb
import com.tendebit.dungeonmaster.charactercreation3.proficiency.DndProficiencySelection
import com.tendebit.dungeonmaster.charactercreation3.proficiency.data.storage.DndProficiencyStorage
import com.tendebit.dungeonmaster.charactercreation3.proficiency.data.storage.RoomProficiencyStorage
import com.tendebit.dungeonmaster.testhelpers.CharacterCreationViewRobots
import com.tendebit.dungeonmaster.testhelpers.TestConcurrencyUi
import com.tendebit.dungeonmastercore.model.state.Locked
import io.reactivex.observers.TestObserver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class TestProficiencySelectionDb {

	private lateinit var db: CharacterCreationDb
	private lateinit var storage: DndProficiencyStorage

	@Before
	fun setup() {
		val context = ApplicationProvider.getApplicationContext<Context>()
		db = Room.inMemoryDatabaseBuilder(context, CharacterCreationDb::class.java).build()
		storage = RoomProficiencyStorage(db.proficiencyDao(), TestConcurrencyUi)
	}

	@After
	@Throws(IOException::class)
	fun cleanup() {
		db.close()
	}

	@Test
	fun testSaveSelection() {
		val selection = DndProficiencySelection(CharacterCreationViewRobots.blankProficiencyGroups)
		val id = storage.storeSelection(selection)
		assert(id.isNotEmpty())
		val testObserver = TestObserver<DndProficiencySelection>()
		val maybe = storage.findSelectionById(id)
		maybe.subscribe(testObserver)
		testObserver.await()
		testObserver.assertNoErrors()
		testObserver.assertValueCount(1)
		val result = testObserver.values()[0]
		assert(result.groupStates.isNotEmpty())
		for (item in result.groupStates.withIndex()) {
			val groupIndex = item.index
			val groupState = item.value
			val groupFromOriginal = selection.groupStates[groupIndex]
			assert(groupFromOriginal == groupState) { "Expected $groupFromOriginal but had $groupState"}
			assert(groupState.item!!.options.size == groupFromOriginal.item!!.options.size) { "Group sizes didn't match" }
			for (subItem in groupState.item!!.options.withIndex()) {
				val profIndex = subItem.index
				val profState = subItem.value
				assert(groupFromOriginal.item?.options?.get(profIndex) == profState) { "Got unexpected state $profState at index $profIndex in group $groupIndex"}
			}
		}
	}

	@Test
	fun testSaveSelectionWithSelectedItem() {
		val selection = DndProficiencySelection(CharacterCreationViewRobots.blankProficiencyGroups)
		selection.groupStates[0].item?.select(2)
		val id = storage.storeSelection(selection)
		assert(id.isNotEmpty())
		val testObserver = TestObserver<DndProficiencySelection>()
		val maybe = storage.findSelectionById(id)
		maybe.subscribe(testObserver)
		testObserver.await()
		testObserver.assertNoErrors()
		testObserver.assertValueCount(1)
		val result = testObserver.values()[0]
		assert(result.groupStates.isNotEmpty())
		for (item in result.groupStates.withIndex()) {
			val groupIndex = item.index
			val groupState = item.value
			val groupFromOriginal = selection.groupStates[groupIndex]
			assert(groupFromOriginal == groupState) { "Expected $groupFromOriginal but had $groupState"}
			assert(groupState.item!!.options.size == groupFromOriginal.item!!.options.size) { "Group sizes didn't match" }
			for (subItem in groupState.item!!.options.withIndex()) {
				val profIndex = subItem.index
				val profState = subItem.value
				assert(groupFromOriginal.item?.options?.get(profIndex) == profState) { "Got unexpected state $profState at index $profIndex in group $groupIndex"}
			}
		}

		// Item selected in one group should have locked state in another
		assert(result.groupStates[1].item!!.options[2] is Locked)
	}

}
