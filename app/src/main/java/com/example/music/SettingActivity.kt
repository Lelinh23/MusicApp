package com.example.music

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.music.databinding.ActivitySettingBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingActivity : AppCompatActivity() {

    lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currrenThemeNav[MainActivity.themeIndex])
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Cài đặt"
        when(MainActivity.themeIndex){
            0 -> binding.themTimHong.setBackgroundColor(Color.CYAN)
            1 -> binding.themXanh.setBackgroundColor(Color.CYAN)
            2 -> binding.themDen.setBackgroundColor(Color.CYAN)
            3 -> binding.themDo.setBackgroundColor(Color.CYAN)
            4 -> binding.themVang.setBackgroundColor(Color.CYAN)
        }
        binding.themTimHong.setOnClickListener { saveTheme(0) }
        binding.themXanh.setOnClickListener { saveTheme(1) }
        binding.themDen.setOnClickListener { saveTheme(2) }
        binding.themDo.setOnClickListener { saveTheme(3) }
        binding.themVang.setOnClickListener { saveTheme(4) }
        binding.versionName.text = setVersion()
    }

    private fun saveTheme(index: Int){
        if (MainActivity.themeIndex != index){
            val editor = getSharedPreferences("Màn hình", MODE_PRIVATE).edit()
            editor.putInt("themeIndex", index)
            editor.apply()
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Đổi màu màn hình")
                .setMessage("Bạn muốn đổi màu màn hình?")
                .setPositiveButton("Yes"){_, _ ->
                    exitApplication()
                }
                .setNegativeButton("No"){dialog, _ ->
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
            customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
        }
    }
    private fun setVersion():String{
        return "Version app: ${BuildConfig.VERSION_NAME}"
    }
}