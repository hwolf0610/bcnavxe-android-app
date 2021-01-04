package com.crittermap.backcountrynavigator.xe.di

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import javax.inject.Inject

internal interface ProvidesProgress {
    fun monitorProgressActivity(): LiveData<Boolean>
    fun progressStart()
    fun progressFinished()
}

class ProvidesProgressImpl @Inject constructor(
) : ProvidesProgress {
    private val progressStatus = MutableLiveData<Boolean>()

    init {
        progressStatus.value = false
    }

    override fun progressStart() {
        progressStatus.value?.let { inProgress ->
            if (!inProgress) progressStatus.postValue(true)
        } ?: progressStatus.postValue(true)
    }

    override fun progressFinished() {
        progressStatus.value?.let { inProgress ->
            if (inProgress) progressStatus.postValue(false)
        } ?: progressStatus.postValue(false)
    }

    override fun monitorProgressActivity(): LiveData<Boolean> {
        return progressStatus
    }
}