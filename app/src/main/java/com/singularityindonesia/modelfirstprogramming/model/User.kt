package com.singularityindonesia.modelfirstprogramming.model

import com.singularityindonesia.modelfirstprogramming.core.tools.AutomatedInstance
import com.singularityindonesia.modelfirstprogramming.core.tools.AutomatedInstanceImpl
import com.singularityindonesia.modelfirstprogramming.core.tools.automateShare
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class User private constructor(
    override val coroutine: CoroutineScope = CoroutineScope(Dispatchers.IO),
    private val webApi: UserWebApi = UserWebApi(),
) : AutomatedInstance by AutomatedInstanceImpl(
    coroutine,
    Companion::destroy,
    Companion::cancelDestroy
) {
    companion object {
        private var instance: User? = null
        private var destructionJob: Job? = null
        fun get(): User {
            cancelDestroy()
            return instance ?: User().also { instance = it }
        }

        private fun destroy() {
            cancelDestroy()
            destructionJob = instance?.coroutine?.launch {
                instance = null
            }
        }

        private fun cancelDestroy() {
            destructionJob?.cancel()
        }
    }

    private val _name = MutableStateFlow(Name(""))
    val name: StateFlow<Name> = _name
        .automateShare(
            default = _name.value,
            onStart = { fetchUserName() }
        )

    private fun fetchUserName() {
        coroutine.launch {
            _name.update { Name("...") }
            val result = webApi.getUserName().getOrElse { "Error" }
            _name.update { Name(result) }
        }
    }

    suspend fun updateName(name: Name): Result<Name> = withContext(coroutine.coroutineContext) {
        webApi.updateUserName(name.value)
            .map { Name(it) }
            .onSuccess { newName -> _name.update { newName } }
    }
}
