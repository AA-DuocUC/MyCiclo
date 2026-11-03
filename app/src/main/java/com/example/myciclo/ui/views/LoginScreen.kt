package com.example.myciclo.ui.views

import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myciclo.ui.components.CampoPassword
import com.example.myciclo.ui.theme.CicloBackground
import com.example.myciclo.ui.theme.CicloDeepText
import com.example.myciclo.ui.theme.CicloMauveText
import com.example.myciclo.ui.theme.CicloPrimary
import com.example.myciclo.ui.viewmodel.AppViewModelProvider
import com.example.myciclo.ui.viewmodel.LoginEvento
import com.example.myciclo.ui.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginExitoso: () -> Unit,
    onIrARegistro: () -> Unit,
    onEntrarInvitado: () -> Unit,
    viewModel: LoginViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.eventos.collect { evento ->
            when (evento) {
                is LoginEvento.LoginExitoso -> {
                    Toast.makeText(context, "¡Bienvenido/a, ${evento.email}!", Toast.LENGTH_SHORT).show()
                    onLoginExitoso()
                }
            }
        }
    }

    val transicion = rememberInfiniteTransition(label = "logo")
    val escala by transicion.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "escala"
    )
    val rotacion by transicion.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotacion"
    )

    Scaffold(
        containerColor = CicloBackground,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CicloBackground,
                    titleContentColor = CicloDeepText,
                ),
                title = {
                    Text(
                        text = "MYCICLO",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        letterSpacing = 2.sp,
                        color = CicloMauveText
                    )
                }
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(CicloBackground)
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = "Logo de MyCiclo",
                tint = CicloPrimary,
                modifier = Modifier
                    .size(72.dp)
                    .graphicsLayer {
                        scaleX = escala
                        scaleY = escala
                        rotationZ = rotacion
                    }
            )
            Spacer(Modifier.height(16.dp))

            Text(
                text = "Iniciar Sesión",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = CicloDeepText
            )
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = uiState.usuario,
                onValueChange = viewModel::onUsuarioChange,
                label = { Text("Usuario / Email") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CicloPrimary,
                    focusedLabelColor = CicloPrimary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            CampoPassword(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(20.dp))

            if (uiState.error.isNotEmpty()) {
                Text(uiState.error, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(8.dp))
            }

            Button(
                onClick = viewModel::ingresar,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CicloPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    "Ingresar",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(Modifier.height(12.dp))

            TextButton(onClick = onIrARegistro) {
                Text("¿No tienes cuenta? Registrarse", color = CicloPrimary)
            }

            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick = onEntrarInvitado,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    "Continuar sin iniciar sesión (modo offline)",
                    color = CicloPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}