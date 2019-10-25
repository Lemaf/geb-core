package org.geb

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import java.time.Duration
import java.time.Instant

interface Crawler {

    val delay: Duration

    val id: String

    val initialDelay: Duration

    val sensor: Sensor

    suspend fun download(image: ImageDescription, imageStore: ImageStore): PublishedImage

    suspend fun process(image: ImageDescription, store: ImageStore): PublishedImage

    fun publish(image: ImageDescription): PublishedImage

    suspend fun search(begin: Instant, scope: CoroutineScope): ReceiveChannel<ImageDescription>
}
