package com.example.myciclo.data.repository

import com.example.myciclo.data.room.UsuarioDao
import com.example.myciclo.data.room.UsuarioEntity
import com.example.myciclo.utils.PasswordHasher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UsuarioRepository(private val dao: UsuarioDao) {

    suspend fun registrar(email: String, password: String): Boolean {
        // El hash se hace fuera del hilo principal.
        val hash = withContext(Dispatchers.Default) { PasswordHasher.hashear(password) }
        return dao.insertar(UsuarioEntity(email, hash)) != -1L
    }

    suspend fun autenticar(email: String, password: String): Boolean {
        val usuario = dao.obtenerPorEmail(email) ?: return false

        if (PasswordHasher.esHash(usuario.password)) {
            return withContext(Dispatchers.Default) {
                PasswordHasher.verificar(password, usuario.password)
            }
        }

        // Usuario antiguo con la contraseña en texto plano:
        // se valida y, si es correcta, se convierte a hash para que no vuelva a quedar visible.
        if (usuario.password != password) return false
        val hash = withContext(Dispatchers.Default) { PasswordHasher.hashear(password) }
        dao.actualizarPassword(email, hash)
        return true
    }
}