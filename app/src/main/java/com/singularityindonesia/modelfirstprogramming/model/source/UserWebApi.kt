package com.singularityindonesia.modelfirstprogramming.model.source

import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

class UserWebApi {
    private var userName = "User"

    suspend fun getUserName(): Result<String> {
        delay(5.seconds)
        return runCatching { userName }
    }

    suspend fun updateUserName(name: String): Result<String> {
        delay(5.seconds)
        userName = name
        return runCatching { userName }
    }
}