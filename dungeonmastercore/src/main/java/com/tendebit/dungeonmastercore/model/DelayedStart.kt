package com.tendebit.dungeonmastercore.model

/**
 * [DelayedStart] objects have dependencies which, for some implementation-specific reason, cannot be
 * provided as part of the object constructor. Some examples are objects constructed via deserialization
 * which have dependencies which could not be serialized, or objects which are constructed earlier in the Android
 * lifecycle than some dependency
 */
interface DelayedStart<Prerequisites> {

	/**
	 * Invoke this method to supply dependencies to the object. This method should be invoked as soon as possible after
	 * the object constructor
	 */
	fun start(prerequisites: Prerequisites)

}
