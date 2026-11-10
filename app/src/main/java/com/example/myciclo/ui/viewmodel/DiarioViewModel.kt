package com.example.myciclo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myciclo.data.room.RegistroDiarioDao
import com.example.myciclo.data.room.RegistroDiarioEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

const val ID_USUARIO_INVITADO = 0L

data class EstadoDiarioUI(
    val estadoFertilidad: String = "Infértil / Fase Folicular Temprana",
    val mensajeDiagnostico: String = "No se detecta patrón de helechos. Probabilidad baja de ovulación.",
    val colorAlertaHex: Long = 0xFF4CAF50,
    val diasRestantesVentana: Int = 0,
    val enVentanaFertil: Boolean = false,
    val registros: List<RegistroDiarioEntity> = emptyList()
)

class DiarioViewModel(
    private val registroDiarioDao: RegistroDiarioDao,
    private val usuarioIdActual: Long = 1L
) : ViewModel() {

    private val _uiState = MutableStateFlow(EstadoDiarioUI())
    val uiState: StateFlow<EstadoDiarioUI> = _uiState.asStateFlow()

    init {
        cargarYAnalizarRegistros()
    }

    fun cargarYAnalizarRegistros() {
        viewModelScope.launch {
            registroDiarioDao.obtenerRegistrosPorUsuario(usuarioIdActual).collect { listaRegistros ->
                evaluarEstadoCiclo(listaRegistros)
            }
        }
    }

    fun guardarNuevoRegistro(
        diaDelCiclo: Int,
        estadoSaliva: String,
        notas: String,
        fotoPath: String? = null
    ) {
        viewModelScope.launch {
            val nuevoRegistro = RegistroDiarioEntity(
                usuarioId = usuarioIdActual,
                fecha = System.currentTimeMillis().toString(),
                diaDelCiclo = diaDelCiclo,
                estadoCristalizacion = estadoSaliva,
                fotoPath = fotoPath,
                notas = notas
            )
            registroDiarioDao.insertarRegistro(nuevoRegistro)
        }
    }

    private fun evaluarEstadoCiclo(registros: List<RegistroDiarioEntity>) {
        if (registros.isEmpty()) {
            _uiState.value = EstadoDiarioUI(
                estadoFertilidad = "Sin Datos",
                mensajeDiagnostico = "Registra tu primer análisis de saliva para comenzar el seguimiento dinámico.",
                colorAlertaHex = 0xFF757575,
                registros = emptyList()
            )
            return
        }

        val registrosOrdenados = registros.sortedByDescending { it.diaDelCiclo }
        val ultimoRegistro = registrosOrdenados.first()

        val registroPrimerHelecho = registrosOrdenados.lastOrNull {
            it.estadoCristalizacion.contains("Helecho", ignoreCase = true)
        }

        if (registroPrimerHelecho != null) {
            val diaInicioHelecho = registroPrimerHelecho.diaDelCiclo
            val diaActual = ultimoRegistro.diaDelCiclo
            val diasTranscurridos = diaActual - diaInicioHelecho

            if (diasTranscurridos in 0..7) {
                val diaVentana = diasTranscurridos + 1
                val diasRestantes = 8 - diaVentana

                _uiState.value = EstadoDiarioUI(
                    estadoFertilidad = "VENTANA FÉRTIL ACTIVA (Día $diaVentana de 8)",
                    mensajeDiagnostico = "⚠️ ALTA FERTILIDAD: Se detectó cristalización de estrógenos. Te quedan $diasRestantes días de máxima probabilidad de ovulación.",
                    colorAlertaHex = 0xFFE91E63,
                    diasRestantesVentana = diasRestantes,
                    enVentanaFertil = true,
                    registros = registros
                )
                return
            }
        }

        when {
            ultimoRegistro.diaDelCiclo in 1..5 -> {
                _uiState.value = EstadoDiarioUI(
                    estadoFertilidad = "Fase Menstrual (Día ${ultimoRegistro.diaDelCiclo})",
                    mensajeDiagnostico = "Periodo de sangrado. Nivel bajo de estrógenos. Infértil.",
                    colorAlertaHex = 0xFF2196F3,
                    registros = registros
                )
            }
            ultimoRegistro.estadoCristalizacion.contains("Sin helechos", ignoreCase = true) -> {
                _uiState.value = EstadoDiarioUI(
                    estadoFertilidad = "Periodo Infértil / Seguros",
                    mensajeDiagnostico = "Saliva con estructura amorfa/puntos. No hay estrógenos altos.",
                    colorAlertaHex = 0xFF4CAF50,
                    registros = registros
                )
            }
            else -> {
                _uiState.value = EstadoDiarioUI(
                    estadoFertilidad = "Fase Lútea / Post-Ovulatoria",
                    mensajeDiagnostico = "Ventana fértil finalizada. Probabilidad baja de concepción hasta el próximo ciclo.",
                    colorAlertaHex = 0xFF9C27B0,
                    registros = registros
                )
            }
        }
    }
}