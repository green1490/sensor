package com.example.sensor.ui.enviroment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EnviromentViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is enviroment Fragment"
    }
    val text: LiveData<String> = _text
}