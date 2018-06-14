package com.orcchg.ecologymap

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco

class EcologyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this)
    }
}
