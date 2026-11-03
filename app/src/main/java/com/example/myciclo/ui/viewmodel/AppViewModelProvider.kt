package com.example.myciclo.ui.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myciclo.MyApplication
import com.example.myciclo.data.room.AppDatabase

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            LoginViewModel(
                myApplication().container.usuarioRepository,
                myApplication().container.sesionRepository
            )
        }
        initializer {
            RegistroViewModel(
                myApplication().container.usuarioRepository
            )
        }
        initializer {
            val db = AppDatabase.getInstance(myApplication().applicationContext)
            DiarioViewModel(
                db.registroDiarioDao()
            )
        }
    }
}

fun CreationExtras.myApplication(): MyApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApplication)