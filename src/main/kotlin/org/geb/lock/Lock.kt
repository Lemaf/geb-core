package org.geb.lock

import java.util.concurrent.locks.ReentrantLock

class Lock {

    val lock = ReentrantLock()

    inline operator fun <R> invoke(block: () -> R): R {
        lock.lock()
        try {
            return block()
        } finally {
            lock.unlock()
        }
    }
}