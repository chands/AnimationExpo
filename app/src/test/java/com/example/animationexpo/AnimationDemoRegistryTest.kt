package com.example.animationexpo

import com.example.animationexpo.gallery.AnimationDestination
import com.example.animationexpo.gallery.animationDemoSpecs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AnimationDemoRegistryTest {

    @Test
    fun registry_containsExpectedDemosInOrder() {
        val ids = animationDemoSpecs().map { it.id }

        assertEquals(listOf("receipt", "ixigo-logo"), ids)
    }

    @Test
    fun demo_destinations_matchDemoIds() {
        val specs = animationDemoSpecs()

        assertTrue(
            specs.all { spec ->
                (spec.destination as AnimationDestination.Demo).demoId == spec.id
            }
        )
    }
}
