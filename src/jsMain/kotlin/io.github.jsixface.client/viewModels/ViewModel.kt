package io.github.jsixface.client.viewModels

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

abstract class ViewModel {
    val scope = CoroutineScope(Dispatchers.Default)


    fun destroy() {
        console.log("Cancelling view model scope ${this::class}")
        scope.cancel()
    }
}