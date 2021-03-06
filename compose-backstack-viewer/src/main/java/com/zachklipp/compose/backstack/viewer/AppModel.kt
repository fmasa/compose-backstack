package com.zachklipp.compose.backstack.viewer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.savedinstancestate.listSaver
import androidx.compose.runtime.savedinstancestate.rememberSavedInstanceState
import androidx.compose.runtime.setValue
import com.zachklipp.compose.backstack.BackstackTransition

@Stable
internal class AppModel private constructor(
  namedTransitions: List<Pair<String, BackstackTransition>>,
  prefabBackstacks: List<List<String>>
) {
  private var selectedTransitionIndex by mutableStateOf(0)

  // Can't define this var in the constructor because the backing field isn't set by the time the
  // currentBackstack initializer tries to read it when the @Model annotation is applied.
  @Suppress("CanBePrimaryConstructorProperty")
  var prefabBackstacks by mutableStateOf(prefabBackstacks)
  var currentBackstack by mutableStateOf(prefabBackstacks.first())

  var namedTransitions = namedTransitions
    set(value) {
      field = value
      selectedTransitionIndex = selectedTransitionIndex.coerceAtMost(value.size)
    }
  val selectedTransition get() = namedTransitions[selectedTransitionIndex]

  var slowAnimations: Boolean by mutableStateOf(false)
  var inspectionEnabled: Boolean by mutableStateOf(false)

  val bottomScreen get() = currentBackstack.first()

  fun selectTransition(name: String) {
    selectedTransitionIndex = namedTransitions.indexOfFirst { it.first == name }
  }

  fun pushScreen(screen: String) {
    val newBackstack = (currentBackstack + screen).distinct()
    currentBackstack = newBackstack
  }

  fun popScreen() {
    if (currentBackstack.size > 1) {
      currentBackstack = currentBackstack.dropLast(1)
    }
  }

  companion object {
    /**
     * Creates an instance of [AppModel] and saves it using [rememberSavedInstanceState].
     */
    @Composable
    fun create(
      namedTransitions: List<Pair<String, BackstackTransition>>,
      prefabBackstacks: List<List<String>>
    ): AppModel {
      return rememberSavedInstanceState(saver = saver(namedTransitions, prefabBackstacks)) {
        AppModel(namedTransitions, prefabBackstacks)
      }
    }

    private fun saver(
      namedTransitions: List<Pair<String, BackstackTransition>>,
      prefabBackstacks: List<List<String>>
    ) = listSaver(
        save = { model: AppModel ->
          listOf(
              model.selectedTransitionIndex,
              model.currentBackstack,
              model.slowAnimations,
              model.inspectionEnabled
          )
        },
        restore = { values ->
          AppModel(namedTransitions, prefabBackstacks).also { model ->
            val (restoredSelectedTransitionIndex,
              restoredCurrentBackstack,
              restoredSlowAnimations,
              restoredInspectionEnabled
            ) = values
            (restoredSelectedTransitionIndex as? Int)?.let {
              model.selectedTransitionIndex = it
            }
            @Suppress("UNCHECKED_CAST")
            (restoredCurrentBackstack as? List<String>)?.let {
              model.currentBackstack = it
            }
            (restoredSlowAnimations as? Boolean)?.let {
              model.slowAnimations = it
            }
            (restoredInspectionEnabled as? Boolean)?.let {
              model.inspectionEnabled = it
            }
          }
        }
    )
  }
}
