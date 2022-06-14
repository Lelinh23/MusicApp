package com.example.music

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.music.databinding.ActivityPhanHoiBinding

class PhanHoiActivity : AppCompatActivity() {

    lateinit var binding: ActivityPhanHoiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currrenThemeNav[MainActivity.themeIndex])
        binding = ActivityPhanHoiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Phản hồi"
        binding.sendMSG.setOnClickListener {
            // su dung action_send để mo ứng dụng email được cài đặt trên thiết bị Android
            val userNhan = binding.emailFA.text.toString()
            val ChuDe = binding.topicFA.text.toString()
            val message = binding.phanHoi.text.toString()

            sendMSG(userNhan, ChuDe, message)
        }
    }

    @SuppressLint("IntentReset")
    private fun sendMSG(userNhan: String, chuDe: String, message: String) {
        val mIntent = Intent(Intent.ACTION_SEND)
        //để gửi email cần chỉ định mailto: vì URI sử dụng phương thức setData ()
        // và setData() là "text/plain" sử dụng phương thức setType ()
        mIntent.data = Uri.parse("mailto:")
        mIntent.type = "text/plain"
        mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(userNhan))
        mIntent.putExtra(Intent.EXTRA_SUBJECT, chuDe)
        mIntent.putExtra(Intent.EXTRA_TEXT, message)

        try {
            startActivity(Intent.createChooser(mIntent,"Chon ung dung de giui..."))
            finish()
        }
        catch (e: Exception){
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }
}
