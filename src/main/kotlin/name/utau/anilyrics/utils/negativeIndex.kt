package name.utau.anilyrics.utils

import kotlin.math.absoluteValue

/**
 * like [List.slice] but allow negative index and default value.
 *
 * @param indices negative and out of bounds indices is allowed.
 * @param default provide default value for outbounds index.
 */
internal fun <T> List<T>.negativeIndexSlice(
    indices: Iterable<Int>,
    default: (index: Int) -> T = { error("No default value for out of bounds index.") }
): List<T> {
    val size = indices.tryMeasureSize() ?: 10
    val target = ArrayList<T>(size)
    val availRange = 0..this.lastIndex
    indices.forEach {
        val pos = if (it < 0) this.size + it else it
        if (pos in availRange) {
            target.add(this[pos])
        } else {
            target.add(default.invoke(pos))
        }
    }
    return target
}

internal fun Iterable<*>.tryMeasureSize(): Int? =
    when (this) {
        is IntProgression -> {
            this.run {
                when {
                    step > 0 -> if (first > last) 0 else ((last - first) / step) + 1
                    step < 0 -> if (last > first) 0 else ((first - last) / step.absoluteValue) + 1
                    else -> error("step equals zero.")
                }
            }
        }
        is Collection<*> -> this.size
        else -> null
    }