package com.example.sensor.ui.motion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.lang.reflect.Type

class MotionViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Gyro fragment"

    }
    private val _sensorSize = MutableLiveData<Int>().apply {
        value = 0
    }

    private val _sensorValue = MutableLiveData<Any>().apply {
        value = 0
    }

    fun setSensorSize(size:Int) {
        _sensorSize.value = size
    }

    fun setSensorValue(value:Any) {
        _sensorValue.value = value
    }

    val text: LiveData<String> = _text
    val sensorSize:LiveData<Int> = _sensorSize
    val sensorValue:LiveData<Any> = _sensorValue
}