package com.tech.vircle.base.location

import android.location.Location

interface LocationResultListener {
    fun getLocation(location: Location)
}