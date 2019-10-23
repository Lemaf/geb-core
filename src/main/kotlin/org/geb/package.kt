package org.geb

sealed class Try<T>

data class Success<T>(val value: T) : Try<T>()

data class Failure<T>(val cause: Throwable) : Try<T>()