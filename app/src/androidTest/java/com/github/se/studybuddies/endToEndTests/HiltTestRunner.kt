package com.github.se.studybuddies.endToEndTests

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import com.github.se.studybuddies.mapService.LocationApp
import dagger.hilt.android.testing.HiltTestApplication

class HiltTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}