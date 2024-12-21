package com.singularityindonesia.modelfirstprogramming.model.source

import com.singularityindonesia.modelfirstprogramming.model.Name
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import kotlin.time.Duration.Companion.seconds

interface UserWebApi {
    suspend fun fetchUser(): Result<JsonElement>
    suspend fun updateUserName(name: Name): Result<Name>
}

class UserWebApiImpl : UserWebApi {
    // dummy web source
    private var user = """
        {
            "name": "User"
        }
    """.let { Json.parseToJsonElement(it) }

    override suspend fun fetchUser(): Result<JsonElement> {
        delay(5.seconds)
        // dummy
        return Result.success(user)
    }

    override suspend fun updateUserName(name: Name): Result<Name> {
        user = user.jsonObject.toMutableMap()
            .apply { set("name", Json.parseToJsonElement(name.value)) }
            .let { Json.encodeToJsonElement(it) }

        return Result.success(name)
    }
}