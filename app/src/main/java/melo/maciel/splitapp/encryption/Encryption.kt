package melo.maciel.splitapp.encryption
import java.security.MessageDigest

class Encryption {
    fun sha256(input: String): String {
        // Inicializa o algoritmo SHA-256
        val digest = MessageDigest.getInstance("SHA-256")
        // Cria o hash a partir da senha convertida para bytes (UTF-8)
        val hash = digest.digest(input.toByteArray(Charsets.UTF_8))
        // Converte os bytes do hash para uma string hexadecimal
        return hash.joinToString("") { "%02x".format(it) }
    }

}