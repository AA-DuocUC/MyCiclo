package com.example.myciclo.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "registros_diarios")
data class RegistroDiarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val usuarioId: Long,
    val fecha: String,
    val diaDelCiclo: Int,
    val estadoCristalizacion: String,
    val fotoPath: String? = null,
    val notas: String = ""
)