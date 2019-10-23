package org.geb

import java.util.concurrent.Executor
import java.util.concurrent.ForkJoinPool

interface Watcher {

    fun close()

    fun on(executor: Executor = ForkJoinPool.commonPool(), listener: (PublishedImage) -> Unit): Watcher

    fun error(executor: Executor = ForkJoinPool.commonPool(), handler: (Throwable) -> Unit): Watcher
}