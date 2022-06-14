package com.example.music

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.music.databinding.ActivityChiTietDanhSachPhatBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder

class ChiTietDanhSachPhat : AppCompatActivity() {

    lateinit var binding: ActivityChiTietDanhSachPhatBinding
    lateinit var adapter: MusicAdapter

    companion object{
        var currentPlaylistPos: Int = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currenTheme[MainActivity.themeIndex])
        binding = ActivityChiTietDanhSachPhatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        currentPlaylistPos = intent.extras?.get("index") as Int
        DanhSachNhacActivity.DanhSachBH.ref[currentPlaylistPos].playlist =
            checkDataDSN(DSN = DanhSachNhacActivity.DanhSachBH.ref[currentPlaylistPos].playlist)
        binding.chiTietDanhSach.setItemViewCacheSize(10)
        binding.chiTietDanhSach.setHasFixedSize(true)
        binding.chiTietDanhSach.layoutManager = LinearLayoutManager(this)
        adapter = MusicAdapter(this, DanhSachNhacActivity.DanhSachBH.ref[currentPlaylistPos].playlist, chiTietDSP = true)
        binding.chiTietDanhSach.adapter = adapter
        binding.btnCancel.setOnClickListener { finish() }
        binding.btnXaoTron.setOnClickListener {
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("index",0)
            intent.putExtra("class","XaoTronDSCT")
            startActivity(intent)
        }
        binding.btnAdd.setOnClickListener {
            startActivity(Intent(this, SelectionActivity::class.java))
        }
        binding.xoaBh.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Xoa")
                .setMessage("Ban muon thoat xoa tat ca cac bai hat co trong danh sach?")
                .setPositiveButton("Yes"){dialog, _ ->
                    DanhSachNhacActivity.DanhSachBH.ref[currentPlaylistPos].playlist.clear()
                    adapter.LamMoiDanhSach()
                    dialog.dismiss()
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

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged", "CommitPrefEdits")
    override fun onResume() {
        super.onResume()
        binding.namePlaylist.text = DanhSachNhacActivity.DanhSachBH.ref[currentPlaylistPos].name
        binding.diaChiDS.text = "Co ${adapter.itemCount} bai hat\n\n" +
                "Ngay tao: \n${DanhSachNhacActivity.DanhSachBH.ref[currentPlaylistPos].createOn}\n\n" +
                "-- ${DanhSachNhacActivity.DanhSachBH.ref[currentPlaylistPos].createdBy}"
        if (adapter.itemCount > 0){
            Glide.with(this)
                .load(DanhSachNhacActivity.DanhSachBH.ref[currentPlaylistPos].playlist[0].imgBH)
                .apply(RequestOptions().placeholder(R.drawable.nhac).centerCrop())
                .into(binding.anhPlaylist)
            binding.btnXaoTron.visibility = View.VISIBLE
        }
        adapter.notifyDataSetChanged()
        // để lưu trữ bài hát yêu thích bằng cách sử dụng các tùy chọn được chia sẻ
        val editor = getSharedPreferences("LOVE", MODE_PRIVATE).edit()
        val jsonStringDS = GsonBuilder().create().toJson(DanhSachNhacActivity.DanhSachBH)
        editor.putString("DanhSachBH", jsonStringDS)
        editor.apply()
    }
}

