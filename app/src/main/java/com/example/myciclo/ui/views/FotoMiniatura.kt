package com.example.myciclo.ui.views

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Muestra una foto guardada en el teléfono.
 * Si el archivo no existe, no dibuja nada.
 */
@Composable
fun FotoMiniatura(
    ruta: String,
    modifier: Modifier = Modifier,
    maxLado: Int = 400,
    contentScale: ContentScale = ContentScale.Crop
) {
    val imagen by produceState<ImageBitmap?>(initialValue = null, ruta, maxLado) {
        value = withContext(Dispatchers.IO) { cargarBitmap(ruta, maxLado)?.asImageBitmap() }
    }

    imagen?.let {
        Image(
            bitmap = it,
            contentDescription = "Foto de la muestra de saliva",
            modifier = modifier,
            contentScale = contentScale
        )
    }
}

/** Carga la foto reducida (para no gastar memoria) y corrige su rotación. */
private fun cargarBitmap(ruta: String, maxLado: Int): Bitmap? {
    return try {
        val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(ruta, limites)

        var muestreo = 1
        while (maxOf(limites.outWidth, limites.outHeight) / (muestreo * 2) >= maxLado) {
            muestreo *= 2
        }

        val opciones = BitmapFactory.Options().apply { inSampleSize = muestreo }
        val bitmap = BitmapFactory.decodeFile(ruta, opciones) ?: return null

        val orientacion = ExifInterface(ruta).getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
        val grados = when (orientacion) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }
        if (grados == 0f) {
            bitmap
        } else {
            Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width, bitmap.height,
                Matrix().apply { postRotate(grados) }, true
            )
        }
    } catch (e: Exception) {
        null
    }
}