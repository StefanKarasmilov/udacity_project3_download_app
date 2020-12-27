package com.udacity

import android.R.attr.action
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_SCREEN_OFF
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.udacity.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var downloadID: Long = 0
    private var URL: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(toolbar)

        var radioButton: RadioButton? = null

        binding.contentMain.radioGroup.setOnCheckedChangeListener { radioGroup, i ->
            radioButton = findViewById(radioGroup.checkedRadioButtonId)
        }

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        binding.contentMain.customButton.setOnClickListener {
            if (checkRadioButtonSelected(radioButton)) {
                download()
                custom_button.setState(ButtonState.Clicked)
            }
        }

        createChannel()
    }

    private fun checkRadioButtonSelected(radioButton: RadioButton?): Boolean {
        if (radioButton != null) {
            URL = when (radioButton.id) {
                binding.contentMain.glideRadioButton.id -> {
                    "https://github.com/bumptech/glide/archive/master.zip"
                }
                binding.contentMain.loadAppRadioButton.id -> {
                    "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
                }
                binding.contentMain.retrofitRadioButton.id -> {
                    "https://github.com/square/retrofit/archive/master.zip"
                }
                else -> null
            }
            return true
        } else {
            Toast.makeText(this, "Please select the file to download", Toast.LENGTH_SHORT).show()
            return false
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            if (id == downloadID) {
                binding.contentMain.customButton.setState(ButtonState.Completed)

                val notificationManager =
                    ContextCompat.getSystemService(
                        this@MainActivity,
                        NotificationManager::class.java
                    ) as NotificationManager

                notificationManager.sendNotification(
                    "Download Finish!",
                    this@MainActivity,
                    id,
                    getDownloadTitle()
                )
            }
        }
    }

    private fun getDownloadTitle(): String {
        return when (URL) {
            "https://github.com/bumptech/glide/archive/master.zip" -> binding.contentMain.glideRadioButton.text.toString()
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip" -> binding.contentMain.loadAppRadioButton.text.toString()
            "https://github.com/square/retrofit/archive/master.zip" -> binding.contentMain.retrofitRadioButton.text.toString()
            else -> ""
        }
    }

    private fun createChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Download Finished"

            val notificationManager =
                getSystemService(NotificationManager::class.java) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }

    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    companion object {
        const val CHANNEL_ID = "channelId"
        const val CHANNEL_NAME = "Download Channel"
    }

}

