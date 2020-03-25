package name.utau.anilyrics

import com.fasterxml.jackson.module.kotlin.registerKotlinModule

internal fun registerKotlinJacksonModules() {
    io.vertx.core.json.jackson.DatabindCodec.mapper().registerKotlinModule()
    io.vertx.core.json.jackson.DatabindCodec.prettyMapper().registerKotlinModule()
}

class PatternsBuilder(private val awsl: (String, String) -> Unit) {
    internal infix fun String.replaceWith(target: String) {
        awsl(this, target)
    }
}

fun multiPatternReplace(patterns: PatternsBuilder.() -> Unit): MultiPattern {
    val map = mutableMapOf<String, String>()
    PatternsBuilder { a, b -> map[a] = b }.apply { this.patterns() }
    val regex = map.keys.map { s ->
        val sb = StringBuilder(s.length + 8)
        s.forEach {
            when (it) {
                '\n' -> sb.append("\\n")
                '\r' -> sb.append("\\r")
                else -> if (it in setOf('[', ']', '(', ')', '{', '}', '\\', '/', '.', '+', '|', '?', '*', '^', '$'))
                    sb.append('\\').append(it)
                else
                    sb.append(it)
            }
        }
        sb.toString()
    }.joinToString(separator = "|").let { Regex(it) }
    return MultiPattern(map, regex)
}

class MultiPattern(private val map: Map<String, String>, private val regex: Regex) {
    fun process(input: String): String {
        var lastIndex = 0
        val sb = StringBuilder()
        regex.findAll(input).fold(sb) { acc, r ->
            val t = r.range.first
            if (lastIndex < t) {
                acc.append(input.slice(lastIndex until t))
            }
            lastIndex = r.range.last + 1
            sb.append(map[r.value])
            acc
        }
        if (lastIndex < input.length) {
            sb.append(input.slice(lastIndex..input.lastIndex))
        }
        return sb.toString()
    }
}