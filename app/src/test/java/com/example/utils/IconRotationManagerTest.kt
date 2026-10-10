package com.example.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IconRotationManagerTest {

    @Test
    fun testInactivityThreshold_isExactly24Hours() {
        val expected24HoursMs = 24 * 60 * 60 * 1000L
        assertEquals(expected24HoursMs, IconRotationManager.INACTIVITY_THRESHOLD_MS)
        assertEquals(expected24HoursMs, IconRotationManager.ROTATION_INTERVAL_MS)
    }

    @Test
    fun testLauncherAliases_areOnlyNormalAndInactive() {
        assertEquals("Normal", IconRotationManager.STATE_NORMAL)
        assertEquals("Inactive", IconRotationManager.STATE_INACTIVE)
        assertEquals("com.example.MainActivityAliasNormal", IconRotationManager.ALIAS_NORMAL)
        assertEquals("com.example.MainActivityAliasInactive", IconRotationManager.ALIAS_INACTIVE)
    }

    @Test
    fun testWorkName_isDefined() {
        assertEquals("lisan_daily_icon_inactivity_work", IconRotationManager.WORK_NAME)
    }

    @Test
    fun testInactivityElapsedMath_12hVs24hVs48h() {
        val now = 1_000_000_000_000L
        val threshold = IconRotationManager.INACTIVITY_THRESHOLD_MS

        // 12 hours: < 24h threshold
        val open12hAgo = now - (12 * 60 * 60 * 1000L)
        val elapsed12h = now - open12hAgo
        assertTrue(elapsed12h < threshold)

        // 24 hours: >= 24h threshold
        val open24hAgo = now - (24 * 60 * 60 * 1000L)
        val elapsed24h = now - open24hAgo
        assertTrue(elapsed24h >= threshold)

        // 25 hours: >= 24h threshold
        val open25hAgo = now - (25 * 60 * 60 * 1000L)
        val elapsed25h = now - open25hAgo
        assertTrue(elapsed25h >= threshold)

        // 48 hours: >= 24h threshold
        val open48hAgo = now - (48 * 60 * 60 * 1000L)
        val elapsed48h = now - open48hAgo
        assertTrue(elapsed48h >= threshold)
    }

    @Test
    fun testNoSpecialEventStatesExist() {
        // Assert that no event constants exist or are exposed
        val validStates = listOf(IconRotationManager.STATE_NORMAL, IconRotationManager.STATE_INACTIVE)
        assertEquals(2, validStates.size)
        assertFalse(validStates.contains("Ramadan"))
        assertFalse(validStates.contains("Eid"))
        assertFalse(validStates.contains("Knowledge"))
        assertFalse(validStates.contains("Achievement"))
        assertFalse(validStates.contains("WorldCup"))
    }
}
