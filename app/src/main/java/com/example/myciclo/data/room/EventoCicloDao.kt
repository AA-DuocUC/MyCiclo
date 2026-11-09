package com.example.myciclo.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EventoCicloDao {

    @Query("SELECT * FROM eventos_ciclo WHERE usuarioId = :usuarioId ORDER BY fecha ASC, id ASC")
    fun obtenerEventos(usuarioId: Long): Flow<List<EventoCicloEntity>>

    @Insert
    suspend fun insertar(evento: EventoCicloEntity)

    @Delete
    suspend fun eliminar(evento: EventoCicloEntity)
}