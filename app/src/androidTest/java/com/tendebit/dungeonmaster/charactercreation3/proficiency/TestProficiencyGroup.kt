package com.tendebit.dungeonmaster.charactercreation3.proficiency

import android.os.Parcel
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tendebit.dungeonmaster.charactercreation3.Normal
import com.tendebit.dungeonmaster.charactercreation3.Selected
import com.tendebit.dungeonmaster.testhelpers.CharacterCreationViewRobots
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import net.bytebuddy.matcher.ElementMatchers.`is` as matches
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TestProficiencyGroup {

	@Test
	fun testParcelization() {
		val toTest = DndProficiencyGroup(CharacterCreationViewRobots.blankProficiencyStateList, 2)
		val parcel = Parcel.obtain()
		toTest.writeToParcel(parcel, toTest.describeContents())
		parcel.setDataPosition(0)

		val testResult = DndProficiencyGroup.createFromParcel(parcel)

		assertEquals(testResult.remainingChoices, 2)
	}

	@Test
	fun testParcelizationAfterSelection() {
		val toTest = DndProficiencyGroup(CharacterCreationViewRobots.blankProficiencyStateList, 2)
		toTest.select(0)
		val parcel = Parcel.obtain()
		toTest.writeToParcel(parcel, toTest.describeContents())
		parcel.setDataPosition(0)

		val testResult = DndProficiencyGroup.createFromParcel(parcel)

		assertEquals(testResult.remainingChoices, 1)
		assertTrue(testResult.options[0] is Selected)
		assertTrue(testResult.options[1] is Normal)
	}

}