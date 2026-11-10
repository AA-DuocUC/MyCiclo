package com.example.myciclo.utils

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Hash de contraseñas con PBKDF2 + sal aleatoria.
 * Se guarda como texto: pbkdf2:iteraciones:sal:hash
 * No se puede revertir: solo se puede comprobar si una contraseña coincide.
 */
object PasswordHasher {

    private const val PREFIJO = "pbkdf2"
    private const val SEPARADOR = ":"
    private const val ALGORITMO = "PBKDF2WithHmacSHA1" // disponible desde Android 7 (API 24)
    private const val ITERACIONES = 60_000
    private const val LARGO_HASH_BITS = 256
    private const val LARGO_SAL_BYTES = 16

    /** Indica si el texto guardado ya es un hash (y no una contraseña en texto plano). */
    fun esHash(almacenado: String): Boolean = almacenado.startsWith(PREFIJO + SEPARADOR)

    fun hashear(password: String): String {
        val sal = ByteArray(LARGO_SAL_BYTES).also { SecureRandom().nextBytes(it) }
        val hash = derivar(password, sal, ITERACIONES)
        return listOf(PREFIJO, ITERACIONES.toString(), aBase64(sal), aBase64(hash))
            .joinToString(SEPARADOR)
    }

    fun verificar(password: String, almacenado: String): Boolean {
        val partes = almacenado.split(SEPARADOR)
        if (partes.size != 4 || partes[0] != PREFIJO) return false

        val iteraciones = partes[1].toIntOrNull() ?: return false
        val sal = Base64.decode(partes[2], Base64.NO_WRAP)
        val esperado = Base64.decode(partes[3], Base64.NO_WRAP)
        val calculado = derivar(password, sal, iteraciones)

        // Comparación en tiempo constante
        return MessageDigest.isEqual(esperado, calculado)
    }

    private fun derivar(password: String, sal: ByteArray, iteraciones: Int): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), sal, iteraciones, LARGO_HASH_BITS)
        return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(spec).encoded
    }

    private fun aBase64(bytes: ByteArray): String = Base64.encodeToString(bytes, Base64.NO_WRAP)
}