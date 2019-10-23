package org.geb.copernicus

import kotlinx.coroutines.channels.Channel
import okhttp3.OkHttpClient
import org.geb.*
import java.time.Duration
import java.time.Instant

class OpenCopernicusCrawler(
        override val id: String,
        override val initialDelay: Duration,
        override val delay: Duration
) : Crawler {


    override val sensor: Sensor
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override suspend fun download(image: ImageDescription, imageStore: ImageStore): PublishedImage {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun process(image: ImageDescription, store: ImageStore): PublishedImage {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun publish(image: ImageDescription): PublishedImage {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun search(begin: Instant): Channel<ImageDescription> {

    }

    data class Config(val client: OkHttpClient)
}