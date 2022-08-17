package com.rachad.mysubmarine.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.rachad.mysubmarine.R
import com.rachad.mysubmarine.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val mainViewModel: MainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        binding.rightMotorJoystick.setOnMoveListener { angle, strength ->
            if (angle in 0..180)
                mainViewModel.updateMotorsSpeed(strength, 0)
            else
                mainViewModel.updateMotorsSpeed(-strength, 0)
        }
        binding.leftMotorJoystick.setOnMoveListener { angle, strength ->
            if (angle in 0..180)
                mainViewModel.updateMotorsSpeed(0, strength)
            else
                mainViewModel.updateMotorsSpeed(0, -strength)
        }
        binding.leftMotorJoystick.setOnMoveListener { angle, strength ->
            if (angle in 0..180)
                mainViewModel.updateMotorsSpeed(strength, strength)
            else
                mainViewModel.updateMotorsSpeed(-strength, -strength)
        }
        mainViewModel.gyroscope.observe()


    }
}