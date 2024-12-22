package com.singularityindonesia.modelfirstprogramming.core.tools

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

interface AutomatedInstance {
    val coroutine: CoroutineScope
    val subscribedParameters: MutableStateFlow<List<Any>>
    fun destroyInstance()
}

class AutomatedInstanceImpl(
    override val coroutine: CoroutineScope,
    private val destroyInstance: () -> Unit,
) : AutomatedInstance {
    override val subscribedParameters: MutableStateFlow<List<Any>> = MutableStateFlow(emptyList())

    override fun destroyInstance() {
        destroyInstance.invoke()
    }
}

context(AutomatedInstance)
inline fun <reified T, R> MutableStateFlow<T>.automateShare(
    default: T,
    @Suppress("UNCHECKED_CAST")
    noinline map: (T) -> R = { it as R },
    noinline onStart: () -> Unit = {}
): StateFlow<R> = this
    .onSubscription {
        this@AutomatedInstance.subscribedParameters.update {
            it.takeIf { it.contains(T::class) } ?: (it + T::class)
        }
    }
    .onCompletion { cause ->
        this@AutomatedInstance.subscribedParameters.update { it - T::class }

        // if nothing is subscribed, destroy instance
        if (this@AutomatedInstance.subscribedParameters.value.isEmpty())
            this@AutomatedInstance.destroyInstance()

    }
    .map { map(it) }
    .onStart { onStart.invoke() }
    .stateIn(
        this@AutomatedInstance.coroutine,
        SharingStarted.WhileSubscribed(INSTANCE_CACHING_DURATION.inWholeMilliseconds),
        map(default)
    )
