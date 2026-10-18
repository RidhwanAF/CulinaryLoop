package com.raf.culinaryloop

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Base Application class initializing Hilt dependency injection and app runtime infrastructure.
 */
@HiltAndroidApp
class CulinaryLoopApp : Application()

