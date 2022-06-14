package com.example.music

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.music.databinding.ActivityYeuThichBinding

class YeuThichActivity : AppCompatActivity() {

    private lateinit var binding: ActivityYeuThichBinding
    private lateinit var adapter: YeuThichAdapter

    companion object{
        var bh_yeu_thich: ArrayList<Music> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currenTheme[MainActivity.themeIndex])
        binding = ActivityYeuThichBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bh_yeu_thich = checkDataDSN(bh_yeu_thich)
        binding.btnBack.setOnClickListener { finish() }
        binding.thichRV.setHasFixedSize(true)
        binding.thichRV.setItemViewCacheSize(13)
        binding.thichRV.layoutManager = LinearLayoutManager(this@YeuThichActivity)
        adapter = YeuThichAdapter(this@YeuThichActivity, bh_yeu_thich)
        binding.thichRV.adapter = adapter
        if (bh_yeu_thich.size < 1){
            binding.btnXaoTron.visibility = View.INVISIBLE
        }
        binding.btnXaoTron.setOnClickListener {
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("index",0)
            intent.putExtra("class", "XaoTronLove")
            startActivity(intent)
        }
    }
}