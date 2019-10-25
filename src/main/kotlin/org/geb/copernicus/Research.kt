package org.geb.copernicus

import io.github.rybalkinsd.kohttp.dsl.httpGet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import okhttp3.OkHttpClient
import okhttp3.Response
import org.geb.ImageDescription
import org.geb.Polygon
import org.geb.http.authorization
import org.geb.validate
import java.time.Instant

data class Research(val config: Config, val polygon: Polygon, val product: ProductType) {

    private fun footprint(): String {
        return """footprint:"intersects(${polygon.wkt})""""
    }

    private fun position(begin: Instant): String {
        return "(beginPosition:[$begin TO NOW] OR endPosition:[$begin TO NOW])"
    }

    private fun productType(): String {
        return "producttype:${product.id}"
    }

    fun q(begin: Instant): String {
        return StringBuilder()
                .append(productType())
                .append(" AND ")
                .append(position(begin))
                .append(" AND ")
                .append(footprint())
                .toString()
    }

    suspend fun search(client: OkHttpClient, begin: Instant, scope: CoroutineScope): ReceiveChannel<ImageDescription> {
        return scope.produce {
            for (item in AtomPage.consume(request(client, begin))) {

            }
        }
    }

    private fun request(client: OkHttpClient, begin: Instant): (Int) -> Response {

        return { start ->
            httpGet(client) {

                authorization(config.username, config.password)

                param {
                    "q" to q(begin)
                    "rows" to config.pageSize
                    "start" to start
                    "orderby" to "ingestiondate asc"
                }
            }
        }
    }

    enum class ProductType {
        S2MSI2A {
            override val id = "S2MSI2A"
        };

        abstract val id: String
    }

    data class Config(
            val username: String,
            val password: String,
            val pageSize: Int,
            val url: String = "https://scihub.copernicus.eu/dhus/search"
    ) {
        init {
            validate(pageSize in 10..100) {
                IllegalArgumentException("pageSize should be greater than or equal to 10!")
            }
        }
    }

}
