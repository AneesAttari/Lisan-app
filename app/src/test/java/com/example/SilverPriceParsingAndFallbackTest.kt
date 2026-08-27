package com.example

import com.example.data.provider.SilverHtmlParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SilverPriceParsingAndFallbackTest {

    @Test
    fun testSingleLineHtmlParsing() {
        val html = "<div><h2>Silver Rate in Pakistan</h2><p>1 Tola Silver: Rs. 3,350</p></div>"
        val price = SilverHtmlParser.extractTolaPrice(html)
        assertNotNull("Should extract price from single-line HTML", price)
        assertEquals(3350.0, price!!, 0.01)
    }

    @Test
    fun testMultiLineHtmlTableParsing() {
        val html = """
            <table class="table table-bordered">
                <thead>
                    <tr>
                        <th>Weight</th>
                        <th>Silver Rate (PKR)</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>1 Tola</td>
                        <td>
                            <span class="price">Rs. 3,420</span>
                        </td>
                    </tr>
                    <tr>
                        <td>10 Gram</td>
                        <td>Rs. 2,932</td>
                    </tr>
                </tbody>
            </table>
        """.trimIndent()

        val price = SilverHtmlParser.extractTolaPrice(html)
        assertNotNull("Should extract price from multi-line HTML table", price)
        assertEquals(3420.0, price!!, 0.01)
    }

    @Test
    fun testWhitespaceAndCarriageReturnVariations() {
        val html = "<tr>\r\n\t<td>\r\n\t\t1  Tola  Silver\r\n\t</td>\r\n\t<td>\r\n\t\tRs.   3,380.50\r\n\t</td>\r\n</tr>"
        val price = SilverHtmlParser.extractTolaPrice(html)
        assertNotNull("Should tolerate arbitrary whitespace, tabs, and \\r\\n", price)
        assertEquals(3380.50, price!!, 0.01)
    }

    @Test
    fun testNestedTagsAndSpans() {
        val html = """
            <div class="card silver-rate-card">
                <div class="card-header">
                    <h3><b>Silver Rate Today</b></h3>
                </div>
                <div class="card-body">
                    <div class="row">
                        <span class="label">1 Tola</span>
                    </div>
                    <div class="row">
                        <span class="currency">PKR</span>
                        <span class="val">3,490</span>
                    </div>
                </div>
            </div>
        """.trimIndent()

        val price = SilverHtmlParser.extractTolaPrice(html)
        assertNotNull("Should extract price across complex nested divs and spans", price)
        assertEquals(3490.0, price!!, 0.01)
    }

    @Test
    fun testUrduScriptHtmlParsing() {
        val html = """
            <div dir="rtl">
                <h3>چاندی کی قیمت پاکستان میں</h3>
                <p>1 تولہ چاندی: 3,350 روپے</p>
            </div>
        """.trimIndent()

        val price = SilverHtmlParser.extractTolaPrice(html)
        assertNotNull("Should extract price with Urdu script labels", price)
        assertEquals(3350.0, price!!, 0.01)
    }

    @Test
    fun testMalformedHtmlHandling() {
        val malformed = "<div><p>Silver 1 Tola <span>3350</td></tr>"
        val price = SilverHtmlParser.extractTolaPrice(malformed)
        assertNotNull("Should handle unclosed or malformed tags gracefully", price)
        assertEquals(3350.0, price!!, 0.01)
    }

    @Test
    fun testNoSilverRatesReturnsNull() {
        val html = "<html><body><h1>News Today</h1><p>General market update with no rates.</p></body></html>"
        val price = SilverHtmlParser.extractTolaPrice(html)
        assertNull("Should safely return null when no silver rate exists", price)
    }

    @Test
    fun testGoldPriceIgnoredOutsideSilverRange() {
        // Gold rate of 250,000 should be rejected by silver validator (1,500..15,000)
        val html = "<div>1 Tola Gold: Rs. 250,000</div>"
        val price = SilverHtmlParser.extractTolaPrice(html)
        assertNull("Gold rate outside 1500..15000 range should not be returned as silver price", price)
    }

    @Test
    fun testHaqMehrCalculationFormulas() {
        // Mehr-e-Fatimi: 500 Dirhams = 131.25 Tola / 1530.875g
        val silverRatePerTola = 3350.0
        val fatimiTola = 131.25
        val calculatedMehrFatimi = fatimiTola * silverRatePerTola
        assertEquals(439687.5, calculatedMehrFatimi, 0.01)

        // Gram-based equivalence verification
        val fatimiGrams = 1530.875390625 // 131.25 * 11.6638125
        val ratePerGram = silverRatePerTola / 11.6638125
        val gramCalculatedMehr = fatimiGrams * ratePerGram
        assertEquals(439687.5, gramCalculatedMehr, 0.01)

        // Minimum Shariah Mehr: 10 Dirhams = 2.625 Tola / 30.618g
        val minTola = 2.625
        val calculatedMinMehr = minTola * silverRatePerTola
        assertEquals(8793.75, calculatedMinMehr, 0.01)
    }

    @Test
    fun testCustomSilverRateAndCustomQuantityHaqMehr() {
        val customSilverRatePerTola = 3500.0
        val customQuantityTolas = 50.0

        val calculatedHaqMehr = customQuantityTolas * customSilverRatePerTola
        assertEquals(175000.0, calculatedHaqMehr, 0.01)
    }

    @Test
    fun testCustomRateOverride() {
        val liveRate = 3350.0
        val userCustomRate = 3500.0
        val isCustomMode = true

        val effectiveRate = if (isCustomMode) userCustomRate else liveRate
        assertEquals(3500.0, effectiveRate, 0.01)
    }
}
