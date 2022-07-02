package com.example.sensor.ui.motion

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.sensor.databinding.FragmentMotionBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MotionFragment : Fragment(){
    private lateinit var sensorManager:SensorManager
    private var _binding: FragmentMotionBinding? = null
    private lateinit var textView:TextView
    private lateinit var motionViewModel:MotionViewModel
    private var totalStep:Float = 0f
    private var previousStep:Float = 0f
    private var sensorListener = object :SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if(event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
                totalStep = event.values[0]
                val currentStep = totalStep.toInt() - previousStep.toInt()
                motionViewModel.setSensorValue(currentStep)
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

        lifecycleScope.launch(Dispatchers.Default) {
            previousStep = loadStepsData()
        }

        motionViewModel =
            ViewModelProvider(this)[MotionViewModel::class.java]

        motionViewModel.sensorValue.observe(viewLifecycleOwner) {
            textView.text = it.toString()
        }

        _binding = FragmentMotionBinding.inflate(inflater, container, false)
        val root: View = binding.root

        textView = binding.textHome

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sensorManager = view.context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) == null) {
            Toast.makeText(context,"You don't have a step counter",Toast.LENGTH_SHORT).show()
        } else {
            lifecycleScope.launch(Dispatchers.Default) {
                sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)?.also {
                    sensorManager.registerListener(
                        sensorListener,
                        it,
                        SensorManager.SENSOR_DELAY_UI,
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        lifecycleScope.launch(Dispatchers.Default) {
            sensorManager.unregisterListener(sensorListener)
        }
        saveStepsData()
    }

    override fun onDestroy() {
        super.onDestroy()
        saveStepsData()
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch(Dispatchers.Default) {
            sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)?.also {
                sensorManager.registerListener(
                    sensorListener,
                    it,
                    SensorManager.SENSOR_DELAY_UI
                )
            }
        }
    }

    private fun saveStepsData() {
        //to long time to save
        val sharedPreferences = context?.getSharedPreferences("motionPref",Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.putFloat("totalStep",totalStep)
        editor?.apply()
    }

    private fun loadStepsData(): Float {
        val sharedPreferences = context?.getSharedPreferences("motionPref", Context.MODE_PRIVATE)
        return sharedPreferences?.getFloat("totalStep", 0f) ?: return 0f
    }
}