package com.fatih.prayertime.util.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart

fun <T> Flow<T>.withRetry(retryTrigger : SharedFlow<Unit>) : Flow<T> {
    return this.combine(retryTrigger.onStart { emit(Unit) }){ value,_-> value }
}