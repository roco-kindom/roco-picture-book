package com.lanier.roco.picturebook.feature.main

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.lanier.roco.picturebook.R
import com.lanier.roco.picturebook.database.entity.Spirit
import com.lanier.roco.picturebook.ext.launchSafe
import com.lanier.roco.picturebook.feature.setting.SettingsActivity
import com.lanier.roco.picturebook.manager.SyncAction
import com.lanier.roco.picturebook.widget.CommonLoading
import com.lanier.roco.picturebook.widget.rv.OnItemClickListener
import com.lanier.roco.picturebook.widget.rv.OnLoadMoreListener

class MainActivity : AppCompatActivity() {

    private val viewmodel by viewModels<MainViewModel>()
    private var loadingDialog : CommonLoading? = null

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

    private val onSharedPreferencesChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            key?.let {
                if (it == getString(R.string.key_sync_type)) {
                    println(">>>> value = ${sharedPreferences.getString(key, "")}")
                }
            }
        }

    private val rv by lazy {
        findViewById<RecyclerView>(R.id.recyclerview)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_item, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_sync) {
            dialog(
                message = "是否从服务器同步?",
                positive = {
                    viewmodel.sync()
                }
            )
        }
        if (item.itemId == R.id.menu_settings) {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))

        rv.adapter = mAdapter

        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(onSharedPreferencesChangeListener)

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

        launchSafe {
            viewmodel.syncAction.observe(this@MainActivity) {
                when (it) {
                    is SyncAction.Completed -> {
                        dismissLoading()
                        if (it.success) {
                            viewmodel.load(true)
                        } else {
                            dialog(it.thr?.message?:"Unknown Error")
                        }
                    }
                    SyncAction.Loading -> {
                        showLoading()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(onSharedPreferencesChangeListener)
    }

    private fun dialog(
        message: String,
        positive: (() -> Unit)? = null,
        negative: (() -> Unit)? = null,
    ) {
        val onClickListener = DialogInterface.OnClickListener { dialog, which ->
            if (which == DialogInterface.BUTTON_POSITIVE) {
                positive?.invoke()
                dialog.dismiss()
            }
            if (which == DialogInterface.BUTTON_NEGATIVE) {
                negative?.invoke()
                dialog.dismiss()
            }
        }
        AlertDialog.Builder(this)
            .setTitle("提示")
            .setMessage(message)
            .setPositiveButton("确定", onClickListener)
            .setNegativeButton("取消", onClickListener)
            .show()
    }

    private fun showLoading() {
        loadingDialog?.dismiss()
        loadingDialog = CommonLoading.newInstance()
        loadingDialog?.show(supportFragmentManager, CommonLoading::class.java.simpleName)
    }

    private fun dismissLoading() {
        loadingDialog?.dismiss()
    }
}