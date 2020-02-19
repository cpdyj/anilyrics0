package name.utau.anilyrics

import name.utau.anilyrics.utils.negativeIndexSlice
import name.utau.anilyrics.utils.tryMeasureSize
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class NegativeIndexKtTest {

    @Test
    fun slice() {
        val list = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8)
        assertEquals(list.negativeIndexSlice(1..5) { 0 }, listOf(1, 2, 3, 4, 5))
        assertEquals(list.negativeIndexSlice(0..1) { 0 }, listOf(0, 1))
        assertEquals(list.negativeIndexSlice(1..0) { 0 }, emptyList<Int>())
        assertEquals(list.negativeIndexSlice(1..1) { 0 }, listOf(1))
        assertEquals(list.negativeIndexSlice(0..0) { 0 }, listOf(0))
        assertEquals(list.negativeIndexSlice(0..20) { it }, (0..20).toList())

        assertEquals(list.negativeIndexSlice(-5..-1) { 0 }, listOf(4, 5, 6, 7, 8))
        assertEquals(list.negativeIndexSlice(-5..1) { 0 }, listOf(4, 5, 6, 7, 8, 0, 1))
        assertEquals(list.negativeIndexSlice(0..5 step 2) { 0 }, listOf(0, 2, 4))
        assertEquals(list.negativeIndexSlice(0..6 step 2) { 0 }, listOf(0, 2, 4, 6))
        assertEquals(listOf(0, 0, 0, 0, 1, 2, 3), listOf(1, 2, 3).negativeIndexSlice(-7..-1) { 0 })

        assertEquals(list.negativeIndexSlice(5 downTo 0) { 0 }, listOf(5, 4, 3, 2, 1, 0))
        assertEquals(list.negativeIndexSlice(3 downTo -3) { 0 }, listOf(3, 2, 1, 0, 8, 7, 6))
        assertEquals(list.negativeIndexSlice(3 downTo -3 step 2) { 0 }, listOf(3, 1, 8, 6))
    }

    @Test
    fun measureSizeOrDefault() {
        assertEquals(5, (1..5).tryMeasureSize())
        assertEquals(0, (1..0).tryMeasureSize())
        assertEquals(6, (0..10 step 2).tryMeasureSize())
        assertEquals(6, (10 downTo 0 step 2).tryMeasureSize())

        assertEquals(3, listOf(1, 2, 3).tryMeasureSize())
    }
}