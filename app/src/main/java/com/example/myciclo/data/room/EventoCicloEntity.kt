package com.example.myciclo.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Un cambio registrado por el usuario en una fecha concreta.
 * Ej: "3 de enero: Inicio de ciclo", "12 de febrero: Fase ovulatoria".
 *
 * La fecha se guarda como número yyyyMMdd (ej: 20260103) para poder ordenarla fácilmente.
 */
@Entity(tableName = "eventos_ciclo")
data class EventoCicloEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val usuarioId: Long,
    val fecha: Int,
    val tipo: String,
    val nota: String = ""
)

/** Tipos de cambio que el usuario puede registrar, con el color que se ve en el calendario. */
enum class TipoEventoCiclo(val etiqueta: String, val colorHex: Long) {
    INICIO_CICLO("Inicio de ciclo", 0xFF2196F3),
    FASE_FOLICULAR("Fase folicular", 0xFF4CAF50),
    FASE_OVULATORIA("Fase ovulatoria", 0xFFE91E63),
    FASE_LUTEA("Fase lútea", 0xFF9C27B0),
    OTRO("Otro cambio", 0xFF757575);

    companion object {
        fun desde(nombre: String): TipoEventoCiclo =
            entries.firstOrNull { it.name == nombre } ?: OTRO
    }
}