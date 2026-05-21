package com.example.pdsesimongameapp

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import kotlin.math.PI
import kotlin.math.sin

class AudioGameManager {
    private val sampleRate = 44100 //campioni al secondo standard CD audio

    private val frequenze = mapOf(
        'R' to 261.63, // DO
        'G' to 293.66, // RE
        'B' to 329.63, // MI
        'Y' to 349.23, // FA
        'M' to 392.00, // SOL
        'C' to 440.00  // LA
    )

    fun riproduciTono(char : Char, durataMs: Int = 350){
        val frequenza = frequenze[char] ?: return
        val numSamples = (durataMs * sampleRate / 1000.0).toInt()
        val samples = ShortArray(numSamples)
        for(i in samples.indices){
            val angle = 2.0 * PI * i * frequenza / sampleRate

            samples[i] = (sin(angle) * Short.MAX_VALUE).toInt().toShort()
        }
        val audioTrack = AudioTrack(
            AudioManager.STREAM_MUSIC,
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            samples.size * 2,
            AudioTrack.MODE_STATIC
        )

        audioTrack.write(samples,0,samples.size)
        audioTrack.setNotificationMarkerPosition(samples.size)

        audioTrack.setPlaybackPositionUpdateListener(
            object : AudioTrack.OnPlaybackPositionUpdateListener{
                override fun onMarkerReached(track: AudioTrack?) {
                    track?.release()
                }

                override fun onPeriodicNotification(track: AudioTrack?) {
                    //
                }
            }
        )
        audioTrack.play()
    }

}