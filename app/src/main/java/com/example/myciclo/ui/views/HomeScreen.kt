package com.example.myciclo.ui.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myciclo.ui.viewmodel.DiarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    diarioViewModel: DiarioViewModel,
    onCerrarSesion: () -> Unit,
    esInvitado: Boolean = false
) {
    var pestanaSeleccionada by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (esInvitado) "MyCiclo (Invitado)" else "MyCiclo") },
                actions = {
                    IconButton(onClick = onCerrarSesion) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = if (esInvitado) "Salir del modo offline" else "Cerrar Sesión"
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = pestanaSeleccionada == 0,
                    onClick = { pestanaSeleccionada = 0 },
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Diario") },
                    label = { Text("Ovulación / Diario") }
                )
                NavigationBarItem(
                    selected = pestanaSeleccionada == 1,
                    onClick = { pestanaSeleccionada = 1 },
                    icon = { Icon(Icons.Default.Info, contentDescription = "Educación") },
                    label = { Text("Módulo Educativo") }
                )
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            if (esInvitado) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Modo offline: tus registros se guardan solo en este dispositivo y no están asociados a una cuenta.",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
            Box(modifier = Modifier.weight(1f)) {
                when (pestanaSeleccionada) {
                    0 -> DiarioScreen(viewModel = diarioViewModel)
                    1 -> EducacionContenido()
                }
            }
        }
    }
}

@Composable
fun EducacionContenido() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "Guía Completa: Método EVA y Cristalización",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "El método de saliva EVA analiza los niveles de estrógeno en el cuerpo. A mayor nivel de estrógeno (previo a la ovulación), la saliva cristaliza en forma de helechos al secarse.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "🔬 1. Patrón Amorfo / Puntos (Infértil)",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• Apariencia: Puntos aislados, burbujas o líneas desorganizadas.")
                    Text("• Significado: Niveles bajos de estrógeno. Ventana fértil cerrada.")
                    Text("• Recomendación: Registro de control diario habitual.")
                }
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "🌿 2. Transición / Formación de Helechos (Inicio Ventana Fértil)",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• Apariencia: Comienzan a verse pequeñas ramificaciones finas.")
                    Text("• Significado: El estrógeno está aumentando. Inicio de los 8 días fértiles.")
                    Text("• Recomendación: Alta probabilidad de concepción en los próximos días.")
                }
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "❄️ 3. Patrón de Helecho Tupido (Pico de Ovulación)",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• Apariencia: Estructura de cristalización densa, similar a hojas de helecho o escarcha.")
                    Text("• Significado: Máximo pico de estrógeno. Ovulación inminente (24-48 horas).")
                    Text("• Recomendación: Máxima fertilidad del ciclo.")
                }
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "💡 Consejos para la Toma de Muestra",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("1. Realizar la prueba en ayunas por la mañana, antes de comer, beber o lavarse los dientes.")
                    Text("2. Colocar una pequeña gota de saliva en el lente o portaobjetos sin hacer burbujas.")
                    Text("3. Dejar secar completamente al aire (10 a 15 minutos) antes de observar.")
                }
            }
        }
    }
}