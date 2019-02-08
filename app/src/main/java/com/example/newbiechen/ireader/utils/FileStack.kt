package com.example.newbiechen.ireader.utils

import java.io.File

class FileStack {
    private var node: Node? = null
    private var count = 0

    fun push(fileSnapshot: FileSnapshot) {
        val fileNode = Node(fileSnapshot, node)
        node = fileNode
        count++
    }

    fun pop(): FileSnapshot? {
        if (node == null) {
            return null
        }
        val fileSnapshot = node!!.fileSnapshot
        node = node!!.next
        count--
        return fileSnapshot
    }


    data class Node(var fileSnapshot: FileSnapshot, var next: Node?)
    class FileSnapshot {
        var filePath: String? = null
        var files: List<File>? = null
        var scrollOffset: Int = 0
    }

}