package com.example.music

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.music.databinding.ActivityMainBinding
import java.io.File
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var musicAdapter: MusicAdapter
    private lateinit var MusicListMA : ArrayList<Music>

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestRunTime()
        setTheme(R.style.Theme_Music)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        KhoiTaoBoCuc()
        binding.btnXaoTron.setOnClickListener {
            val intent = Intent(this, PlayerActivity::class.java)
            startActivity(intent)
        }
        binding.btnYeuThich.setOnClickListener {
            val int = Intent(this, YeuThichActivity::class.java)
            startActivity(int)
        }
        binding.btnDanhSachPhat.setOnClickListener {
            val ten = Intent(this, DanhSachNhacActivity::class.java)
            startActivity(ten)
        }
        binding.menuView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.PhanHoi -> Toast.makeText(baseContext, "Phản hồi", Toast.LENGTH_SHORT).show()
                R.id.Setting -> Toast.makeText(baseContext, "Cài đặt", Toast.LENGTH_SHORT).show()
                R.id.About -> Toast.makeText(baseContext, "Thông tin", Toast.LENGTH_SHORT).show()
                R.id.Exit -> exitProcess(1)
            }
            true
        }
    }

    //Code yêu cầu quyền thời gian chạy
    private fun requestRunTime() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 13) }
    }

    //yêu cầu cho phép truy cập vào thư viện máy điện thoại
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 13) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Đã được cho phép", Toast.LENGTH_SHORT).show()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    13
                )
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun KhoiTaoBoCuc() {
        //menu
        toggle = ActionBarDrawerToggle(this, binding.root, R.string.open, R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        val musicList = ArrayList<String>()
//        musicList.add("háu ăn")
//        musicList.add("háu ăn")
//        musicList.add("háu ăn")
//        musicList.add("háu ăn")
        MusicListMA = getAllAudio()
        binding.musicRV.setHasFixedSize(true)
        binding.musicRV.setItemViewCacheSize(13)
        binding.musicRV.layoutManager = LinearLayoutManager(this@MainActivity)
        musicAdapter = MusicAdapter(this@MainActivity, MusicListMA)
        binding.musicRV.adapter = musicAdapter
        binding.totalBH.text = "Tìm được: " + musicAdapter.itemCount + " bài hát"
    }

    @SuppressLint("Range")
    private fun getAllAudio(): ArrayList<Music> {
        val tempList = ArrayList<Music>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"//kiểm tra tệp rỗng hay không
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA)
        //lấy dữ liệu từ bộ nhớ và sắp xếp theo thứ tự giảm dần
        val cursor = this.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection,
            selection, null, MediaStore.Audio.Media.DATE_ADDED + "DESC", null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val tenbh =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val idBH = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    val albumBH =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    val tacgiaBH =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val linkBH =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val timeBH = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val music = Music(
                        id = idBH, tenBH = tenbh, album = albumBH, TacGia = tacgiaBH,
                        ThoiLuongBH = timeBH, link = linkBH)
                    val file = File(music.link)
                    if (file.exists())
                        tempList.add(music)
                } while (cursor.moveToNext())
                cursor.close()
            }
        }
        return tempList
    }
}

