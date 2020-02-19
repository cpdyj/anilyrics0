package name.utau.anilyrics


import java.io.File
import java.util.LinkedList
import kotlin.Comparator

class HiraganaTagger {
    class Node(val deep: Int) {
        val child: MutableMap<Char, Node> = mutableMapOf()
        lateinit var failPtr: Node
        var terminal: String? = null
    }

    val root = Node(deep = 0)

    fun insertWord(word: String, target: String) {
        var curr = root
        word.forEachIndexed { index, ch ->
            curr = curr.child[ch] ?: Node(deep = index + 1)
                .also { curr.child[ch] = it }
            if (index == word.lastIndex) curr.terminal = target.removeSuffix("―")
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

    fun query(string: String): Result {
        var curr = root
        val results = mutableListOf<Segment>()
        string.forEachIndexed { index, c ->
            curr = findChild(c, curr) ?: run { curr = root; return@forEachIndexed /* not found in root */ }
            findTerminal(curr, index, results)
        }

        return results.asSequence().sortedWith(Comparator { (a, _), (b, _) ->
            if (a.first != b.first)
                compareValues(a.first, b.first)
            else
                compareValues(b.last - b.first, a.last - b.first)
        }).distinctBy { it.range.first }.map {
            val s = string.substring(it.range)
            if (s.last().isKana()) {
                val p = s.indexOfLast { !it.isKana() }
                if (p > -1) { // drop zero kanji prefixed segment
                    Segment(it.range.first..it.range.first + p, it.hiragana)
                } else {
                    null
                }
            } else {
                it
            }
        }.filterNotNull().toList().let { Result(string, it) }
    }

    private tailrec fun findChild(ch: Char, node: Node): Node? =
        node.child[ch] ?: (gana2pron[ch]?.let { node.child[it] })
        ?: if (node == root) null else findChild(ch, node.failPtr)

    private tailrec fun findTerminal(node: Node, index: Int, list: MutableList<Segment>): Unit =
        if (node != root) node.terminal
            ?.let {
                list.add(
                    Segment((index - node.deep + 1)..index, it)
                ); Unit
            }
            ?: Unit else findTerminal(node.failPtr, index, list)

    private fun Char.isKana() = this in '\u3040'..'\u309f' || this in '\u30a0'..'\u30ff' // hiragana or katakana

    class Result(val source: String, val kanaList: List<Segment>) {
        fun toRubyHtml(): String {
            var lastIndex = 0
            val rubyString = StringBuilder()
            kanaList.forEach { (range, kana) ->
                if (range.first > lastIndex) {
                    rubyString.append("<ruby>${source.substring(lastIndex until range.first)}</ruby>")
                }
                rubyString.append("<ruby>${source.slice(range)}<rp>(</rp><rt>$kana</rt><rp>)</rp></ruby>")
                lastIndex = range.last + 1
            }
            return rubyString.toString()
        }

        fun toBracketStyle(): String {
            var lastIndex = 0
            val string = StringBuilder()
            kanaList.forEach { (range, kana) ->
                if (range.first > lastIndex) {
                    string.append(source.substring(lastIndex until range.first))
                }
                string.append("${source.slice(range)}($kana)")
                lastIndex = range.last + 1
            }
            return string.toString()
        }
    }
}

data class Segment(val range: IntRange, val hiragana: String)

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

private val gana2pron = mutableMapOf<Char, Char>().apply {
    listOf(
        hiragana,
        katakana
    ).forEach { it.forEach { (k, vs) -> vs.forEach { v -> this[v] = k } } }
}


fun main() {
    val text =
        File("\"C:\\Users\\iseki\\Documents\\ibus-replace-with-kanji\\dic\\restrained.dic\"".removeSurrounding("\"")).readLines()
            .filterNot { it.startsWith("#") or it.startsWith(";") or it.isBlank() }
            .map { it.split("/").map { it.trim() }.filter { it.isNotBlank() } }
    val ac2 = HiraganaTagger()
    text.forEach { list ->
        list.slice(1..list.lastIndex).forEach {
            ac2.insertWord(it, list.first())
        }
    }
    ac2.buildAC()

    ac2.query(
        """
        静かに訪れる　色なき世界

        全ての時を止め　眠りにつく

        哀しみ喜びを　集めて人は

        流れし時の中　安らぎ見る

        産まれ生き消えてゆく　人の運命の中

        誰も皆　空の星に　かすかな願い託す

        ひそかに輝ける　摩天の星

        地平の彼方へと　流れ消える

        色づき始めた　大地の上を

        無情な時間だけが　吹き抜けてく

        風に揺る　緑の先　明日へと続いて

        新しき時間の中　笑顔の花を咲かす

        産まれ生き消えてゆく　人の運命の中

        誰も皆　空の星に　かすかな願い託す

        静かに訪れる　まばゆき世界

        全ての時を生み　咲き乱れる 
        
    """.trimIndent()
    ).also { println(it.toBracketStyle()) }

}

