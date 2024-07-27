package com.lanier.roco.picturebook.widget.rv

interface OnItemClickListener<T> {

    fun onItemClick(t: T, position: Int)
}