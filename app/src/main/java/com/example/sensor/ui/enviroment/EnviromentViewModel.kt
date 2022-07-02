package com.example.sensor.ui.enviroment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EnviromentViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "0"
    }
    fun setText(userValue:String) {
        _text.value = userValue
    }
    val text: LiveData<String> = _text
}