package com.example.newbiechen.ireader.utils

import java.util.concurrent.Executors

private val IO_EXECUTOR = Executors.newSingleThreadExecutor()

/**
 * Utility method to run blocks on a dedicated background thread, used for io/database work
 */
fun io_thread(f: () -> Unit) {
    IO_EXECUTOR.execute(f)
}