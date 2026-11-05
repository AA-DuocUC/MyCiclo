package com.example.myciclo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myciclo.data.room.EventoCicloDao
import com.example.myciclo.data.room.EventoCicloEntity
import com.example.myciclo.data.room.RegistroDiarioDao
import com.example.myciclo.data.room.RegistroDiarioEntity
import com.example.myciclo.data.room.TipoEventoCiclo
import com.example.myciclo.utils.FechaUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class EstadoCalendarioUI(
    val anio: Int,
    val mes: Int, // 0 a 11
    val fechaSeleccionada: Int? = null,
    val eventos: List<EventoCicloEntity> = emptyList(),
    val registros: List<RegistroDiarioEntity> = emptyList()
)

class CalendarioViewModel(
    private val eventoCicloDao: EventoCicloDao,
    registroDiarioDao: RegistroDiarioDao,
    private val usuarioIdActual: Long = 1L
) : ViewModel() {

    private val hoy = FechaUtils.hoy()
    private val _mesVisible = MutableStateFlow(FechaUtils.anio(hoy) to FechaUtils.mes(hoy))
    private val _fechaSeleccionada = MutableStateFlow<Int?>(hoy)

    val uiState: StateFlow<EstadoCalendarioUI> = combine(
        _mesVisible,
        _fechaSeleccionada,
        eventoCicloDao.obtenerEventos(usuarioIdActual),
        registroDiarioDao.obtenerRegistrosPorUsuario(usuarioIdActual)
    ) { mesVisible, seleccion, eventos, registros ->
        EstadoCalendarioUI(
            anio = mesVisible.first,
            mes = mesVisible.second,
            fechaSeleccionada = seleccion,
            eventos = eventos,
            registros = registros
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = EstadoCalendarioUI(
            anio = FechaUtils.anio(hoy),
            mes = FechaUtils.mes(hoy),
            fechaSeleccionada = hoy
        )
    )

    fun mesAnterior() {
        val (anio, mes) = _mesVisible.value
        _mesVisible.value = if (mes == 0) (anio - 1) to 11 else anio to (mes - 1)
    }

    fun mesSiguiente() {
        val (anio, mes) = _mesVisible.value
        _mesVisible.value = if (mes == 11) (anio + 1) to 0 else anio to (mes + 1)
    }

    fun irAHoy() {
        _mesVisible.value = FechaUtils.anio(hoy) to FechaUtils.mes(hoy)
        _fechaSeleccionada.value = hoy
    }

    fun seleccionarFecha(fecha: Int) {
        _fechaSeleccionada.value = fecha
    }

    /** Salta a la fecha de un evento (se usa al tocar el historial). */
    fun irAFecha(fecha: Int) {
        _mesVisible.value = FechaUtils.anio(fecha) to FechaUtils.mes(fecha)
        _fechaSeleccionada.value = fecha
    }

    fun guardarEvento(tipo: TipoEventoCiclo, nota: String) {
        val fecha = _fechaSeleccionada.value ?: return
        viewModelScope.launch {
            eventoCicloDao.insertar(
                EventoCicloEntity(
                    usuarioId = usuarioIdActual,
                    fecha = fecha,
                    tipo = tipo.name,
                    nota = nota.trim()
                )
            )
        }
    }

    fun eliminarEvento(evento: EventoCicloEntity) {
        viewModelScope.launch {
            eventoCicloDao.eliminar(evento)
        }
    }
}