package com.lanier.roco.picturebook.feature.main

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.preference.PreferenceManager
import com.lanier.roco.picturebook.R
import com.lanier.roco.picturebook.databinding.ActivityMainBinding
import com.lanier.roco.picturebook.ext.toast
import com.lanier.roco.picturebook.feature.main.fragment.SkillDataFragment
import com.lanier.roco.picturebook.feature.main.fragment.SpiritDataFragment
import com.lanier.roco.picturebook.feature.search.SearchActivity
import com.lanier.roco.picturebook.feature.setting.SettingsActivity
import com.lanier.roco.picturebook.manager.AppData
import com.lanier.roco.picturebook.manager.SyncAction
import com.lanier.roco.picturebook.manager.SyncType
import com.lanier.roco.picturebook.utils.FragmentSwitchHelper
import com.lanier.roco.picturebook.widget.CommonLoading

class MainActivity : AppCompatActivity() {

    private val viewmodel by viewModels<MainViewModel>()
    private var loadingDialog : CommonLoading? = null

    private lateinit var binding: ActivityMainBinding

    private val switchFragmentHelper by lazy {
        FragmentSwitchHelper(
            fragmentManager = supportFragmentManager,
            resId = R.id.fragmentContainer,
        )
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
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setSupportActionBar(findViewById(R.id.toolbar))

        initializedSync()

        switchFragmentHelper.setFragments(
            listOf(
                FragmentSwitchHelper.SwitchFragment(fragment = SpiritDataFragment.newInstance()),
                FragmentSwitchHelper.SwitchFragment(fragment = SkillDataFragment.newInstance()),
            )
        )

        binding.tvSearch.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java).apply {
                putExtra("searchType", switchFragmentHelper.showIndex + 1)
            }
            startActivity(intent)
        }
        binding.btmNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_nav_spirit -> switchFragmentHelper.switchFra(0)
                R.id.menu_nav_skill -> switchFragmentHelper.switchFra(1)
            }
            true
        }

        viewmodel.loadingLiveData.observe(this) {
            if (it) showLoading(false) else dismissLoading()
        }

        viewmodel.syncAction.observe(this@MainActivity) {
            when (it) {
                is SyncAction.Completed -> {
                    if (it.success) {
                        toast("同步完成")
                        AppData.SPData.syncTime = System.currentTimeMillis()
                        viewmodel.load()
                    } else {
                        dialog(it.thr?.message?:"Unknown Error", cancelable = false)
                    }
                }
                SyncAction.Loading -> {}
            }
        }

        switchFragmentHelper.switchFra(0)
    }

    /**
     * 初始化同步相关
     */
    private fun initializedSync() {
        val defPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val defType = defPreferences.getString(
            getString(R.string.key_sync_type),
            "2"
        ) // def load from cache file
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
        AppData.syncWithSkillConfig = defPreferences.getBoolean(
            getString(R.string.key_sync_skill),
            false
        )
        AppData.syncWithManorSeedConfig = defPreferences.getBoolean(
            getString(R.string.key_sync_manor_seeds),
            false
        )
        AppData.syncWithSceneConfig = defPreferences.getBoolean(
            getString(R.string.key_sync_scene),
            false
        )
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