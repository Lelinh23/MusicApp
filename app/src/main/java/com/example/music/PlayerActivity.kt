package com.example.music

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.music.databinding.ActivityPlayerBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

@Suppress("DEPRECATION")
class PlayerActivity : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {

    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityPlayerBinding
        lateinit var musicListPN : ArrayList<Music>
        var vitriBH: Int = 0
        var isPlaying: Boolean = false
        var musicService: MusicService? = null
        var laplai: Boolean = false
        var isFavourite: Boolean = false
        var fIndex: Int = -1
        var min15: Boolean = false
        var min30: Boolean = false
        var min60: Boolean = false
        var nowPlayingId: String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currenTheme[MainActivity.themeIndex])
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        KhoiTaoBoCuc()
        binding.btnBack.setOnClickListener { finish() }
        binding.btnPhatBHPa.setOnClickListener{
            if (isPlaying) DungLaiBH()
            else playBH()
        }
        binding.btnBHTruoc.setOnClickListener{
            chuyenBH(increment = false)
        }
        binding.btnBHNext.setOnClickListener{
            chuyenBH(increment = true)
        }
        binding.seekBar.setOnSeekBarChangeListener(@SuppressLint("AppCompatCustomView")
        object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) musicService!!.phat_nhac!!.seekTo(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })
        binding.btnLapLai.setOnClickListener {
            if (!laplai){
                laplai = true
                binding.btnLapLai.setColorFilter(ContextCompat.getColor(this,
                    android.R.color.holo_red_dark))
            }else{
                laplai = false
                binding.btnLapLai.setColorFilter(ContextCompat.getColor(this,
                    android.R.color.white))
            }
        }
        binding.btnYeuThichPA.setOnClickListener {
            if (isFavourite){
                isFavourite = false
                binding.btnYeuThichPA.setImageResource(R.drawable.icon_chua_thich)
                YeuThichActivity.bh_yeu_thich.removeAt(fIndex)
            }
            else{
                isFavourite = true
                binding.btnYeuThichPA.setImageResource(R.drawable.icon_yeu_thich)
                YeuThichActivity.bh_yeu_thich.add(musicListPN[vitriBH])
            }
        }
        binding.btnCanBang.setOnClickListener {
            try {
                val Can_bang = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                Can_bang.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, musicService!!.phat_nhac!!.audioSessionId)
                Can_bang.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, baseContext.packageName)
                Can_bang.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                startActivityForResult(Can_bang,13)
            }catch (e: Exception){
                Toast.makeText(
                    this, "Chuc nang dieu chinh do can bang khong duoc ho tro!", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnHenGioTat.setOnClickListener {
            val time = min15 || min30 || min60
            if (!time){showBottomSheet()}
            else {
                val builder = MaterialAlertDialogBuilder(this)
                builder.setTitle("Dừng thời gian")
                    .setMessage("Bạn có muốn dừng hẹn giờ không?")
                    .setPositiveButton("Yes"){_, _ ->
                        min15 = false
                        min30 = false
                        min60 = false
                        binding.btnHenGioTat.setColorFilter(ContextCompat.getColor(this, R.color.white))
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
        binding.btnShare.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "Audio/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(musicListPN[vitriBH].link))
            startActivity(Intent.createChooser(shareIntent,"Chia sẻ bài hát dưới dạng tệp!!"))
        }
    }

    private fun KhoiTaoBoCuc() {
        vitriBH = intent.getIntExtra("index", 0)
        when(intent.getStringExtra("class")){
            "NowPlaying" ->{
                setLayout()
                binding.tvSeekBarStart.text = DinhDangTime(musicService!!.phat_nhac!!.currentPosition.toLong())
                binding.tvSeekBarEnd.text = DinhDangTime(musicService!!.phat_nhac!!.duration.toLong())
                binding.seekBar.progress = musicService!!.phat_nhac!!.currentPosition
                binding.seekBar.max = musicService!!.phat_nhac!!.duration
                if(isPlaying) binding.btnPhatBHPa.setIconResource(R.drawable.icon_phat_bai_hat)
                else binding.btnPhatBHPa.setIconResource(R.drawable.icon_dung_bai_hat)
            }
            "XaoTronLove"->{
                // bắt đầu Xáo Trộn
                val intent = Intent(this,MusicService::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPN = ArrayList()
                musicListPN.addAll(YeuThichActivity.bh_yeu_thich)
                musicListPN.shuffle()
                setLayout()
            }
            "chiTietDSPAdapter"->{
                // bắt đầu Xáo Trộn
                val intent = Intent(this,MusicService::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPN = ArrayList()
                musicListPN.addAll(DanhSachNhacActivity.DanhSachBH.ref[ChiTietDanhSachPhat.currentPlaylistPos].playlist)
                setLayout()
            }
            "YeuThichAdapter"->{
                val intent = Intent(this,MusicService::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPN = ArrayList()
                musicListPN.addAll(YeuThichActivity.bh_yeu_thich)
                setLayout()
            }
            "MusicAdapterSearch"->{
                // bắt đầu Se
                val intent = Intent(this,MusicService::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPN = ArrayList()
                musicListPN.addAll(MainActivity.musicListSearch)
                setLayout()
            }
            "MusicAdapter" -> {
                // bắt đầu Se
                val intent = Intent(this,MusicService::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPN = ArrayList()
                musicListPN.addAll(MainActivity.MusicListMA)
                setLayout()
            }
            "MainActivity" -> {
                // bắt đầu Se
                val intent = Intent(this,MusicService::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPN = ArrayList()
                musicListPN.addAll(MainActivity.MusicListMA)
                musicListPN.shuffle()
                setLayout()
            }
            "XaoTronDSCT"->{
                // bắt đầu Xáo Trộn
                val intent = Intent(this,MusicService::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPN = ArrayList()
                musicListPN.addAll(DanhSachNhacActivity.DanhSachBH.ref[ChiTietDanhSachPhat.currentPlaylistPos].playlist)
                musicListPN.shuffle()
                setLayout()
            }
        }
    }

    private fun setLayout(){
        fIndex = CheckerThich(musicListPN[vitriBH].id)
        Glide.with(this)
            .load(musicListPN[vitriBH].imgBH)
            .apply(RequestOptions().placeholder(R.drawable.nhac).centerCrop())
            .into(binding.anhBHPa)
        binding.nameBHPa.text = musicListPN[vitriBH].tenBH
        if (laplai){
            binding.btnLapLai.setColorFilter(ContextCompat.getColor(this,
                android.R.color.white))
        }
        if (min15 || min30 || min60){
            binding.btnLapLai.setColorFilter(ContextCompat.getColor(this,
                android.R.color.holo_red_dark
            ))
        }
        if (isFavourite) binding.btnYeuThichPA.setImageResource(R.drawable.icon_yeu_thich)
        else binding.btnYeuThichPA.setImageResource(R.drawable.icon_chua_thich)
    }
    private fun creatPhatNhac(){
        try {
            if (musicService!!.phat_nhac == null) musicService!!.phat_nhac = MediaPlayer()
            musicService!!.phat_nhac!!.reset()
            musicService!!.phat_nhac!!.setDataSource(musicListPN[vitriBH].link)
            musicService!!.phat_nhac!!.prepare()
            musicService!!.phat_nhac!!.start()
            isPlaying = true
            binding.btnPhatBHPa.setIconResource(R.drawable.icon_phat_bai_hat)
            binding.tvSeekBarStart.text = DinhDangTime(musicService!!.phat_nhac!!.currentPosition.toLong())
            binding.tvSeekBarEnd.text = DinhDangTime(musicService!!.phat_nhac!!.duration.toLong())
            binding.seekBar.progress = 0
            binding.seekBar.max = musicService!!.phat_nhac!!.duration
            musicService!!.phat_nhac!!.setOnCompletionListener(this)//lap lai BH
            nowPlayingId = musicListPN[vitriBH].id
        }catch (e: Exception){
            return
        }
    }
    private fun playBH(){
        binding.btnPhatBHPa.setIconResource(R.drawable.icon_phat_bai_hat)
        isPlaying = true
        musicService!!.phat_nhac!!.start()
    }
    private fun DungLaiBH(){
        binding.btnPhatBHPa.setIconResource(R.drawable.icon_dung_bai_hat)
        isPlaying = false
        musicService!!.phat_nhac!!.pause()
    }
    private fun chuyenBH(increment: Boolean){
        if (increment){
            setViTriBH(increment = true)
            setLayout()
            creatPhatNhac()
        }
        else{
            setViTriBH(increment = false)
            setLayout()
            creatPhatNhac()
        }
    }
    private fun setViTriBH(increment: Boolean){
        if (increment)
        {
            if (musicListPN.size -1 == vitriBH)
                vitriBH = 0
            else ++vitriBH
        }
        else{
            if (0 == vitriBH)
                vitriBH = musicListPN.size -1
            else --vitriBH
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MyBinder
        musicService = binder.currentService()
        creatPhatNhac()
        //musicService!!.showNotification()
        musicService!!.CaiDatseekBar()
        musicService!!.audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        musicService!!.audioManager.requestAudioFocus(musicService, AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN)

    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService = null
    }

    override fun onCompletion(mp: MediaPlayer?) {
        setViTriBH(increment = true)
        creatPhatNhac()
        setLayout()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 13 || resultCode == RESULT_OK)
            return
    }

    private fun showBottomSheet(){
        val dialog = BottomSheetDialog(this@PlayerActivity)
        dialog.setContentView(R.layout.bottom_sheet_dialog)
        dialog.show()
        dialog.findViewById<LinearLayout>(R.id.min_15)?.setOnClickListener{
            Toast.makeText(baseContext,"Sẽ đóng ứng dụng sau 15 phút nữa!", Toast.LENGTH_SHORT).show()
            binding.btnHenGioTat.setColorFilter(ContextCompat.getColor(this,
                android.R.color.holo_red_dark
            ))
            min15 = true
            Thread{
                Thread.sleep((60000 * 15).toLong())
                if (min15) exitApplication()
            }.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_30)?.setOnClickListener{
            Toast.makeText(baseContext,"Sẽ đóng ứng dụng sau 30 phút nữa!", Toast.LENGTH_SHORT).show()
            binding.btnHenGioTat.setColorFilter(ContextCompat.getColor(this,
                android.R.color.holo_red_dark
            ))
            min30 = true
            Thread{
                Thread.sleep((60000 * 30).toLong())// 60000 ms = 1 phút
                if (min30) exitApplication()
            }.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_60)?.setOnClickListener{
            Toast.makeText(baseContext,"Sẽ đóng ứng dụng sau 1 tiếng nữa!", Toast.LENGTH_SHORT).show()
            binding.btnHenGioTat.setColorFilter(ContextCompat.getColor(this,
                android.R.color.holo_red_dark
            ))
            min60 = true
            Thread{
                Thread.sleep((60000 * 60).toLong())
                if (min60) exitApplication()
            }.start()
            dialog.dismiss()
        }
    }

}