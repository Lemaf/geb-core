package org.geb


inline fun validate(validation: Boolean,  problem: () -> Throwable) {
    if (!validation)
        throw problem()
}