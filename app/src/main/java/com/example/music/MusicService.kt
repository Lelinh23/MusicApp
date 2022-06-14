package com.example.music

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat

class MusicService:Service(), AudioManager.OnAudioFocusChangeListener {

    private val myBinder = MyBinder()
    var phat_nhac: MediaPlayer? = null
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var runable: Runnable
    lateinit var audioManager: AudioManager

    override fun onBind(intent: Intent?): IBinder {
        mediaSession = MediaSessionCompat(baseContext,"Nhạc của tôi")
        return myBinder
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun showNotification(iconPhatBaiHat: Int) {
        val intent = Intent(baseContext, MainActivity::class.java)
        val contextIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val notification = NotificationCompat.Builder(baseContext, ApplicationClass.CHANNEL_ID)
            .setContentIntent(contextIntent)
            .setContentTitle(PlayerActivity.musicListPN[PlayerActivity.vitriBH].tenBH)
            .setContentText(PlayerActivity.musicListPN[PlayerActivity.vitriBH].TacGia)
            .setSmallIcon(R.drawable.icon_danh_sach_phat)
            .setLargeIcon(BitmapFactory.decodeResource(resources,R.drawable.man_hinh_screen))
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.icon_bai_hat_truoc, "Previous", null)
            .addAction(R.drawable.icon_phat_bai_hat, "Play", null)
            .addAction(R.drawable.icon_bai_hat_next, "Next", null)
            .addAction(R.drawable.icon_exit, "Exit", null)
            .build()
        startForeground(13, notification)
    }

    fun createPhatNhac(){
        try {
            if (PlayerActivity.musicService!!.phat_nhac == null) PlayerActivity.musicService!!.phat_nhac = MediaPlayer()
            PlayerActivity.musicService!!.phat_nhac!!.reset()
            PlayerActivity.musicService!!.phat_nhac!!.setDataSource(PlayerActivity.musicListPN[PlayerActivity.vitriBH].link)
            PlayerActivity.musicService!!.phat_nhac!!.prepare()
            PlayerActivity.musicService!!.phat_nhac!!.start()
            PlayerActivity.isPlaying = true
            PlayerActivity.binding.btnPhatBHPa.setIconResource(R.drawable.icon_phat_bai_hat)
            PlayerActivity.binding.tvSeekBarStart.text = DinhDangTime(PlayerActivity.musicService!!.phat_nhac!!.currentPosition.toLong())
            PlayerActivity.binding.tvSeekBarEnd.text = DinhDangTime(PlayerActivity.musicService!!.phat_nhac!!.duration.toLong())
            PlayerActivity.binding.seekBar.progress = 0
            PlayerActivity.binding.seekBar.max = PlayerActivity.musicService!!.phat_nhac!!.duration
            PlayerActivity.nowPlayingId = PlayerActivity.musicListPN[PlayerActivity.vitriBH].id
        }catch (e: Exception){
            return
        }
    }

    inner class MyBinder:Binder(){
        fun currentService(): MusicService{
            return this@MusicService
        }
    }
    fun CaiDatseekBar(){
        runable = Runnable {
            PlayerActivity.binding.tvSeekBarStart.text = DinhDangTime(phat_nhac!!.currentPosition.toLong())
            PlayerActivity.binding.seekBar.progress= phat_nhac!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runable,200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runable,0)
    }

    override fun onAudioFocusChange(focusChange: Int) {
        if (focusChange <= 0){
            // tam dung phat nhac
            PlayerActivity.binding.btnPhatBHPa.setIconResource(R.drawable.icon_dung_bai_hat)
            NowPlaying.binding.btnPlay.setIconResource(R.drawable.icon_dung_bai_hat)
            PlayerActivity.isPlaying = false
            phat_nhac!!.pause()
        }
        else{
            //phat nhac
            PlayerActivity.binding.btnPhatBHPa.setIconResource(R.drawable.icon_phat_bai_hat)
            NowPlaying.binding.btnPlay.setIconResource(R.drawable.icon_phat_bai_hat)
            PlayerActivity.isPlaying = true
            phat_nhac!!.start()
        }
    }
}
