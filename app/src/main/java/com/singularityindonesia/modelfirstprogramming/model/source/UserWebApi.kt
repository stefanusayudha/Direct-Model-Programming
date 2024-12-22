package com.singularityindonesia.modelfirstprogramming.model.source

import com.singularityindonesia.modelfirstprogramming.model.Name
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import kotlin.time.Duration.Companion.seconds

interface UserWebApi {
    suspend fun fetchUser(): Result<String>
    suspend fun updateUserName(name: Name): Result<Name>
}

class UserWebApiImpl : UserWebApi {
    // dummy web source
    private var user = """
        {
            "name": "User"
        }
    """.trimIndent()

    override suspend fun fetchUser(): Result<String> {
        delay(5.seconds)
        // dummy
        return Result.success(user)
    }

    override suspend fun updateUserName(name: Name): Result<Name> {
        delay(5.seconds)
        user = user.let { Json.parseToJsonElement(user).jsonObject }.toMutableMap()
            .apply {
                this["name"] = JsonPrimitive(name.value)
            }
            .let { Json.encodeToJsonElement(it) }
            .toString()

        return Result.success(name)
    }
}