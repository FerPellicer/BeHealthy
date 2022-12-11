package com.example.behealthy.model.utils

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.behealthy.R

object FaceColor {

    fun getHexColor(value: Int): String {
        if (value <= 49) {
            val red = 255 - (value * 5)
            // Devolvemos la cadena hexadecimal del color rojo
            return "#" + Integer.toHexString(red) + "0000"
        } else {
            if (value == 49) {
                return "#FF0000"
            }
            val green = (value - 50) * 5
            return "#00" + Integer.toHexString(green) + "00"
        }
    }


    fun get_face_drawable(context: Context, rating: String): Drawable? {


        // Get the hexadecimal color string from the getHexColor() function
        val ratingInt = rating.toInt()
        val hexColor = getHexColor(ratingInt)

        // Parse the hexadecimal color string into an integer color value
        val color = Color.parseColor(hexColor)

        // Get the Drawable object

        val id_drawable: Int

        if (ratingInt <= 34) id_drawable = R.drawable.sad_face
        else if (ratingInt > 34 && ratingInt <= 66) id_drawable = R.drawable.neutral_face
        else id_drawable = R.drawable.smile_face


        val drawable: Drawable? =
            AppCompatResources.getDrawable(context, id_drawable)

        // Obtenemos el valor entero del color deseado a partir de un valor RGB o ARG.

        val colorValue = Color.parseColor(hexColor)

        drawable?.colorFilter = PorterDuffColorFilter(colorValue, PorterDuff.Mode.SRC_IN)
        return drawable


    }







    fun getColor(percent: Int): String {
        if (percent < 1 || percent > 100) {
            return "null"
        }

        val color: String
        when {
            percent <= 25 -> {
                // Cálculo del tono de rojo en hexadecimal
                val redHex = Math.round((255 * percent / 25).toDouble()).toString(16)
                val redHexFormatted = if (redHex.length == 1) "0$redHex" else redHex
                color = "#FF$redHexFormatted$redHexFormatted"
            }
            percent <= 50 -> {
                // Cálculo del tono de naranja en hexadecimal
                val orangeHex = Math.round((255 * (percent - 25) / 25).toDouble()).toString(16)
                val orangeHexFormatted = if (orangeHex.length == 1) "0$orangeHex" else orangeHex
                color = "#FF" + "$orangeHexFormatted" + "00"
            }
            percent <= 75 -> {
                // Cálculo del tono de verde claro en hexadecimal
                val lightGreenHex = Math.round((255 * (percent - 50) / 25).toDouble()).toString(16)
                val lightGreenHexFormatted = if (lightGreenHex.length == 1) "0$lightGreenHex" else lightGreenHex
                color = "#$lightGreenHexFormatted" + "FF" + "$lightGreenHexFormatted"
            }
            else -> {
                // Cálculo del tono de verde oscuro en hexadecimal
                val darkGreenHex = Math.round((255 * (100 - percent) / 25).toDouble()).toString(16)
                val darkGreenHexFormatted = if (darkGreenHex.length == 1) "0$darkGreenHex" else darkGreenHex
                color = "#$darkGreenHexFormatted$darkGreenHexFormatted" + "00"
            }
        }

        return color
    }
}