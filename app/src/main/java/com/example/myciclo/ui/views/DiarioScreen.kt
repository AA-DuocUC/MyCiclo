package com.example.myciclo.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myciclo.ui.viewmodel.DiarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiarioScreen(viewModel: DiarioViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    var diaText by remember { mutableStateOf("") }
    var opcionSaliva by remember { mutableStateOf("Sin helechos (Infértil)") }
    var notasText by remember { mutableStateOf("") }

    val opcionesSaliva = listOf("Sin helechos (Infértil)", "Patrón de helechos (Fértil)", "Patrón marcado (Pico Ovulación)")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
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

        Text(
            text = "Ingresar Nuevo Análisis EVA",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

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

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = notasText,
            onValueChange = { notasText = it },
            label = { Text("Notas / Síntomas (Opcional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                val diaNum = diaText.toIntOrNull() ?: 1
                viewModel.guardarNuevoRegistro(diaNum, opcionSaliva, notasText)
                diaText = ""
                notasText = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Registro y Evaluar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Historial del Ciclo",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
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
                        Column {
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
                    }
                }
            }
        }
    }
}