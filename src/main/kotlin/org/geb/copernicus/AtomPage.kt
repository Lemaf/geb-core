package org.geb.copernicus

import io.github.rybalkinsd.kohttp.ext.asStream
import okhttp3.Response
import org.geb.ImageDescription
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

class AtomPage(input: InputStream, start: Int) {

    companion object {

        val docBuilderFactory by lazy {
            val factory = DocumentBuilderFactory.newInstance()
            factory.isNamespaceAware = true
            factory
        }

        val xPathFactory by lazy {
            XPathFactory.newInstance()
        }

        val xPath by lazy {
            xPathFactory.newXPath()
        }

        val xpathNextLink = xPath.compile("""/feed/link[@rel='next']""")

        fun consume(request: (Int) -> Response): Sequence<ImageDescription> {

            var last = AtomPage(request(0), 0)

            return generateSequence(last) {
                if (last.hasNext) {
                    last = last.next(request)
                }

                return@generateSequence last
            }.flatMap {
                return@flatMap it.images
            }
        }
    }

    val images: Sequence<ImageDescription> = emptySequence()

    val hasNext: Boolean
        get() = xpathNextLink.evaluate(document, XPathConstants.NODE) != null


    private val document by lazy {
        docBuilderFactory.newDocumentBuilder().parse(input)
    }

    fun next(request: (Int) -> Response): AtomPage {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

@Suppress("FunctionName")
fun AtomPage(response: Response, start: Int): AtomPage {
    return response.asStream().use {
        AtomPage(it ?: throw IllegalStateException("InputStream is null!"), start)
    }
}
