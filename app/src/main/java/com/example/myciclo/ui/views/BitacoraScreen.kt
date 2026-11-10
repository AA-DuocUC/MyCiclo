package com.example.myciclo.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myciclo.data.room.EventoCicloEntity
import com.example.myciclo.data.room.RegistroDiarioEntity
import com.example.myciclo.data.room.TipoEventoCiclo
import com.example.myciclo.ui.viewmodel.CalendarioViewModel
import com.example.myciclo.utils.FechaUtils

// ---------- Colores y niveles (según la leyenda del diseño) ----------

private val ColorFondoPantalla = Color(0xFFFAF5F7)
private val ColorSeleccion = Color(0xFFC85A8A)
private val ColorTextoOscuro = Color(0xFF2D2433)
private val ColorTextoSuave = Color(0xFF8E8496)
private val ColorSinDato = Color(0xFFCBD5E1)

private enum class NivelFertilidad(val etiqueta: String, val color: Color) {
    ALTO("Alto", Color(0xFF9333EA)),
    MEDIO("Medio", Color(0xFFE8884A)),
    NULO("Nulo", Color(0xFF5BA88A))
}

/** Convierte el texto de saliva que guarda el diario en un nivel de fertilidad. */
private fun nivelDe(registro: RegistroDiarioEntity): NivelFertilidad {
    val t = registro.estadoCristalizacion
    return when {
        t.contains("Sin helechos", ignoreCase = true) -> NivelFertilidad.NULO
        t.contains("Pico", ignoreCase = true) -> NivelFertilidad.ALTO
        t.contains("helecho", ignoreCase = true) -> NivelFertilidad.MEDIO
        else -> NivelFertilidad.NULO
    }
}

// ---------- Pantalla ----------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BitacoraScreen(
    viewModel: CalendarioViewModel,
    esInvitado: Boolean,
    onVolver: () -> Unit
) {
    val estado by viewModel.uiState.collectAsState()
    val hoy = remember { FechaUtils.hoy() }

    val eventosPorFecha = remember(estado.eventos) { estado.eventos.groupBy { it.fecha } }
    val registrosPorFecha = remember(estado.registros) {
        estado.registros.groupBy { FechaUtils.desdeMillis(it.fecha.toLongOrNull() ?: 0L) }
    }
    // Si un día tiene varios registros, el punto muestra el nivel más alto.
    val nivelPorFecha = remember(registrosPorFecha) {
        registrosPorFecha.mapValues { (_, lista) -> lista.map { nivelDe(it) }.minBy { it.ordinal } }
    }

    var tipoElegido by remember { mutableStateOf(TipoEventoCiclo.INICIO_CICLO) }
    var nota by remember { mutableStateOf("") }
    var menuAbierto by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = ColorFondoPantalla,
        contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // ----- Encabezado -----
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(listOf(Color(0xFFFCE4F0), Color(0xFFFDF1F6)))
                        )
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Bitácora",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 26.sp,
                            color = ColorTextoOscuro
                        )
                        Text(
                            text = "${FechaUtils.NOMBRES_MES[estado.mes]} ${estado.anio}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ColorTextoSuave
                        )
                    }
                    if (esInvitado) {
                        Box(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color(0xFFDDF3E8))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "SIN CONEXIÓN",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E8B63)
                            )
                        }
                    }
                }
            }

            // ----- Leyenda -----
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        NivelFertilidad.entries.forEach {
                            ItemLeyenda(color = it.color, texto = it.etiqueta)
                        }
                    }
                    ItemLeyenda(color = ColorSinDato, texto = "Sin dato", hueco = true)
                }
            }

            // ----- Calendario -----
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = viewModel::mesAnterior) {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Mes anterior")
                        }
                        Spacer(Modifier.weight(1f))
                        TextButton(onClick = viewModel::irAHoy) {
                            Text("Hoy", color = ColorSeleccion, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = viewModel::mesSiguiente) {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Mes siguiente")
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth()) {
                        listOf("L", "M", "X", "J", "V", "S", "D").forEach {
                            Text(
                                text = it,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorTextoSuave
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))

                    val desfase = FechaUtils.primerDiaSemana(estado.anio, estado.mes)
                    val totalDias = FechaUtils.diasEnMes(estado.anio, estado.mes)
                    val filas = (desfase + totalDias + 6) / 7

                    for (fila in 0 until filas) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            for (columna in 0 until 7) {
                                val dia = fila * 7 + columna - desfase + 1
                                if (dia in 1..totalDias) {
                                    val fecha = FechaUtils.codificar(estado.anio, estado.mes, dia)
                                    val futuro = fecha > hoy
                                    val tipoCambio = eventosPorFecha[fecha]?.firstOrNull()
                                        ?.let { TipoEventoCiclo.desde(it.tipo) }
                                    CeldaDia(
                                        modifier = Modifier.weight(1f),
                                        dia = dia,
                                        futuro = futuro,
                                        seleccionado = fecha == estado.fechaSeleccionada,
                                        esHoy = fecha == hoy,
                                        nivel = nivelPorFecha[fecha],
                                        colorCambio = tipoCambio?.let { Color(it.colorHex) },
                                        onClick = { if (!futuro) viewModel.seleccionarFecha(fecha) }
                                    )
                                } else {
                                    Spacer(Modifier.weight(1f))
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Un contorno de color marca un día con cambio de fase registrado.",
                        fontSize = 11.sp,
                        color = ColorTextoSuave,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }

            // ----- Tarjeta del día seleccionado -----
            item {
                val seleccion = estado.fechaSeleccionada
                if (seleccion != null) {
                    val registrosDia = registrosPorFecha[seleccion].orEmpty()
                    val eventosDia = eventosPorFecha[seleccion].orEmpty()

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = FechaUtils.formatearConDiaSemana(seleccion),
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = ColorTextoOscuro
                            )
                            Spacer(Modifier.height(16.dp))

                            if (registrosDia.isEmpty() && eventosDia.isEmpty()) {
                                Text(
                                    text = "Sin registro para este día",
                                    color = ColorTextoSuave,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                )
                            } else {
                                registrosDia.forEach { RegistroFila(it) }
                                eventosDia.forEach { evento ->
                                    EventoFila(
                                        evento = evento,
                                        onEliminar = { viewModel.eliminarEvento(evento) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ----- Formulario: registrar cambio de fase -----
            item {
                if (estado.fechaSeleccionada != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Registrar cambio en esta fecha",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = ColorTextoOscuro
                            )
                            Spacer(Modifier.height(12.dp))

                            ExposedDropdownMenuBox(
                                expanded = menuAbierto,
                                onExpandedChange = { menuAbierto = it }
                            ) {
                                OutlinedTextField(
                                    value = tipoElegido.etiqueta,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Tipo de cambio") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuAbierto)
                                    },
                                    modifier = Modifier
                                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                        .fillMaxWidth()
                                )
                                ExposedDropdownMenu(
                                    expanded = menuAbierto,
                                    onDismissRequest = { menuAbierto = false }
                                ) {
                                    TipoEventoCiclo.entries.forEach { tipo ->
                                        DropdownMenuItem(
                                            text = { Text(tipo.etiqueta) },
                                            leadingIcon = {
                                                Box(
                                                    Modifier
                                                        .size(10.dp)
                                                        .background(Color(tipo.colorHex), CircleShape)
                                                )
                                            },
                                            onClick = {
                                                tipoElegido = tipo
                                                menuAbierto = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = nota,
                                onValueChange = { nota = it },
                                label = { Text("Nota (opcional)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    viewModel.guardarEvento(tipoElegido, nota)
                                    nota = ""
                                },
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ColorSeleccion),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Text("Guardar cambio", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // ----- Historial -----
            item {
                Text(
                    text = "Historial de cambios",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = ColorTextoOscuro,
                    modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 8.dp)
                )
            }
            if (estado.eventos.isEmpty()) {
                item {
                    Text(
                        text = "Aún no registras cambios. Selecciona un día y guárdalo arriba.",
                        color = ColorTextoSuave,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            } else {
                val historial = estado.eventos.sortedByDescending { it.fecha }
                items(historial.size) { indice ->
                    val evento = historial[indice]
                    val tipo = TipoEventoCiclo.desde(evento.tipo)
                    Card(
                        onClick = { viewModel.irAFecha(evento.fecha) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                Modifier
                                    .size(12.dp)
                                    .background(Color(tipo.colorHex), CircleShape)
                            )
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = FechaUtils.formatear(evento.fecha),
                                    fontWeight = FontWeight.Bold,
                                    color = ColorTextoOscuro
                                )
                                Text(
                                    text = tipo.etiqueta,
                                    fontSize = 13.sp,
                                    color = ColorTextoSuave
                                )
                                if (evento.nota.isNotBlank()) {
                                    Text(
                                        text = evento.nota,
                                        fontSize = 13.sp,
                                        color = ColorTextoOscuro
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.navigationBarsPadding().height(24.dp)) }
        }
    }
}

// ---------- Componentes ----------

@Composable
private fun ItemLeyenda(color: Color, texto: String, hueco: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .then(
                    if (hueco) Modifier.border(1.dp, color, CircleShape)
                    else Modifier.background(color, CircleShape)
                )
        )
        Spacer(Modifier.width(6.dp))
        Text(text = texto, fontSize = 12.sp, color = ColorTextoSuave)
    }
}

@Composable
private fun CeldaDia(
    modifier: Modifier,
    dia: Int,
    futuro: Boolean,
    seleccionado: Boolean,
    esHoy: Boolean,
    nivel: NivelFertilidad?,
    colorCambio: Color?,
    onClick: () -> Unit
) {
    val forma = RoundedCornerShape(14.dp)
    Box(
        modifier = modifier
            .padding(horizontal = 3.dp, vertical = 3.dp)
            .height(50.dp)
            .clip(forma)
            .background(if (seleccionado) ColorSeleccion else Color.Transparent)
            .then(
                if (colorCambio != null && !seleccionado)
                    Modifier.border(2.dp, colorCambio, forma)
                else Modifier
            )
            .clickable(enabled = !futuro, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = dia.toString(),
                fontSize = 15.sp,
                fontWeight = if (esHoy || seleccionado) FontWeight.Bold else FontWeight.Medium,
                color = when {
                    seleccionado -> Color.White
                    futuro -> ColorSinDato
                    else -> ColorTextoOscuro
                }
            )
            Spacer(Modifier.height(4.dp))
            when {
                futuro -> Spacer(Modifier.size(5.dp))
                nivel != null -> Box(
                    Modifier
                        .size(5.dp)
                        .background(if (seleccionado) Color.White else nivel.color, CircleShape)
                )
                else -> Box(
                    Modifier
                        .size(5.dp)
                        .border(1.dp, if (seleccionado) Color.White else ColorSinDato, CircleShape)
                )
            }
        }
    }
}

@Composable
private fun RegistroFila(registro: RegistroDiarioEntity) {
    val nivel = nivelDe(registro)
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(10.dp).background(nivel.color, CircleShape))
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Fertilidad ${nivel.etiqueta.lowercase()} · Día ${registro.diaDelCiclo} del ciclo",
                fontWeight = FontWeight.Bold,
                color = ColorTextoOscuro
            )
        }
        Text(
            text = registro.estadoCristalizacion,
            fontSize = 13.sp,
            color = ColorTextoSuave,
            modifier = Modifier.padding(start = 18.dp)
        )
        if (registro.notas.isNotBlank()) {
            Text(
                text = registro.notas,
                fontSize = 13.sp,
                color = ColorTextoOscuro,
                modifier = Modifier.padding(start = 18.dp)
            )
        }
        registro.fotoPath?.let { ruta ->
            FotoMiniatura(
                ruta = ruta,
                modifier = Modifier
                    .padding(start = 18.dp, top = 8.dp)
                    .size(96.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        }
        HorizontalDivider(modifier = Modifier.padding(top = 12.dp))
    }
}

@Composable
private fun EventoFila(evento: EventoCicloEntity, onEliminar: () -> Unit) {
    val tipo = TipoEventoCiclo.desde(evento.tipo)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(10.dp).background(Color(tipo.colorHex), CircleShape))
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = tipo.etiqueta, fontWeight = FontWeight.Bold, color = ColorTextoOscuro)
            if (evento.nota.isNotBlank()) {
                Text(text = evento.nota, fontSize = 13.sp, color = ColorTextoSuave)
            }
        }
        IconButton(onClick = onEliminar) {
            Icon(Icons.Default.Delete, contentDescription = "Eliminar cambio", tint = ColorTextoSuave)
        }
    }
}