package com.zachklipp.compose.backstack.viewer

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.ui.test.assertHasClickAction
import androidx.ui.test.assertIsDisplayed
import androidx.ui.test.assertIsNotSelected
import androidx.ui.test.assertIsSelected
import androidx.ui.test.createComposeRule
import androidx.ui.test.onNodeWithSubstring
import androidx.ui.test.onNodeWithTag
import androidx.ui.test.onNodeWithText
import androidx.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BackstackViewerTest {

  @get:Rule
  val compose = createComposeRule()

  @Test
  fun initialState() {
    compose.setContent {
      BackstackViewerApp()
    }

    compose.onNodeWithText("Slide Transition").assertIsDisplayed()
    compose.onNodeWithSubstring("Slow animations").assertIsDisplayed()

    compose.onNodeWithText("one").assertIsSelected()
    compose.onNodeWithText("one, two").assertIsNotSelected()
    compose.onNodeWithText("one, two, three").assertIsNotSelected()

    compose.onNodeWithText("Screen one").assertIsDisplayed()
    compose.onNodeWithSubstring("Counter:").assertIsDisplayed()
  }

  @Test
  fun transitionBackFromSingleScreen() {
    compose.setContent {
      BackstackViewerApp()
    }

    compose.onNodeWithTag(backTestTag("one")).assertHasClickAction().performClick()
    compose.onNodeWithText("Screen one").assertIsDisplayed()
  }

  @Test
  fun transitionToSecondPrefabBackstack() {
    compose.setContent {
      BackstackViewerApp()
    }

    compose.onNodeWithText("Screen one").assertIsDisplayed()
    compose.onNodeWithText("Screen two").assertDoesNotExist()

    compose.onNodeWithText("one, two")
      .assertIsNotSelected()
      .performClick()
      .assertIsSelected()

    compose.onNodeWithText("Screen one").assertDoesNotExist()
    compose.onNodeWithText("Screen two").assertIsDisplayed()
  }

  @Test
  fun transitionToThirdPrefabBackstack() {
    compose.setContent {
      BackstackViewerApp()
    }

    compose.onNodeWithText("Screen one").assertIsDisplayed()
    compose.onNodeWithText("Screen two").assertDoesNotExist()
    compose.onNodeWithText("Screen three").assertDoesNotExist()

    compose.onNodeWithText("one, two, three")
      .assertIsNotSelected()
      .performClick()
      .assertIsSelected()

    compose.onNodeWithText("Screen one").assertDoesNotExist()
    compose.onNodeWithText("Screen two").assertDoesNotExist()
    compose.onNodeWithText("Screen three").assertIsDisplayed()
  }

  @Test
  fun transitionBackFromPrefabBackstack() {
    compose.setContent {
      BackstackViewerApp()
    }

    compose.onNodeWithText("one, two, three").performClick().assertIsSelected()
    compose.onNodeWithText("Screen three").assertIsDisplayed()

    compose.onNodeWithTag(backTestTag("three")).performClick()
    compose.onNodeWithText("one, two").assertIsSelected()
    compose.onNodeWithText("Screen three").assertDoesNotExist()

    compose.onNodeWithTag(backTestTag("two")).performClick()
    compose.onNodeWithText("one").assertIsSelected()
    compose.onNodeWithText("Screen two").assertDoesNotExist()
  }

  @Test
  fun addScreenWithFab() {
    compose.setContent {
      BackstackViewerApp()
    }

    compose.onNodeWithTag(addTestTag("one")).assertHasClickAction().performClick()
    compose.onNodeWithText("Screen one+").assertIsDisplayed()
    compose.onNodeWithTag(backTestTag("one+")).assertHasClickAction().performClick()
    compose.onNodeWithText("Screen one+").assertDoesNotExist()
  }
}
