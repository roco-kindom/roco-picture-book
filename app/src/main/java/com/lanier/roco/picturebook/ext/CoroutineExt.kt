package com.lanier.roco.picturebook.ext

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
