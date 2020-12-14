package com.nemesiss.dev.logback_android_logviewer

import java.util.*
import kotlin.collections.ArrayList


interface OnPathChangedListener {
    fun onEnter(oldPath: String, newPath: String)
    fun onExit(oldPath: String, newPath: String, isRoot: Boolean)
    fun onRootDirChanged(newRootDir: String)
}

class PathResolver(val rootPaths: List<String>) {
    var currentRoot = 0
        private set

    private val browseRecord = Stack<String>()

    private val pathChangedListeners = ArrayList<OnPathChangedListener>()

    val atTop: Boolean get() = browseRecord.size == 1

    val currentPath: String get() = browseRecord.peek()

    val currentRootPath: String get() = rootPaths[currentRoot]

    init {
        changeRoot(currentRoot)
    }

    @Throws(IndexOutOfBoundsException::class)
    @Synchronized
    fun changeRoot(index: Int) {
        val validRange = rootPaths.indices
        if (!validRange.contains(index)) {
            throw IndexOutOfBoundsException()
        }
        currentRoot = index
        browseRecord.clear()
        browseRecord.push(rootPaths[currentRoot])
        broadcastChangeRootEvent(rootPaths[currentRoot])
    }

    fun enter(directoryName: String): String {
        val currentPath = browseRecord.peek()
        val nextPath = buildNextDirectoryPath(currentPath, directoryName)
        browseRecord.push(nextPath)
        broadcastOnEnterEvent(currentPath, nextPath)
        return nextPath
    }

    fun exit(): String {
        if (currentPath == currentRootPath) {
            return currentRootPath
        }
        val currentPath = browseRecord.pop()
        val nextPath = browseRecord.peek()
        broadcastOnExitEvent(currentPath, nextPath)
        return nextPath
    }

    fun refresh() {
        broadcastChangeRootEvent(browseRecord.peek())
    }

    fun addPathChangedListener(listener: OnPathChangedListener) {
        pathChangedListeners.add(listener)
    }

    fun removePathChangedListener(listener: OnPathChangedListener): Boolean = pathChangedListeners.remove(listener)

    private fun broadcastChangeRootEvent(nextPath: String) {
        pathChangedListeners.forEach { action -> action.onRootDirChanged(nextPath) }
    }

    private fun broadcastOnEnterEvent(currentPath: String, nextPath: String) {
        pathChangedListeners.forEach { action -> action.onEnter(currentPath, nextPath) }
    }

    private fun broadcastOnExitEvent(currentPath: String, nextPath: String) {
        pathChangedListeners.forEach { action -> action.onExit(currentPath, nextPath, currentPath == currentRootPath) }
    }

    companion object {
        @JvmStatic
        fun buildNextDirectoryPath(currentAbsPath: String, nextDirName: String): String =
            "${currentAbsPath}/${nextDirName}"

        fun buildPrevDirectoryPath(currentAbsPath: String): String =
            if (currentAbsPath == "/") currentAbsPath else currentAbsPath.substring(0, currentAbsPath.lastIndexOf("/"))
    }
}