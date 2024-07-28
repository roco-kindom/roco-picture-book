package com.lanier.roco.picturebook.manager

sealed class SyncAction {

    data object Loading : SyncAction()
    data class Completed(val success: Boolean, val thr: Throwable? = null) : SyncAction()
}