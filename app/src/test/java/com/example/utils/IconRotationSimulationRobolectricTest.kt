package com.example.utils

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class IconRotationSimulationRobolectricTest {

    private lateinit var context: Context
    private lateinit var settingsRepository: SettingsRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        settingsRepository = SettingsRepository(context)
        runBlocking {
            val now = System.currentTimeMillis()
            IconRotationManager.setAutoRotationEnabled(context, settingsRepository, true)
            IconRotationManager.setManualIcon(context, settingsRepository, "Normal")
            settingsRepository.updateSettings {
                it.copy(
                    lastAppOpenTimestamp = now,
                    lastIconChangeTimestamp = now
                )
            }
        }
    }

    private fun getEnabledAliases(): List<String> {
        val pm = context.packageManager
        val allAliases = listOf(
            IconRotationManager.ALIAS_NORMAL,
            IconRotationManager.ALIAS_INACTIVE
        )
        return allAliases.filter { alias ->
            val comp = IconRotationManager.getAliasComponentName(context, alias)
            val state = pm.getComponentEnabledSetting(comp)
            state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        }
    }

    @Test
    fun test12HoursInactivity_doesNotTriggerVisualEffect() = runBlocking {
        // Start in Normal State
        IconRotationManager.setManualIcon(context, settingsRepository, "Normal")
        val initialSettings = settingsRepository.userSettingsFlow.first()
        assertEquals("Normal", initialSettings.currentIconKey)

        // Simulate 12 hours of inactivity (< 24h)
        val changed = IconRotationManager.simulateTimeElapsedForTesting(context, settingsRepository, 12L)
        assertFalse("12h inactivity should NOT trigger visual effect", changed)

        val settingsAfter12h = settingsRepository.userSettingsFlow.first()
        assertEquals("Normal", settingsAfter12h.currentIconKey)
    }

    @Test
    fun test24HoursInactivity_triggersSubtleVisualEffect() = runBlocking {
        IconRotationManager.setManualIcon(context, settingsRepository, "Normal")

        // Simulate 24 hours of inactivity (= 24h)
        val changed = IconRotationManager.simulateTimeElapsedForTesting(context, settingsRepository, 24L)
        assertTrue("24h inactivity SHOULD trigger subtle visual effect", changed)

        val updatedSettings = settingsRepository.userSettingsFlow.first()
        assertEquals("Inactive", updatedSettings.currentIconKey)

        // Verify PackageManager component state
        val pm = context.packageManager
        val normalComp = IconRotationManager.getAliasComponentName(context, IconRotationManager.ALIAS_NORMAL)
        val inactiveComp = IconRotationManager.getAliasComponentName(context, IconRotationManager.ALIAS_INACTIVE)

        assertEquals(PackageManager.COMPONENT_ENABLED_STATE_ENABLED, pm.getComponentEnabledSetting(inactiveComp))
        assertEquals(PackageManager.COMPONENT_ENABLED_STATE_DISABLED, pm.getComponentEnabledSetting(normalComp))

        // Verify ONLY ONE alias is enabled
        val enabledAliases = getEnabledAliases()
        assertEquals(1, enabledAliases.size)
        assertEquals(IconRotationManager.ALIAS_INACTIVE, enabledAliases.first())
    }

    @Test
    fun test25HoursInactivity_remainsInSubtleEffectWithoutFurtherRotation() = runBlocking {
        IconRotationManager.setManualIcon(context, settingsRepository, "Normal")

        // 24h: Transitions to Inactive
        val firstChange = IconRotationManager.simulateTimeElapsedForTesting(context, settingsRepository, 24L)
        assertTrue(firstChange)

        // 25h: Stays in subtle inactive effect, does NOT change to another icon
        val secondChange = IconRotationManager.simulateTimeElapsedForTesting(context, settingsRepository, 1L)
        assertFalse("25h should remain in subtle inactive effect without secondary rotation", secondChange)

        val settingsAfter25h = settingsRepository.userSettingsFlow.first()
        assertEquals("Inactive", settingsAfter25h.currentIconKey)
    }

    @Test
    fun test48HoursInactivity_remainsInSubtleEffectWithoutFurtherRotation() = runBlocking {
        IconRotationManager.setManualIcon(context, settingsRepository, "Normal")

        // 48 hours of inactivity -> transitions to Inactive and stays there
        val changed = IconRotationManager.simulateTimeElapsedForTesting(context, settingsRepository, 48L)
        assertTrue(changed)

        val settings = settingsRepository.userSettingsFlow.first()
        assertEquals("Inactive", settings.currentIconKey)

        // Additional time passed (e.g. 24h more -> 72h) does not trigger any further icon swap
        val furtherChange = IconRotationManager.simulateTimeElapsedForTesting(context, settingsRepository, 24L)
        assertFalse("Additional inactivity should NOT rotate to any other icon", furtherChange)
        assertEquals("Inactive", settingsRepository.userSettingsFlow.first().currentIconKey)
    }

    @Test
    fun testOpeningAppAfterInactivity_restoresNormalIconAndResetsTimer() = runBlocking {
        // Set to Inactive state (as after 24h inactivity)
        IconRotationManager.setManualIcon(context, settingsRepository, "Inactive")
        val inactiveSettings = settingsRepository.userSettingsFlow.first()
        assertEquals("Inactive", inactiveSettings.currentIconKey)

        // User opens the app!
        IconRotationManager.onAppOpened(context, settingsRepository)

        val restoredSettings = settingsRepository.userSettingsFlow.first()
        assertEquals("Normal", restoredSettings.currentIconKey)
        assertTrue("lastAppOpenTimestamp must be updated", restoredSettings.lastAppOpenTimestamp > 0)

        // Verify PackageManager has restored MainActivityAliasNormal
        val pm = context.packageManager
        val normalComp = IconRotationManager.getAliasComponentName(context, IconRotationManager.ALIAS_NORMAL)
        val inactiveComp = IconRotationManager.getAliasComponentName(context, IconRotationManager.ALIAS_INACTIVE)

        assertEquals(PackageManager.COMPONENT_ENABLED_STATE_ENABLED, pm.getComponentEnabledSetting(normalComp))
        assertEquals(PackageManager.COMPONENT_ENABLED_STATE_DISABLED, pm.getComponentEnabledSetting(inactiveComp))
    }

    @Test
    fun testAutoEffectDisabled_doesNotApplyInactivityEffect() = runBlocking {
        IconRotationManager.setManualIcon(context, settingsRepository, "Normal")
        IconRotationManager.setAutoRotationEnabled(context, settingsRepository, false)

        val changed = IconRotationManager.simulateTimeElapsedForTesting(context, settingsRepository, 48L)
        assertFalse("When auto effect is disabled, 48h simulation should NOT apply inactivity effect", changed)

        val currentSettings = settingsRepository.userSettingsFlow.first()
        assertEquals("Normal", currentSettings.currentIconKey)
    }

    @Test
    fun testNoSpecialEventStatesOrMultipleIdentitiesExist() {
        val pm = context.packageManager
        val pkg = context.packageName

        // Verify only Normal and Inactive exist
        val normalComp = IconRotationManager.getAliasComponentName(context, IconRotationManager.ALIAS_NORMAL)
        val inactiveComp = IconRotationManager.getAliasComponentName(context, IconRotationManager.ALIAS_INACTIVE)

        // Non-existent obsolete aliases should not be enabled
        val ramadanComp = ComponentName(pkg, "$pkg.MainActivityAliasRamadan")
        val eidComp = ComponentName(pkg, "$pkg.MainActivityAliasEid")

        assertEquals(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, pm.getComponentEnabledSetting(ramadanComp))
        assertEquals(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, pm.getComponentEnabledSetting(eidComp))
    }
}
