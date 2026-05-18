package com.example.pdsesimongameapp

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan

object EditTextUtilis {
    fun getEditString(s: String, errorIndex : Int ) : SpannableString{
        errorIndex.coerceIn(0, s.length) //Evita crash se index è fuori dai limiti [0,s.length]
        val spannableString = SpannableString(s)
        spannableString.setSpan(
            ForegroundColorSpan(Color.RED),
            errorIndex,
            s.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return  spannableString
    }
}