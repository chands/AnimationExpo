package com.example.animationexpo

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class AnimationExpoActivityTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<AnimationExpoActivity>()

    @Test
    fun gallery_showsRegisteredDemos() {
        composeRule.onNodeWithText("Animation Expo").assertIsDisplayed()
        composeRule.onNodeWithTag("demo-card-receipt").assertIsDisplayed()
        composeRule.onNodeWithTag("demo-card-ixigo-logo").assertIsDisplayed()
    }

    @Test
    fun selectingIxigoDemo_opensDetailAndBackReturnsToGallery() {
        composeRule.onNodeWithTag("demo-card-ixigo-logo").performClick()
        composeRule.onNodeWithText("ixigo Route Reveal").assertIsDisplayed()
        composeRule.onNodeWithTag("demo-back").performClick()
        composeRule.onNodeWithTag("demo-card-receipt").assertIsDisplayed()
    }
}
