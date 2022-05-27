package com.example.music

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class DanhSachNhacActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Music)
        setContentView(R.layout.activity_danh_sach_nhac)
    }
}