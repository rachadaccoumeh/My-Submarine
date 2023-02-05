package com.rachad.mysubmarine.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.rachad.mysubmarine.R
import com.rachad.mysubmarine.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n", "SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val mainViewModel: MainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        binding.webView.settings.javaScriptEnabled = true


        binding.rightMotorJoystick.setOnMoveListener { angle, strength ->
            if (angle == 90)
                mainViewModel.updateRightMotorsSpeed(strength)
            else
                mainViewModel.updateRightMotorsSpeed(-strength)
        }
        binding.leftMotorJoystick.setOnMoveListener { angle, strength ->
            if (angle == 90)
                mainViewModel.updateLeftMotorsSpeed(strength)
            else
                mainViewModel.updateLeftMotorsSpeed(-strength)
        }
        binding.allMotorJoystick.setOnMoveListener { angle, strength ->
            if (angle == 90) {
                mainViewModel.updateRightMotorsSpeed(strength)
                mainViewModel.updateLeftMotorsSpeed(strength)
            } else {
                mainViewModel.updateRightMotorsSpeed(-strength)
                mainViewModel.updateLeftMotorsSpeed(-strength)
            }
        }
        binding.webView.loadUrl("file:///android_asset/index.html")
        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                Toast.makeText(this@MainActivity, url, Toast.LENGTH_LONG).show()
                mainViewModel.gyroscope.observe(this@MainActivity) {
                    binding.textViewX.text = "X:" + it.x.toString()
                    binding.textViewY.text = "Y:" + it.z.toString()
                    binding.webView.loadUrl("javascript:setRotation(${Math.toRadians(it.x.toDouble())},0,${Math.toRadians(it.z.toDouble())})")

                }
            }
        }

        binding.seekBarAllBallast.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2)
                    mainViewModel.updateBallastIndex(p1, null, null, null, null);
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })
        binding.seekBar1.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2)
                    mainViewModel.updateBallastIndex(null, p1, null, null, null);
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })
        binding.seekBar2.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2)
                    mainViewModel.updateBallastIndex(null, null, p1, null, null);
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })
        binding.seekBar3.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2)
                    mainViewModel.updateBallastIndex(null, null, null, p1, null);
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })
        binding.seekBar4.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2)
                    mainViewModel.updateBallastIndex(null, null, null, null, p1);
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })
        mainViewModel.ballast.observe(this) {
            binding.seekBar1.progress = it.index1
            binding.seekBar2.progress = it.index2
            binding.seekBar3.progress = it.index3
            binding.seekBar4.progress = it.index4

        }
        mainViewModel.ballastIndex.observe(this) {
            binding.textViewBallast1.text = "ballast RT " + it.index1
            binding.textViewBallast2.text = "ballast RB " + it.index2
            binding.textViewBallast3.text = "ballast LB " + it.index3
            binding.textViewBallast4.text = "ballast LT " + it.index4
        }
        mainViewModel.onOffStatus.observe(this) {
            if (it)
                binding.OnOffImageView.setImageResource(R.drawable.baseline_power_24)
            else
                binding.OnOffImageView.setImageResource(R.drawable.baseline_power_off_24)
        }
        mainViewModel.batteryLevel.observe(this) {
            binding.batteryProgress.progress = it
        }
        binding.cameraImageView.setOnClickListener {
            //intent to camera app
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setPackage("generalplus.com.GPCamDemo")
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Camera app not found", Toast.LENGTH_LONG).show()
            }
        }

        binding.OnOffImageView.setOnClickListener {
            mainViewModel.updateOnOff()
        }

    }
}