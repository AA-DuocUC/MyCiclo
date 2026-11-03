package com.example.myciclo.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroDiarioDao {

    @Query("SELECT * FROM registros_diarios WHERE usuarioId = :usuarioId ORDER BY diaDelCiclo ASC")
    fun obtenerRegistrosPorUsuario(usuarioId: Long): Flow<List<RegistroDiarioEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarRegistro(registro: RegistroDiarioEntity)
}