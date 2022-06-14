package com.example.music

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.music.databinding.ActivityThongTinBinding

class ThongTinActivity : AppCompatActivity() {

    lateinit var binding: ActivityThongTinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currrenThemeNav[MainActivity.themeIndex])
        binding = ActivityThongTinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Thông tin"
        binding.aboutText.text = aboutText()
    }
    private fun aboutText():String{
        return "Phat trien boi: Le Thi Hồng Hạnh & Le Thị Thùy Linh\n\n" +
                "Ứng dụng còn nhiều sai sót nếu trong quá trình các bạn trải nghiệm có gặp " +
                "trải nghiệm không tốt nào xin hãy phản hồi lại với chúng tôi"

    }
}