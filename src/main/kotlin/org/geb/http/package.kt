package org.geb.http

import io.github.rybalkinsd.kohttp.dsl.context.HttpContext
import okhttp3.Credentials


fun HttpContext.authorization(username: String, password: String) {
    header {
        "Authorization" to Credentials.basic(username, password)
    }
}