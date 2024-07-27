package com.lanier.roco.picturebook.ext

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

/**
 * Created by 幻弦让叶
 * Date 2024/5/25 18:20
 */
fun LifecycleOwner.launchSafe(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    error: ((Throwable) -> Unit)? = null,
    block: suspend CoroutineScope.() -> Unit,
) {
    lifecycleScope.launch(
        context = Dispatchers.Main + CoroutineExceptionHandler { _, throwable ->
            error?.invoke(throwable)
        },
        start = start,
    ) {
        block.invoke(this)
    }
}

fun ViewModel.launchIOSafe(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    error: ((Throwable) -> Unit)? = null,
    block: suspend CoroutineScope.() -> Unit,
) = launchSafe(
        start = start,
        error = error,
        context = Dispatchers.IO,
        block = block,
    )

fun ViewModel.launchSafe(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    error: ((Throwable) -> Unit)? = null,
    context: CoroutineContext = Dispatchers.Main,
    block: suspend CoroutineScope.() -> Unit,
) {
    viewModelScope.launch(
        context = context + CoroutineExceptionHandler { _, throwable ->
            error?.invoke(throwable)
        },
        start = start,
    ) {
        block.invoke(this)
    }
}

suspend fun io(
    block: suspend CoroutineScope.() -> Unit
) = withContext(Dispatchers.IO) { block.invoke(this) }

suspend fun <T> ioWithData(
    block: suspend CoroutineScope.() -> T
) = withContext(Dispatchers.IO) { block.invoke(this) }

suspend fun main(
    block: suspend CoroutineScope.() -> Unit
) {
    withContext(Dispatchers.Main) { block.invoke(this) }
}

fun CoroutineScope.launchSafe(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    error: ((Throwable) -> Unit)? = null,
    block: suspend CoroutineScope.() -> Unit,
) {
    launch(
        context = Dispatchers.Main + CoroutineExceptionHandler { _, throwable ->
            error?.invoke(throwable)
        },
        start = start,
    ) {
        block.invoke(this)
    }
}

fun CoroutineScope.launchIOSafe(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    error: ((Throwable) -> Unit)? = null,
    block: suspend CoroutineScope.() -> Unit
) = launch(
    context = Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
        error?.invoke(throwable)
    },
    start = start,
    block = block
)
