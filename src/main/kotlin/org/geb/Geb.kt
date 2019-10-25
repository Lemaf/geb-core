package org.geb

import org.geb.event.EventManager
import org.geb.lock.Lock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.RuntimeException
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService

class Geb(private val config: Config) {

    data class Config(
            val executor: ExecutorService,
            val getStore: suspend (String) -> ImageStore,
            val getTimeline: suspend (String) -> Timeline
    )

    private val dispatcher = config.executor.asCoroutineDispatcher()

    private val eManager = EventManager<Product>(config.executor)

    private val scope = CoroutineScope(dispatcher)

    private val getStore get() = config.getStore

    private val getTimeline get() = config.getTimeline

    fun watch(crawler: Crawler): Watcher {
        return InternalWatcher(crawler)
                .on(config.executor) { image ->
                    eManager.fire(Product(crawler.id, crawler.sensor, image))
                }.error(config.executor) { cause ->
                    eManager.exception(CrawlerException(crawler.id, cause))
                }
    }

    class CrawlerException(message: String, cause: Throwable) : RuntimeException(message, cause)

    private inner class InternalWatcher(private val crawler: Crawler) : Watcher {

        private val lock = Lock()

        private val evtManager = EventManager<PublishedImage>(config.executor)

        private val job = scope.launch {
            try {
                delay(crawler.initialDelay.toMillis())

                val timeline = getTimeline(crawler.id)
                val imageStore = getStore(crawler.id)

                if (timeline.isEmpty) {
                    register(crawler.search(timeline.begin, scope), timeline, imageStore)
                }

                while (true) {
                    delay(crawler.delay.toMillis())
                    register(crawler.search(timeline.last, scope), timeline, imageStore)
                }
            } catch (e: Exception) {
                eManager.fail(e)
            }
        }

        override fun error(executor: Executor, handler: (Throwable) -> Unit): Watcher {
            lock {
                evtManager.onException(executor, handler)
            }

            return this
        }

        override fun on(executor: Executor, listener: (PublishedImage) -> Unit): InternalWatcher {
            lock {
                evtManager(this)
            }

            return this
        }

        override fun close() {
            if (job.isActive) {
                job.cancel()
            }
        }

        private suspend fun register(channel: ReceiveChannel<ImageDescription>, timeline: Timeline, store: ImageStore) {
            for (image in channel) {
                val published = if (image.isReady) {
                    if (image.shouldDownload) {
                        crawler.download(image, store)
                    } else {
                        crawler.publish(image)
                    }
                } else {
                    crawler.process(image, store)
                }

                timeline.update(published)
                evtManager.fire(published)

            }
        }
    }
}