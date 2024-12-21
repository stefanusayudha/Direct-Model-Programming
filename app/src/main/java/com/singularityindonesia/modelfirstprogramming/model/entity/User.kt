package com.singularityindonesia.modelfirstprogramming.model.entity

import com.singularityindonesia.modelfirstprogramming.core.tools.INSTANCE_CACHING_DURATION
import com.singularityindonesia.modelfirstprogramming.model.Name
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class User private constructor(
    private val coroutine: CoroutineScope = CoroutineScope(Dispatchers.IO),
    private val webApi: UserWebApi = UserWebApi(),
) {
    companion object {
        private var instance: User? = null
        private var destructionJob: Job? = null
        fun get(): User {
            destructionJob?.cancel()
            return instance
                ?.also {
                    println("Get existing User instance")
                }
                ?: User().also {
                    println("adad Creating new User instance")
                    // cache user instance
                    instance = it
                }
        }

        private fun destroy() {
            destructionJob?.cancel()
            destructionJob = instance?.coroutine?.launch {
                instance = null
                println("adad Instance destroyed ${User::class} ${instance}")
            }
        }
    }

    private val subscribedParameters = MutableStateFlow<List<Any>>(emptyList())

    private inline fun <reified T> MutableStateFlow<T>.asStateFlow(
        default: T,
        noinline onStart: () -> Unit = {}
    ): StateFlow<T> = this
        .onSubscription {
            destructionJob?.cancel()
            subscribedParameters.update {
                it.takeIf { it.contains(T::class) } ?: (it + T::class)
            }
            println("adad Subscribed ${T::class.simpleName}")
            println("adad Subscribed Parameters ${subscribedParameters.value.map { it::class.simpleName }}")
        }
        .onCompletion { cause ->
            subscribedParameters.update { it - T::class }

            // if nothing is subscribed, destroy instance
            if (subscribedParameters.value.isEmpty())
                destroy()

            println("adad Subscription Complete ${cause?.let { it::class.simpleName }} ${subscribedParameters.value.map { it::class.simpleName }}")
        }
        .onStart { onStart.invoke() }
        .stateIn(
            coroutine,
            SharingStarted.WhileSubscribed(INSTANCE_CACHING_DURATION.inWholeMilliseconds),
            default
        )

    private val _name = MutableStateFlow(Name(""))
    val name: StateFlow<Name> = _name
        .asStateFlow(
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
