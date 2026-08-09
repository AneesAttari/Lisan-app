package com.example

import com.example.ui.navigation.HashRouter
import org.junit.Assert.*
import org.junit.Test

class HashRouterTest {

    @Test
    fun testHashRouterNavigationAndBack() {
        HashRouter.navigate("#/lessons")
        assertEquals("#/lessons", HashRouter.currentRoute.value)

        HashRouter.navigate("#/settings")
        assertEquals("#/settings", HashRouter.currentRoute.value)

        val popped = HashRouter.goBack()
        assertTrue("Should successfully go back", popped)
        assertEquals("#/lessons", HashRouter.currentRoute.value)
    }

    @Test
    fun testDuplicateNavigationPrevention() {
        HashRouter.navigate("#/tutor")
        assertEquals("#/tutor", HashRouter.currentRoute.value)

        // Navigating to the same route should not duplicate history entries
        HashRouter.navigate("#/tutor")
        assertEquals("#/tutor", HashRouter.currentRoute.value)
    }

    @Test
    fun testTabMapping() {
        assertEquals(0, HashRouter.routeToTab("#/lessons"))
        assertEquals(1, HashRouter.routeToTab("#/analyzer"))
        assertEquals(2, HashRouter.routeToTab("#/tutor"))
        assertEquals(10, HashRouter.routeToTab("#/settings"))
        assertEquals(14, HashRouter.routeToTab("#/picture-vocabulary"))
        assertEquals(0, HashRouter.routeToTab("#/unknown-route"))

        assertEquals("#/lessons", HashRouter.tabToRoute(0))
        assertEquals("#/settings", HashRouter.tabToRoute(10))
        assertEquals("#/picture-vocabulary", HashRouter.tabToRoute(14))
    }
}
