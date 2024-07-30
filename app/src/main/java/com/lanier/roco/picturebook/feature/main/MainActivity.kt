package com.lanier.roco.picturebook.feature.main

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.lanier.roco.picturebook.R
import com.lanier.roco.picturebook.database.entity.Spirit
import com.lanier.roco.picturebook.ext.launchSafe
import com.lanier.roco.picturebook.ext.toast
import com.lanier.roco.picturebook.feature.setting.SettingsActivity
import com.lanier.roco.picturebook.manager.AppData
import com.lanier.roco.picturebook.manager.SyncAction
import com.lanier.roco.picturebook.manager.SyncType
import com.lanier.roco.picturebook.widget.CommonLoading
import com.lanier.roco.picturebook.widget.rv.EqualDivider
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

    private val rv by lazy {
        findViewById<RecyclerView>(R.id.recyclerview)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_item, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_sync) {
            val fromServer = AppData.syncType == SyncType.Server
            dialog(
                message = "是否从${if (fromServer) "服务器" else "缓存文件"}同步?",
                negativeText = "取消",
                positive = {
                    viewmodel.sync(fromServer)
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
        val divider = ContextCompat.getDrawable(this, R.drawable.equal_divider)
        rv.addItemDecoration(EqualDivider(divider!!, 3))

        val defPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val defType = defPreferences.getString(getString(R.string.key_sync_type), "2") // def load from cache file
        AppData.syncType = when (defType) {
            SyncType.syncOfServer -> {
                SyncType.Server
            }
            SyncType.syncOfCacheFile -> {
                SyncType.CacheFile
            }
            else -> {
                SyncType.CacheFile
            }
        }

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
                            toast("同步完成")
                            AppData.SPData.syncTime = System.currentTimeMillis()
                            viewmodel.load(true)
                        } else {
                            dialog(it.thr?.message?:"Unknown Error", cancelable = false)
                        }
                    }
                    SyncAction.Loading -> {
                        showLoading(false)
                    }
                }
            }
        }
    }

    private fun dialog(
        message: String,
        cancelable: Boolean = true,
        negativeText: String? = null,
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
        val builder = AlertDialog.Builder(this)
            .setTitle("提示")
            .setMessage(message)
            .setCancelable(cancelable)
            .setPositiveButton("确定", onClickListener)
        negativeText?.let {
            builder.setNegativeButton(it, onClickListener)
        }
        builder.show()
    }

    private fun showLoading(cancelable: Boolean = true) {
        loadingDialog?.dismiss()
        loadingDialog = CommonLoading.newInstance(cancelable)
        loadingDialog?.show(supportFragmentManager, CommonLoading::class.java.simpleName)
    }

    private fun dismissLoading() {
        loadingDialog?.dismiss()
    }
}