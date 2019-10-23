package org.geb

import java.time.Instant

interface Timeline {

	var isEmpty: Boolean

	val begin: Instant

	var last: Instant

	suspend fun update(image: PublishedImage)
}
