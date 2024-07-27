package com.lanier.roco.picturebook.feature.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.lanier.roco.picturebook.R
import com.lanier.roco.picturebook.database.entity.Spirit
import com.lanier.roco.picturebook.ext.launchSafe
import com.lanier.roco.picturebook.widget.rv.OnItemClickListener
import com.lanier.roco.picturebook.widget.rv.OnLoadMoreListener

class MainActivity : AppCompatActivity() {

    private val viewmodel by viewModels<MainViewModel>()

    private val mAdapter by lazy {
        SpiritAdapter().apply {
            onItemClickListener = object : OnItemClickListener<Spirit> {
                override fun onItemClick(t: Spirit, position: Int) {
                }
            }

            onLoadMoreListener = object : OnLoadMoreListener {
                override fun onLoadMore() {
                    viewmodel.load()
                }
            }
        }
    }

    private val rv by lazy {
        findViewById<RecyclerView>(R.id.recyclerview)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rv.adapter = mAdapter

        launchSafe {
            viewmodel.spirits.observe(this@MainActivity) {
                mAdapter.isEnd = it.third
                if (it.first == 1) {
                    mAdapter.data = it.second
                } else {
                    mAdapter.addData(it.second)
                }
            }
        }
    }
}