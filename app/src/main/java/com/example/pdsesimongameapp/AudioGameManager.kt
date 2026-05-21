package com.example.pdsesimongameapp

import android.media.AudioAttributes
import android.media.AudioFormat
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

    //TODO : gestire job per riproduzione tono come per evidenzia view
    //      quando riproduce sequenza o input e giro velocemente perde come l'audio, non si sente nulla
    fun riproduciTono(char : Char, durataMs: Int = 350){
        val frequenza = frequenze[char] ?: return
        val numSamples = (durataMs * sampleRate / 1000.0).toInt()
        val samples = ShortArray(numSamples)
        for(i in samples.indices){
            val angle = 2.0 * PI * i * frequenza / sampleRate

            samples[i] = (sin(angle) * Short.MAX_VALUE).toInt().toShort()
        }

        val bufferSize = samples.size * 2
        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setSampleRate(sampleRate)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()
        track.write(samples,0,samples.size)
        track.play()

        //Basta come clean up
        Thread {
            Thread.sleep(durataMs.toLong() + 100)
            track.release()
        }.start()
    }

}