package com.udacity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.udacity.databinding.ActivityDetailBinding
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val downloadId = intent.getLongExtra(DOWNLOAD_ID_EXTRA, -1)
        val downloadFileName = intent.getStringExtra(DOWNLOAD_FILE_NAME_EXTRA)

        if (downloadId.toInt() != -1){
            binding.contentDetail.tvStatus.setTextColor(Color.GREEN)
        } else {
            binding.contentDetail.tvStatus.setTextColor(Color.RED)
        }

        binding.contentDetail.tvFileName.text = downloadFileName
        binding.contentDetail.tvStatus.text = if (downloadId.toInt() != -1) {
            "Success"
        } else "Fail"

        binding.contentDetail.motionLayout.updateState()

        binding.contentDetail.btnGoBack.setOnClickListener {
            goBack()
        }
    }

    private fun goBack() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}
