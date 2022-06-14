package com.example.music

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.music.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File

@Suppress("UNUSED_VARIABLE")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var musicAdapter: MusicAdapter
    companion object{
        lateinit var MusicListMA : ArrayList<Music>
        lateinit var musicListSearch: ArrayList<Music>
        var timkiem: Boolean = false
        var themeIndex: Int = 0
        var currenTheme = arrayOf(R.style.Theme_TimHong, R.style.Theme_Xanh, R.style.Theme_Den,
            R.style.Theme_Do, R.style.Theme_Vang)
        var currrenThemeNav = arrayOf(R.style.Theme_TimHong_Nav, R.style.Theme_Xanh_Nav,
            R.style.Theme_Den_Nav, R.style.Theme_Do_Nav, R.style.Theme_Vang_Nav)
        var currrenGradient = arrayOf(R.drawable.gradient_tim_hong, R.drawable.gradient_blue,
            R.drawable.gradient_den, R.drawable.gradient_do, R.drawable.gradient_vang)
        var sortOrder: Int = 0
        val sort
    }

    @SuppressLint("CommitPrefEdits")
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val themeEditor = getSharedPreferences("Màn hình", MODE_PRIVATE)
        themeIndex = themeEditor.getInt("themeIndex",0)
        setTheme(currrenThemeNav[themeIndex])
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //menu
        toggle = ActionBarDrawerToggle(this, binding.root, R.string.open, R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //
        if (requestRunTime()){
            KhoiTaoBoCuc()
            // để truy xuất bài hát yêu thích bằng cách sử dụng các tùy chọn được chia sẻ
            YeuThichActivity.bh_yeu_thich = ArrayList()
            val editor = getSharedPreferences("YeuThich", MODE_PRIVATE)
            val jsonString = editor.getString("BhYeuThich", null)
            val typeToken = object : TypeToken<ArrayList<Music>>(){}.type
            if (jsonString != null){
                val data: ArrayList<Music> = GsonBuilder().create().fromJson(jsonString, typeToken)
                YeuThichActivity.bh_yeu_thich.addAll(data)
            }
            DanhSachNhacActivity.DanhSachBH = DanhSachBH()
            val jsonStringDS = editor.getString("DanhSachBH", null)
            if (jsonStringDS != null){
                val dataDS: DanhSachBH = GsonBuilder().create().fromJson(jsonStringDS, DanhSachBH::class.java)
                DanhSachNhacActivity.DanhSachBH = dataDS
            }
        }
        binding.btnXaoTron.setOnClickListener {
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("index",0)
            intent.putExtra("class", "MainActivity")
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
                R.id.PhanHoi -> startActivity(Intent(this@MainActivity, PhanHoiActivity::class.java))
                R.id.Setting -> startActivity(Intent(this@MainActivity, SettingActivity::class.java))
                R.id.About -> startActivity(Intent(this@MainActivity, ThongTinActivity::class.java))
                R.id.Exit -> {
                    val builder = MaterialAlertDialogBuilder(this)
                    builder.setTitle("Exit")
                        .setMessage("Bạn muốn thoát ứng dụng?")
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
            true
        }
    }

    //Code yêu cầu quyền thời gian chạy
    private fun requestRunTime(): Boolean{
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 13)
            return false
        }
        return true
    }

    //yêu cầu cho phép truy cập vào thư viện máy điện thoại
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 13) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Đã được cho phép", Toast.LENGTH_SHORT).show()
                KhoiTaoBoCuc()
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

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.R)
    private fun KhoiTaoBoCuc(): DrawerLayout {
        timkiem = false
        MusicListMA = getAllAudio()
        binding.musicRV.setHasFixedSize(true)
        binding.musicRV.setItemViewCacheSize(13)
        binding.musicRV.layoutManager = LinearLayoutManager(this@MainActivity)
        musicAdapter = MusicAdapter(this@MainActivity, MusicListMA)
        binding.musicRV.adapter = musicAdapter
        binding.totalBH.text = "Tìm được: " + musicAdapter.itemCount + " bài hát"
        return binding.root
    }

    @SuppressLint("Range")
    private fun getAllAudio() : ArrayList<Music>{
        val tempList = ArrayList<Music>()
        val selection = MediaStore.Audio.Media.IS_MUSIC+ "!=0" // to check the file is not null
        val projection = arrayOf(MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DATE_ADDED, MediaStore.Audio.Media.ALBUM_ID)
        val cursor = this.contentResolver?.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,selection,null,
            MediaStore.Audio.Media.DATE_ADDED + " DESC",null)//to get data from the storage and arrange in descending order
        if (cursor != null){
            if(cursor.moveToFirst())
                do {
                    val titlec = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val albumc = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    val idc = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    val artistc = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val linkc = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val durationc = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val anhIdc = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString()
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val imgUrc = Uri.withAppendedPath(uri,anhIdc).toString()
                    val music = Music(tenBH = titlec, album = albumc, TacGia = artistc, id = idc,
                        ThoiLuongBH = durationc, link = linkc, imgBH = imgUrc)
                    val file = File(music.link)
                    if(file.exists())
                        tempList.add(music)
                }while (cursor.moveToNext())
                cursor.close()
        }
        return tempList
    }

    @SuppressLint("CommitPrefEdits")
    override fun onDestroy() {
        super.onDestroy()
        if (!PlayerActivity.isPlaying && PlayerActivity.musicService != null){
            exitApplication()
        }
    }

    override fun onResume() {
        super.onResume()
        // để lưu trữ bài hát yêu thích bằng cách sử dụng các tùy chọn được chia sẻ
        val editor = getSharedPreferences("YeuThich", MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(YeuThichActivity.bh_yeu_thich)
        editor.putString("BhYeuThich", jsonString)
        val jsonStringDS = GsonBuilder().create().toJson(DanhSachNhacActivity.DanhSachBH)
        editor.putString("DanhSachBH", jsonStringDS)
        editor.apply()
        //for sorting
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.seach_view, menu)
        //for setting gradient
        findViewById<LinearLayout>(R.id.linearLayoutNav)?.setBackgroundResource(currrenGradient[themeIndex])
        val searchView = menu?.findItem(R.id.seachView)?.actionView as androidx.appcompat.widget.SearchView
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String?): Boolean {
                musicListSearch = ArrayList()
                if(newText != null){
                    val userInput = newText.lowercase()
                    for (song in MusicListMA)
                        if(song.tenBH.lowercase().contains(userInput))
                            musicListSearch.add(song)
                    timkiem = true
                    musicAdapter.updateDanhSachNhac(seachList = musicListSearch)
                }
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }
}

