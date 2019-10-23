package org.geb

import io.kotlintest.shouldNotBe
import io.kotlintest.specs.FreeSpec
import io.mockk.mockk
import java.util.concurrent.ForkJoinPool

class GebSpec : FreeSpec({

    "A Geb instance" - {

        "should" {
            val getTimeline = mockk<suspend (String) -> Timeline>()
            val getStore = mockk<suspend (String) -> ImageStore>()

            val geb = Geb(Geb.Config(ForkJoinPool.commonPool(), getStore, getTimeline))

            val crawler = mockk<Crawler>()

            geb.watch(crawler) shouldNotBe null
        }
    }
})