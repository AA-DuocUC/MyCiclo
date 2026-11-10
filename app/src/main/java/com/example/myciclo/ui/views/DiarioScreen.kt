package com.example.myciclo.ui.views

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myciclo.ui.viewmodel.DiarioViewModel
import com.example.myciclo.utils.CamaraUtils
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiarioScreen(viewModel: DiarioViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var diaText by remember { mutableStateOf("") }
    var opcionSaliva by remember { mutableStateOf("Sin helechos (Infértil)") }
    var notasText by remember { mutableStateOf("") }

    // Foto ya tomada (lista para guardar con el registro) y foto en proceso de captura
    var fotoRuta by rememberSaveable { mutableStateOf<String?>(null) }
    var rutaPendiente by rememberSaveable { mutableStateOf<String?>(null) }
    var fotoAmpliada by remember { mutableStateOf<String?>(null) }

    val opcionesSaliva = listOf(
        "Sin helechos (Infértil)",
        "Patrón de helechos (Fértil)",
        "Patrón marcado (Pico Ovulación)"
    )

    // Abre la cámara del teléfono y avisa si la foto se tomó o se canceló
    val camaraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { fotoTomada ->
        val nueva = rutaPendiente
        if (fotoTomada && nueva != null) {
            fotoRuta?.let { File(it).delete() } // reemplaza la foto anterior
            fotoRuta = nueva
        } else {
            nueva?.let { File(it).delete() }    // se canceló: no deja archivo vacío
        }
        rutaPendiente = null
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(uiState.colorAlertaHex)
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "ESTADO EN TIEMPO REAL",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = uiState.estadoFertilidad,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.mensajeDiagnostico,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
            }
        }

        item {
            Text(
                text = "Ingresar Nuevo Análisis EVA",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = diaText,
                    onValueChange = { diaText = it },
                    label = { Text("Día del ciclo (N°)") },
                    modifier = Modifier.weight(1f)
                )

                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.weight(2f)
                ) {
                    OutlinedTextField(
                        value = opcionSaliva,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Patrón Saliva") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        opcionesSaliva.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion) },
                                onClick = {
                                    opcionSaliva = opcion
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = notasText,
                onValueChange = { notasText = it },
                label = { Text("Notas / Síntomas (Opcional)") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // ----- Foto de la muestra -----
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = {
                        val archivo = CamaraUtils.crearArchivoFoto(context)
                        rutaPendiente = archivo.absolutePath
                        try {
                            camaraLauncher.launch(CamaraUtils.uriDe(context, archivo))
                        } catch (e: Exception) {
                            archivo.delete()
                            rutaPendiente = null
                            Toast.makeText(
                                context,
                                "No se encontró una app de cámara en este dispositivo",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (fotoRuta == null) "📷 Tomar foto de la muestra" else "📷 Volver a tomar foto")
                }

                fotoRuta?.let { ruta ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Box {
                        FotoMiniatura(
                            ruta = ruta,
                            maxLado = 800,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }
                    TextButton(
                        onClick = {
                            File(ruta).delete()
                            fotoRuta = null
                        }
                    ) {
                        Text("Quitar foto")
                    }
                }
            }
        }

        item {
            Button(
                onClick = {
                    val diaNum = diaText.toIntOrNull() ?: 1
                    viewModel.guardarNuevoRegistro(diaNum, opcionSaliva, notasText, fotoRuta)
                    diaText = ""
                    notasText = ""
                    fotoRuta = null
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Registro y Evaluar")
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Historial del Ciclo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(uiState.registros) { registro ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Día ${registro.diaDelCiclo} del Ciclo",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = registro.estadoCristalizacion,
                            style = MaterialTheme.typography.bodySmall
                        )
                        if (registro.notas.isNotEmpty()) {
                            Text(
                                text = "Nota: ${registro.notas}",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }

                    registro.fotoPath?.let { ruta ->
                        FotoMiniatura(
                            ruta = ruta,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { fotoAmpliada = ruta }
                        )
                    }
                }
            }
        }
    }

    // Foto en grande al tocar una miniatura del historial
    fotoAmpliada?.let { ruta ->
        AlertDialog(
            onDismissRequest = { fotoAmpliada = null },
            confirmButton = {
                TextButton(onClick = { fotoAmpliada = null }) { Text("Cerrar") }
            },
            text = {
                FotoMiniatura(
                    ruta = ruta,
                    maxLado = 1600,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        )
    }
}