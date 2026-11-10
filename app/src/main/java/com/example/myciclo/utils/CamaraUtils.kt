package com.example.myciclo.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CamaraUtils {

    /** Crea un archivo vacío y único dentro de la carpeta privada de la app. */
    fun crearArchivoFoto(context: Context): File {
        val carpeta = File(context.filesDir, "fotos").apply { mkdirs() }
        val marca = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        return File(carpeta, "muestra_$marca.jpg")
    }

    /** Dirección segura (content://) que se le entrega a la app de cámara. */
    fun uriDe(context: Context, archivo: File): Uri =
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)
}