package com.example.myciclo.utils

import java.util.Calendar

/**
 * Utilidades de fechas sin java.time (la app soporta Android 7 / API 24).
 * Una fecha se representa como Int yyyyMMdd (ej: 20260103). El mes va de 0 a 11.
 */
object FechaUtils {

    val NOMBRES_MES = listOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )

    fun codificar(anio: Int, mes: Int, dia: Int): Int = anio * 10000 + (mes + 1) * 100 + dia

    fun anio(fecha: Int): Int = fecha / 10000
    fun mes(fecha: Int): Int = (fecha / 100) % 100 - 1
    fun dia(fecha: Int): Int = fecha % 100

    fun hoy(): Int {
        val c = Calendar.getInstance()
        return codificar(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
    }

    fun diasEnMes(anio: Int, mes: Int): Int {
        val c = Calendar.getInstance()
        c.clear()
        c.set(anio, mes, 1)
        return c.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    /** Posición del día 1 en la semana, empezando en lunes: 0 = lunes ... 6 = domingo. */
    fun primerDiaSemana(anio: Int, mes: Int): Int {
        val c = Calendar.getInstance()
        c.clear()
        c.set(anio, mes, 1)
        val diaSemana = c.get(Calendar.DAY_OF_WEEK) // domingo = 1 ... sábado = 7
        return (diaSemana + 5) % 7
    }

    private val NOMBRES_DIA = listOf(
        "lunes", "martes", "miércoles", "jueves", "viernes", "sábado", "domingo"
    )

    /** Convierte milisegundos (como guarda la fecha el diario) a yyyyMMdd. */
    fun desdeMillis(millis: Long): Int {
        val c = Calendar.getInstance()
        c.timeInMillis = millis
        return codificar(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
    }

    /** 0 = lunes ... 6 = domingo */
    fun diaSemana(fecha: Int): Int {
        val c = Calendar.getInstance()
        c.clear()
        c.set(anio(fecha), mes(fecha), dia(fecha))
        return (c.get(Calendar.DAY_OF_WEEK) + 5) % 7
    }

    /** Ej: 20260929 -> "martes, 29 de septiembre" */
    fun formatearConDiaSemana(fecha: Int): String =
        "${NOMBRES_DIA[diaSemana(fecha)]}, ${dia(fecha)} de ${NOMBRES_MES[mes(fecha)].lowercase()}"

    /** Ej: 20260103 -> "3 de enero de 2026" */
    fun formatear(fecha: Int): String =
        "${dia(fecha)} de ${NOMBRES_MES[mes(fecha)].lowercase()} de ${anio(fecha)}"
}