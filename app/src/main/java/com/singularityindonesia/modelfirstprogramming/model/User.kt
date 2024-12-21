package com.singularityindonesia.modelfirstprogramming.model

import com.singularityindonesia.modelfirstprogramming.core.tools.AutomatedInstance
import com.singularityindonesia.modelfirstprogramming.core.tools.AutomatedInstanceImpl
import com.singularityindonesia.modelfirstprogramming.core.tools.automateShare
import com.singularityindonesia.modelfirstprogramming.model.source.UserWebApi
import com.singularityindonesia.modelfirstprogramming.model.source.UserWebApiImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class User private constructor(
    override val coroutine: CoroutineScope = CoroutineScope(Dispatchers.IO),
    private val webApi: UserWebApi = UserWebApiImpl(),
) : AutomatedInstance by AutomatedInstanceImpl(
    coroutine,
    Companion::destroy,
) {
    companion object {
        private var instance: User? = null
        private var destructionJob: Job? = null
        fun get(): User {
            return instance ?: User().also { instance = it }
        }

        private fun destroy() {
            instance = null
        }
    }

    private val _isSynchronizing = MutableStateFlow(false)
    val isSynchronizing = _isSynchronizing.automateShare(_isSynchronizing.value)

    suspend fun sync(): Result<Unit> = withContext(coroutine.coroutineContext) {
        _isSynchronizing.update { true }
        runCatching {
            val json = webApi.fetchUser().map { it.jsonObject }.getOrElse { throw it }
            val name = json["name"]?.jsonPrimitive?.content.orEmpty()
            _name.update { Name(name) }
            _isSynchronizing.update { false }
        }
    }

    private val _name = MutableStateFlow(Name(""))
    val name: StateFlow<Name> = _name.automateShare(default = _name.value)

    suspend fun updateUserName(name: Name): Result<Name> = withContext(coroutine.coroutineContext) {
        webApi.updateUserName(name).onSuccess { _name.update { name } }
    }

    init {
        coroutine.launch {
            // retry twice then give up
            sync().onFailure { sync() }
        }
    }
}
