package com.example.music

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.music.databinding.ActivityDanhSachNhacBinding
import com.example.music.databinding.AddDanhSachPhatBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*

class DanhSachNhacActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDanhSachNhacBinding
    private lateinit var adapter: DanhSachPhatAdapter
    companion object{
        var DanhSachBH: DanhSachBH = DanhSachBH()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currenTheme[MainActivity.themeIndex])
        binding = ActivityDanhSachNhacBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnRa.setOnClickListener { finish() }
        binding.danhSachRV.setHasFixedSize(true)
        binding.danhSachRV.setItemViewCacheSize(13)
        binding.danhSachRV.layoutManager = GridLayoutManager(this@DanhSachNhacActivity, 2)
        adapter = DanhSachPhatAdapter(this@DanhSachNhacActivity, DanhSachPhat = DanhSachBH.ref)
        binding.danhSachRV.adapter = adapter
        binding.addDanhSach.setOnClickListener{ customAlert() }
    }
    private fun customAlert(){
        val customDialog = LayoutInflater.from(this@DanhSachNhacActivity).inflate(R.layout.add_danh_sach_phat, binding.root, false)
        val binder = AddDanhSachPhatBinding.bind(customDialog)

        val builder = MaterialAlertDialogBuilder(this)
        builder.setView(customDialog)
            .setTitle("Danh sách nhạc chi tiết")
            .setPositiveButton("Thêm"){ dialog, _ ->
                val tenDS = binder.btnTenDS.text
                val createdBy = binder.btnTenNT.text
                if (tenDS != null && createdBy != null){
                    if (tenDS.isNotEmpty() && createdBy.isNotEmpty()){
                        addDanhSachPhat(tenDS.toString(), createdBy.toString())
                    }
                }
                dialog.dismiss()
            }.show()
    }

    private fun addDanhSachPhat(name: String, createdBy: String) {

        var playListExit = false
        for (i in DanhSachBH.ref){
            if (name.equals(i.name)){

                playListExit = true
                break
            }
        }
        if (playListExit){
             Toast.makeText(this, " Thoát Danh Sách!!", Toast.LENGTH_SHORT).show()
        } else{
             val  tempList = DanhSachPhat()
             tempList.name = name
             tempList.playlist = ArrayList()
             tempList.createdBy = createdBy
            val calender = Calendar.getInstance().time
            val sdf = SimpleDateFormat("dd MMM", Locale.ENGLISH)
            tempList.createOn = sdf.format(calender)
            DanhSachBH.ref.add(tempList)
            adapter.LamMoiPlayList()
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }
}