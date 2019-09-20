package com.tendebit.dungeonmastercore.network

import com.google.gson.Gson
import okhttp3.OkHttpClient

interface NetworkEnvironment {

	val gson: Gson
	val client: OkHttpClient

	class Impl(override val gson: Gson, override val client: OkHttpClient) : NetworkEnvironment

}
