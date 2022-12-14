package com.example.behealthy.model.utils

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
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

        // Get the Drawable object

        val idDrawable: Int

        if (ratingInt <= 34) idDrawable = R.drawable.sad_face
        else if (ratingInt > 34 && ratingInt <= 66) idDrawable = R.drawable.neutral_face
        else idDrawable = R.drawable.smile_face


        val drawable: Drawable? =
            AppCompatResources.getDrawable(context, idDrawable)

        // Obtenemos el valor entero del color deseado a partir de un valor RGB o ARG.

        val colorValue = Color.parseColor(hexColor)


        drawable?.colorFilter = PorterDuffColorFilter(colorValue, PorterDuff.Mode.SRC_IN)
        return drawable


    }


}