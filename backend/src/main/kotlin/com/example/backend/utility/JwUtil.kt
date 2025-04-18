package com.example.backend.utility


import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.spec.SecretKeySpec
import java.nio.charset.StandardCharsets

@Component
class JwtUtil {

    private val secretKey = "your_secret_key" // Replace with a secure key

    fun generateToken(authentication: Authentication): String {
        val username = authentication.name
        val now = Date()
        val expiryDate = Date(now.time + 86400000) // 1 day

        // Convert the secret key to a SecretKeySpec
        val key = SecretKeySpec(secretKey.toByteArray(StandardCharsets.UTF_8), SignatureAlgorithm.HS512.jcaName)

        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.HS512, key)
            .compact()
    }
}
