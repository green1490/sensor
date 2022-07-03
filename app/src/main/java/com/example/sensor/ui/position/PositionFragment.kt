package com.example.sensor.ui.position

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
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sensor.databinding.FragmentPositionBinding

class PositionFragment : Fragment(){

    private var _binding: FragmentPositionBinding? = null
    private lateinit var sensorManager:SensorManager
    private var proximitySensor = object:SensorEventListener{
        override fun onSensorChanged(p0: SensorEvent?) {
            if (p0?.sensor?.type == Sensor.TYPE_PROXIMITY) {
                val value = p0.values[0]
                positionViewModel.setText(value.toString())
            }
        }

        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

        }

    }
    private  lateinit var textView:TextView
    private lateinit var positionViewModel:PositionViewModel
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        positionViewModel =
            ViewModelProvider(this)[PositionViewModel::class.java]

        _binding = FragmentPositionBinding.inflate(inflater, container, false)
        val root: View = binding.root

        textView = binding.textView
        positionViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sensorManager = view.context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        if (sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) == null) {
            Toast.makeText(context,"You don't have a proximity sensor", Toast.LENGTH_SHORT).show()
        } else {
                sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)?.also {
                    sensorManager.registerListener(
                        proximitySensor,
                        it,
                        SensorManager.SENSOR_DELAY_UI,
                    )
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(proximitySensor)
    }

    override fun onResume() {
        super.onResume()
            sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)?.also {
                sensorManager.registerListener(
                    proximitySensor,
                    it,
                    SensorManager.SENSOR_DELAY_UI
                )
        }
    }
}