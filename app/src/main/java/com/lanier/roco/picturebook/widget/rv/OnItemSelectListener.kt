package com.lanier.roco.picturebook.widget.rv

interface OnItemSelectListener<T> {

    fun onItemSelected(data: T, position: Int)
}