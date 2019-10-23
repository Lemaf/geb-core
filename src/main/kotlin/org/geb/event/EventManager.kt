package org.geb.event

import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.ForkJoinPool
import kotlin.collections.LinkedHashMap

class EventManager<E : Any>(private val executor: Executor) {

    private var failed: Exception? = null

    private val listeners = LinkedHashMap<Class<*>, LinkedList<Entry>>()
    private val handlers = LinkedList<Handler>()
    private var failure: Throwable? = null

    operator fun <T> invoke(value: T): Invoker<T, E> {
        return Invoker(value, this)
    }

    inline fun <reified T : E> listen(executor: Executor, noinline listener: (T) -> Unit) {
        listen(T::class.java, executor, listener)
    }

    fun <T : E> listen(kind: Class<T>, executor: Executor, listener: (T) -> Unit) {
        if (kind !in listeners) {
            listeners[kind] = LinkedList()
        }

        listeners[kind]?.addLast(Entry(executor, listener as (E) -> Unit)) ?: throw IllegalStateException()
    }

    fun exception(cause: Throwable) {
        executor.execute {
            for (handler in handlers)
                handler(cause)
        }
    }

    fun fail(cause: Throwable) {
        if (failure == null) {
            failure = cause
            exception(cause)
        }
    }

    fun fire(event: E) {
        executor.execute {
            if (event.javaClass in listeners) {
                listeners[event.javaClass]?.forEach {
                    it(event)
                }
            }
        }
    }

    fun onException(executor: Executor, handler: (Throwable) -> Unit) {
        val h = Handler(executor, handler)
        handlers.addLast(h)

        if (failure != null)
            h(failure!!)
    }

    private inner class Entry(val executor: Executor, val success: (E) -> Unit) {
        operator fun invoke(event: E) {
            executor.execute {
                success(event)
            }
        }
    }

    private inner class Handler(val executor: Executor, val handler: (Throwable) -> Unit) {

        operator fun invoke(cause: Throwable) {
            executor.execute {
                handler(cause)
            }
        }
    }

    private class Proto<E : Any>(val kind: Class<*>, val executor: Executor, val listener: (E) -> Unit)

    class Invoker<I, E : Any> private constructor(private val value: I, private val manager: EventManager<E>, private val protos: List<Proto<*>>) {

        constructor(value: I, manager: EventManager<E>) : this(value, manager, emptyList())

        operator fun invoke(): I {

            for (proto in protos) {
                manager.listen(proto.kind as Class<E>, proto.executor, proto.executor as (E) -> Unit)
            }

            return value
        }

        inline fun <reified T : E> listen(executor: Executor = ForkJoinPool.commonPool(), noinline listener: (T) -> Unit): Invoker<I, E> {
            return listen(T::class.java, executor, listener)
        }

        fun <T : E> listen(kind: Class<T>, executor: Executor = ForkJoinPool.commonPool(), listener: (T) -> Unit): Invoker<I, E> {
            return Invoker(value, manager, protos + Proto(kind, executor, listener as (E) -> Unit))
        }

        fun onException(executor: Executor, handler: (Throwable) -> Unit): I {
            manager.onException(executor, handler)
            return value
        }
    }
}