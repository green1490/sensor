package com.example.sensor.ui.enviroment

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.sensor.databinding.FragmentEnviromentBinding
import com.example.sensor.ui.motion.MotionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EnviromentFragment : Fragment() {

    private var _binding: FragmentEnviromentBinding? = null
    private lateinit var sensorManager:SensorManager
    private lateinit var enviromentViewModel:EnviromentViewModel
    private lateinit var progressBar:ProgressBar
    private var sensorsMaxlux = 0f
    private var lightSensorListener = object:SensorEventListener {
        override fun onSensorChanged(p0: SensorEvent?) {
            if(p0?.sensor?.type == Sensor.TYPE_LIGHT) {
                val value = p0.values[0]
                enviromentViewModel.setText(value.toInt().toString())
                progressBar.progress = ((value/sensorsMaxlux) * 100).toInt()
            }
        }

        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

        }


    }
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        enviromentViewModel =
            ViewModelProvider(this)[EnviromentViewModel::class.java]

        _binding = FragmentEnviromentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textNotifications
        progressBar = binding.progressBar

        enviromentViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sensorManager = view.context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        if (sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) == null) {
            Toast.makeText(context,"You don't have a light sensor", Toast.LENGTH_SHORT).show()
        } else {
            lifecycleScope.launch(Dispatchers.Default) {
                sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)?.also {
                    sensorManager.registerListener(
                        lightSensorListener,
                        it,
                        SensorManager.SENSOR_DELAY_UI,
                    )
                }
                sensorsMaxlux =  sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT).maximumRange
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(lightSensorListener)
    }

    override fun onResume() {
        super.onResume()
            sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)?.also {
                sensorManager.registerListener(
                    lightSensorListener,
                    it,
                    SensorManager.SENSOR_DELAY_UI)
        }
    }
}