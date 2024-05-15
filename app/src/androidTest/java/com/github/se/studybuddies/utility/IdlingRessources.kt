package com.github.se.studybuddies.utility

import androidx.test.espresso.IdlingResource
import java.util.concurrent.atomic.AtomicBoolean

class ComposeIdlingResource : IdlingResource {
  @Volatile private var callback: IdlingResource.ResourceCallback? = null

  // AtomicBoolean to track if the resource is idle or busy
  private val isIdleNow = AtomicBoolean(true)

  // Setter method for idleness
  fun setIdleState(isIdleNow: Boolean) {
    this.isIdleNow.set(isIdleNow)
    if (isIdleNow && callback != null) {
      callback?.onTransitionToIdle()
    }
  }

  override fun getName(): String {
    return this.javaClass.name
  }

  override fun isIdleNow(): Boolean {
    return isIdleNow.get()
  }

  override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
    this.callback = callback
  }
}
