package name.utau.anilyrics.a

import name.utau.anilyrics.multiPatternReplace
import java.io.File
import java.util.*


class AC {
    class Node(val deep: Int) {
        val child: MutableMap<Char, Node> = mutableMapOf()
        lateinit var failPtr: Node
        val target: MutableList<String> = mutableListOf()
        override fun toString(): String = "{$child -> $target}"
    }

    data class Result(val source: String, val kanaList: List<Segment>) {
        fun toBracketString(): String {
            var nextChIndex = 0
            val result = StringBuilder()
            kanaList.forEach {
                if (it.range.first > nextChIndex) {
                    result.append(source.substring(nextChIndex until it.range.first))
                }
                nextChIndex = it.range.last + 1
                result.append(source.substring(it.range)).append('(').append(it.target).append(')')
            }
            if (nextChIndex < source.length)
                result.append(source.substring(nextChIndex..source.lastIndex))
            return result.toString()
        }
    }


    data class Segment(val range: IntRange, val target: String)

    val root = Node(-1)

    private class Results {
        val list = LinkedList<Segment>()
        fun put(segment: Segment) {
            list.descendingIterator().forEach {
                TODO()
            }
            while (list.isNotEmpty()) {
                if (list.last.range.first in segment.range)
                    list.removeLast()
                else
                    break
            }
            if (list.isEmpty() || segment.range.first !in list.last.range) // if not overlapped.
                list.addLast(segment)//.apply { println("put: $segment") }
        }
    }

    fun query(source: String): Result {
        val r = Results()
        var curr = root
        source.forEachIndexed { index, ch ->
            // for each character: find the correct next node(state) in it's children or failure pointers.
            curr = findChild(ch, curr)?.also {
                // found: collect all result to set
                it
            } ?: root // not found: goto root and continue
            findTerminal(curr, r, index, source)
        }

        return Result(source, r.list)
    }

    private tailrec fun findTerminal(node: Node, results: /*MutableList<Segment>*/Results, pos: Int, source: String) {
        // recursive find all target until root.
        if (node!=root){
            // root have no target
            val nextKanaRomaji = if (pos + 1 < source.length) kanaMap[source[pos + 1]] else null
            println("nextKanaRomaji: $nextKanaRomaji")
            val tg=node.target.find { it.last() !in 'a'..'z' || it.last()==nextKanaRomaji }
            if (tg != null) {
                println(tg)
                results.put(Segment(range = (pos - node.deep)..pos, target = tg))
            }
            findTerminal(node.failPtr,results, pos, source)
        }
    }

    private tailrec fun findChild(ch: Char, node: Node): Node? {
        // find correct child node or recursive failure pointer until root
        node.child[ch]?.let { return it }
        if (node == root) return null
        return findChild(ch, node.failPtr)
    }


    fun insertWord(word: String, target: String): Unit = createNode(word, target, root, 0)

    private tailrec fun createNode(word: String, target: String, currentNode: Node, index: Int) {
        if (index <= word.lastIndex) {
            val ch=word[index]
            val t = currentNode.child[ch] ?: Node(deep = index).also { currentNode.child[ch] = it }
            if (index == word.lastIndex) {
                t.target.add(target)
            }
            createNode(word, target, t, index + 1)
        }
    }

    fun buildAC() {
        val queue = LinkedList<Node>()
        queue.addLast(root)
        root.failPtr = root
        while (queue.isNotEmpty()) {
            val p = queue.removeFirst()!!
            p.child.forEach { (ch, node) ->
                var failTo: Node = p.failPtr
                var resolved = false
                while (failTo != root && !resolved) {
                    failTo.child[ch]?.also { failTo = it; resolved = true }
                }
                node.failPtr = failTo
                queue.addLast(node)
            }
        }

    }


    private fun Char.isKana() = this in '\u3040'..'\u309f' || this in '\u30a0'..'\u30ff' // hiragana or katakana

    private val hiragana = mapOf(
        'a' to "あいうえお",
        'k' to "かきくけこ",
        's' to "さしすせそ",
        't' to "たちつてと",
        'n' to "なにぬねの",
        'h' to "はひふへほ",
        'm' to "まみむめも",
        'y' to "や　ゆ　よ",
        'r' to "らりるれろ",
        'w' to "わ　　　を",
        'g' to "がぎぐげご",
        'z' to "ざじずぜぞ",
        'd' to "だぢづでど",
        'b' to "ばびぶべぼ",
        'p' to "ぱぴぷぺぽ"
    )

    private val katakana = mapOf(
        'a' to "アイウエオ",
        'k' to "カキクケコ",
        's' to "サシスセソ",
        't' to "タチツテト",
        'n' to "ナニヌネノ",
        'h' to "ハヒフヘホ",
        'm' to "マミムメモ",
        'y' to "ヤ　ユ　ヨ",
        'r' to "ラリルレロ",
        'w' to "ワ　　　ヲ",
        'g' to "ガギグゲゴ",
        'z' to "ザジズゼゾ",
        'd' to "ダヂヅデド",
        'b' to "バビブベボ",
        'p' to "パピプペポ"
    )

    // kana -> first letter of romaji
    private val kanaMap = mutableMapOf<Char, Char>().apply {
        listOf(hiragana, katakana)
            .forEach { it.forEach { (k, vs) -> vs.forEach { v -> this[v] = k } } }
    }
}

fun main() {
    val pattern = Regex("^[A-Za-z0-9\"'` .-]+$|^(#|>).+$")
    val ac = AC()

    val a= multiPatternReplace {
        "aa" replaceWith "00"
        "bb" replaceWith "00"
    }
    println(a.process("aa0bbaaaaab"))

    File("C:\\Users\\iseki\\Desktop\\SKK-JISYO.L.unannotated.utf8").bufferedReader().lineSequence()
        .filterNot { it.startsWith(";;") }
        .map { it.split("/").map { it.trim() } }
        .filterNot { it.first().matches(pattern) }
        .map { list: List<String> ->
            list.subList(1, list.size).forEach { ac.insertWord(it, list.first()) }
        }.count().let { println("total size: $it") }
    ac.buildAC()
    ac.query("静かに訪れる　色なき世界　全ての時間お止め　忘れて　可愛いです").let { println(it) }
}
