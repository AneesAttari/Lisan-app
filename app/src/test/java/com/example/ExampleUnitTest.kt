package com.example

import com.example.ui.components.bidiIsolate
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for bidirectional text isolation support.
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testBidiIsolate_pureUrdu_remainsUntouched() {
    val input = "یہ اردو عبارت ہے"
    val result = bidiIsolate(input)
    assertEquals("Pure Urdu should not have isolates inserted", input, result)
  }

  @Test
  fun testBidiIsolate_pureEnglish_getsIsolated() {
    val input = "Simple English sentence."
    val result = bidiIsolate(input)
    assertEquals("Pure English should be wrapped in left-to-right isolate markers", "\u2066$input\u2069", result)
  }

  @Test
  fun testBidiIsolate_mixedText_isolatesEnglishOnly() {
    val input = "انگریزی گرامر کا تجزیہ (English Grammatical Dissection):"
    val expected = "انگریزی گرامر کا تجزیہ \u2066(English Grammatical Dissection):\u2069"
    val result = bidiIsolate(input)
    assertEquals("Only the English segments of a mixed string should be isolated", expected, result)
  }

  @Test
  fun testBidiIsolate_multipleMixedSegments() {
    val input = "If I had known about the meeting, -> یہ شرطیہ جملہ ہے (Type 3 Conditional)"
    val expected = "\u2066If I had known about the meeting, ->\u2069 یہ شرطیہ جملہ ہے \u2066(Type 3 Conditional)\u2069"
    val result = bidiIsolate(input)
    assertEquals("Multiple English segments should each be isolated individually", expected, result)
  }
}
