package com.nemesiss.dev.logback_android_logviewer.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.SpannableStringBuilder
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import com.nemesiss.dev.logback_android_logviewer.OnPathChangedListener
import com.nemesiss.dev.logback_android_logviewer.PathResolver
import com.nemesiss.dev.logback_android_logviewer.R
import com.nemesiss.dev.logback_android_logviewer.adapter.io.LogDirectoryAdapter
import com.nemesiss.dev.logback_android_logviewer.adapter.io.LogbackConfigurationPropertyLogDirectoryAdapter
import com.nemesiss.dev.logback_android_logviewer.adapter.view.FileItemVO
import com.nemesiss.dev.logback_android_logviewer.adapter.view.FileListAdapter
import com.nemesiss.dev.logback_android_logviewer.adapter.view.FileType
import com.nemesiss.dev.logback_android_logviewer.adapter.view.OnFileItemClickedListener
import com.nemesiss.dev.logback_android_logviewer.utils.Utils
import kotlinx.android.synthetic.main.activity_log_viewer.*
import java.io.File

class LogFileExplorerActivity : AppCompatActivity() {

    private lateinit var logDirsAdapter: LogDirectoryAdapter
    private lateinit var dirListAdapter: ArrayAdapter<String>
    private lateinit var logDirs: List<String>
    private lateinit var pathResolver: PathResolver
    private lateinit var fileListAdapter: FileListAdapter
    private lateinit var fileListLayoutManager: LinearLayoutManager

    private val pathChangedListAdapter = object : OnPathChangedListener {
        override fun onEnter(oldPath: String, newPath: String) {
            fileListAdapter.setRootDirs(File(newPath))
            logviewer_pathgroup_spinner.safeSetText(newPath)
        }

        override fun onExit(oldPath: String, newPath: String, isRoot: Boolean) {
            fileListAdapter.setRootDirs(File(newPath))
            logviewer_pathgroup_spinner.safeSetText(newPath)
        }

        override fun onRootDirChanged(newRootDir: String) {
            fileListAdapter.setRootDirs(File(newRootDir))
        }
    }

    private val fileItemClickedListener = object : OnFileItemClickedListener {
        override fun onClicked(fileItemVO: FileItemVO) {
            if (fileItemVO.fileType == FileType.FOLDER) {
                pathResolver.enter(fileItemVO.fileName)
            } else {
                // TODO enter code viewer.
                fileItemVO.original?.let { file ->
                    LogFileViewerActivity.start(this@LogFileExplorerActivity, file.absolutePath)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_viewer)
        initComponents()
    }

    private fun initComponents() {
        logDirsAdapter = LogbackConfigurationPropertyLogDirectoryAdapter(this)
        logDirs = logDirsAdapter.getLogDirectoriesAbsStr()
        dirListAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, logDirs)
        pathResolver = PathResolver(logDirs)

        logviewer_pathgroup_spinner.apply {
            setAdapter(dirListAdapter)
            text = SpannableStringBuilder(logDirs[0])
            // bind click event handler.
            setOnItemClickListener { _, _, position, _ ->
                pathResolver.changeRoot(position)
            }
        }

        fileListAdapter = FileListAdapter(File(logDirs[0]))
        fileListAdapter.fileItemClickedListener = fileItemClickedListener
        fileListLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        logviewer_filelist_recyclerview.apply {
            adapter = fileListAdapter
            layoutManager = fileListLayoutManager
        }
        pathResolver.addPathChangedListener(pathChangedListAdapter)
        logviewer_file_swipe_refresh.apply {
            setColorSchemeColors(Color.RED, Color.MAGENTA, Color.GRAY)
            setOnRefreshListener {
                pathResolver.refresh()
                isRefreshing = false
            }
        }
    }

    override fun onDestroy() {
        pathResolver.removePathChangedListener(pathChangedListAdapter)
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (pathResolver.atTop) {
            super.onBackPressed()
        }
        pathResolver.exit()
    }
}
