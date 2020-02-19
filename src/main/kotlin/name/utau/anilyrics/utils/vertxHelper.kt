package name.utau.anilyrics.utils

import io.vertx.core.Context
import io.vertx.core.Promise

/**
 * execute lambda to determine promise state
 */
inline fun <T> Promise<T>.makeSense(lambda: () -> T) {
    try {
        this.tryComplete(lambda())
    } catch (e: Throwable) {
        this.tryFail(e)
    }
}


inline fun <reified T> Context.put(value: T) {
    put(T::class.qualifiedName, value)
}

inline fun <reified T> Context.get() = get<T>(T::class.qualifiedName)



