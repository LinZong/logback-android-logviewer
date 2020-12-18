package com.nemesiss.dev.logback_android_logviewer.adapter.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nemesiss.dev.logback_android_logviewer.R
import com.nemesiss.dev.logback_android_logviewer.adapter.view.FileType.*
import kotlinx.android.synthetic.main.file_item_layout.view.*
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

enum class FileType {
    FOLDER, FILE, EMPTY_LIST_HOLDER
}

private const val NORMAL_VH = 0
private const val EMPTY_VH = 1

data class FileItemVO(val fileType: FileType, val fileName: String, val lastModifiedMills: Long, val original: File?)

interface OnFileItemClickedListener {
    fun onClicked(fileItemVO: FileItemVO)
}

class FileListAdapter(private var rootDir: File) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class FileListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: FileItemVO, itemClickedListener: OnFileItemClickedListener?) {
            val context = itemView.context
            val dateFormatter = SimpleDateFormat("MMM dd | HH:mm", Locale.getDefault())
            itemView.apply {
                logviewer_filetype_image.setImageDrawable(
                    when (item.fileType) {
                        FOLDER -> context.getDrawable(R.drawable.folder)
                        else -> context.getDrawable(R.drawable.file)
                    }
                )
                logviewer_filename.text = item.fileName
                logviewer_file_modified_time.text = dateFormatter.format(Date(item.lastModifiedMills))
                if (itemClickedListener != null) {
                    setOnClickListener { itemClickedListener.onClicked(item) }
                }
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, vhType: Int): RecyclerView.ViewHolder {
        return when (vhType) {
            NORMAL_VH ->
                FileListViewHolder(
                    LayoutInflater.from(viewGroup.context).inflate(R.layout.file_item_layout, viewGroup, false)
                )

            else ->
                object : RecyclerView.ViewHolder(
                    LayoutInflater.from(viewGroup.context).inflate(R.layout.file_list_empty_layout, viewGroup, false)
                ) {}
        }
    }

    override fun getItemCount(): Int = currentFileVoList.size

    override fun onBindViewHolder(vh: RecyclerView.ViewHolder, index: Int) {
        if (vh is FileListViewHolder) {
            vh.bind(currentFileVoList[index], fileItemClickedListener)
        }
    }

    override fun getItemViewType(position: Int): Int = when (currentFileVoList[position].fileType) {
        EMPTY_LIST_HOLDER -> EMPTY_VH
        else -> NORMAL_VH
    }

    private var currentFileVoList: List<FileItemVO> = emptyList()
    var fileItemClickedListener: OnFileItemClickedListener? = null

    init {
        setRootDirs(rootDir)
    }

    fun setRootDirs(rootDir: File) {
        this.rootDir = rootDir
        parseCurrentRootDir()
    }

    private fun setEmptyPlaceholder() {
        currentFileVoList = listOf(FileItemVO(EMPTY_LIST_HOLDER, "", 0L, null))
        notifyDataSetChanged()
    }

    private fun parseCurrentRootDir() {
        if (rootDir.isFile) {
            setEmptyPlaceholder()
            return
        }
        try {
            var fileItems = rootDir.listFiles()
            if (fileItems == null) {
                setEmptyPlaceholder()
                return
            }
            fileItems = fileItems.filterNotNull().toTypedArray()
            val dirs = fileItems.filter { file -> file.isDirectory }.sortedBy { dir -> dir.name }
            val files = fileItems.filter { file -> file.isFile }.sortedBy { file -> file.name }
            val finalFiles = dirs + files
            currentFileVoList = finalFiles.map { item -> convertFileToFileItemVO(item) }
            if (currentFileVoList.isEmpty()) {
                setEmptyPlaceholder()
                return
            }
            // it's not empty.
            notifyDataSetChanged()
        } catch (e: Exception) {
            setEmptyPlaceholder()
        }
    }

    companion object {
        private fun convertFileToFileItemVO(file: File): FileItemVO {
            val type = if (file.isDirectory) FOLDER else FILE
            val name = file.name
            val lastModifiedMills = file.lastModified()
            return FileItemVO(type, name, lastModifiedMills, file)
        }
    }
}