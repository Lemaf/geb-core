package org.geb.copernicus

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient
import org.geb.*
import java.time.Duration
import java.time.Instant

class CopernicusCrawler(
        override val id: String,
        override val initialDelay: Duration,
        override val delay: Duration,
        val client: OkHttpClient,
        val research: Research
) : Crawler {

    override val sensor: Sensor
        get() = TODO("not implemented")

    override suspend fun download(image: ImageDescription, imageStore: ImageStore): PublishedImage {
        TODO("not implemented")
    }

    override suspend fun process(image: ImageDescription, store: ImageStore): PublishedImage {
        TODO("not implemented")
    }

    override fun publish(image: ImageDescription): PublishedImage {
        TODO("not implemented")
    }

    override suspend fun search(begin: Instant, scope: CoroutineScope): ReceiveChannel<ImageDescription> {
        return research.search(client, begin, scope)
    }
}