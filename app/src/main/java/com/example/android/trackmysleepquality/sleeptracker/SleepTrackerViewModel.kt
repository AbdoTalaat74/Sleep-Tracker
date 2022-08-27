/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import androidx.lifecycle.*
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel for SleepTrackerFragment.
 */

class SleepTrackerViewModel(
    private val database: SleepDatabaseDao,
    application: Application) : AndroidViewModel(application) {

//    private val _navigateToSleepQuality = MutableLiveData<SleepNight>()
//    val navigateToSleepQuality: LiveData<SleepNight>
//    get() = _navigateToSleepQuality
//
//    fun doneNavigating() {
//        _navigateToSleepQuality.value = null
//    }

    //TODO (03) Create a MutableLiveData variable tonight for one SleepNight.

    private var toNight = MutableLiveData<SleepNight?>()

    //TODO (04) Define a variable, nights. Then getAllNights() from the database
    //and assign to the nights variable.
    private var nights = database.getAllNights()

    //TODO (12) Transform nights into a nightsString using formatNights().
    val nightsString = Transformations.map(nights) { nights ->
        formatNights(nights, application.resources)
    }

    //TODO (05) In an init block, initializeTonight(), and implement it to launch a coroutine
    //to getTonightFromDatabase().
    init {
        initializeTonight()
    }

    private fun initializeTonight() {
        viewModelScope.launch {
            toNight.value = getTonightFromDatabase()
        }
    }

    //TODO (06) Implement getTonightFromDatabase()as a suspend function.
    private suspend fun getTonightFromDatabase(): SleepNight? {

        var night = database.getTonight()
        if (night?.endTimeMilli != night?.startTimeMilli) {
            night = null
        }
        return night
    }

    //TODO (07) Implement the click handler for the Start button, onStartTracking(), using
    //coroutines. Define the suspend function insert(), to insert a new night into the database.
    fun onStartTracking() {
        viewModelScope.launch {
            val newNight = SleepNight()
            insert(newNight)
            toNight.value = getTonightFromDatabase()
        }
    }

    private suspend fun insert(sleepNight: SleepNight) {
            database.insert(sleepNight)
    }

    //TODO (08) Create onStopTracking() for the Stop button with an update() suspend function.

    fun onStopTracking() {
        viewModelScope.launch {
            val oldNight = toNight.value ?: return@launch
            oldNight.endTimeMilli = System.currentTimeMillis()
            update(oldNight)
//            _navigateToSleepQuality.value = oldNight

        }

    }

    private suspend fun update(oldNight: SleepNight) {
            database.update(oldNight)
    }

    //TODO (09) For the Clear button, created onClear() with a clear() suspend function.

    fun onClear() {
        viewModelScope.launch {
            clear()
            toNight.value = null
        }
    }

    private suspend fun clear() {
            database.clear()
    }
}

