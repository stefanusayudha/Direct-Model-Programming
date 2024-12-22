package com.singularityindonesia.modelfirstprogramming.model

import com.singularityindonesia.modelfirstprogramming.core.tools.AutomatedInstance
import com.singularityindonesia.modelfirstprogramming.core.tools.AutomatedInstanceImpl
import com.singularityindonesia.modelfirstprogramming.core.tools.automateShare
import com.singularityindonesia.modelfirstprogramming.model.source.UserWebApi
import com.singularityindonesia.modelfirstprogramming.model.source.UserWebApiImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class User private constructor(
    override val coroutine: CoroutineScope = CoroutineScope(Dispatchers.IO),
    private val webApi: UserWebApi = UserWebApiImpl(),
) : AutomatedInstance by AutomatedInstanceImpl(
    coroutine,
    Companion::destroy,
) {
    companion object {
        private var instance: User? = null
        fun get(): User {
            return instance ?: User().also { instance = it }
        }

        private fun destroy() {
            instance = null
        }
    }

    private val _isSynchronizing = MutableStateFlow(false)
    val isSynchronizing = _isSynchronizing.automateShare(default = _isSynchronizing.value, { it })

    private val record = MutableStateFlow<UserRecord?>(null)
    val name: StateFlow<Name> =
        record.automateShare(default = record.value, map = { Name(it?.name.orEmpty()) })

    suspend fun sync(): Result<Unit> = withContext(coroutine.coroutineContext) {
        _isSynchronizing.update { true }
        runCatching {
            webApi.fetchUser()
                .map { Json.decodeFromString<UserRecord>(it) }
                .getOrElse { throw it }
                .also { rec -> record.update { rec } }

            _isSynchronizing.update { false }
        }
    }

    suspend fun updateUserName(name: Name): Result<Name> = withContext(coroutine.coroutineContext) {
        webApi.updateUserName(name).onSuccess { record.update { it?.copy(name = name.value) } }
    }

    init {
        coroutine.launch {
            // retry twice then give up
            sync().onFailure { sync() }
        }
    }
}

@Serializable
private data class UserRecord(
    val name: String? = null,
    val email: String? = null,
)
