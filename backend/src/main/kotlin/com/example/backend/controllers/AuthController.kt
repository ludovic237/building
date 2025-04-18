package com.example.backend.controllers

import com.example.backend.models.User
import com.example.backend.repositories.UserRepository
import com.example.backend.utility.JwtUtil
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/auth")
class AuthController(
  private var authenticationManager: AuthenticationManager? = null,
  private var jwtUtil: JwtUtil,
  var userRepository: UserRepository,
  private val passwordEncoder: PasswordEncoder // Injected here
) {

  fun AuthController(authenticationManager: AuthenticationManager) {
    this.authenticationManager = authenticationManager
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PostMapping("/login")
  fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<*> {
    println("loginRequest")
    println(loginRequest)
    val authentication: Authentication = authenticationManager!!.authenticate(
      UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password)
    )
    SecurityContextHolder.getContext().authentication = authentication

    // Générer le token JWT
    val token = jwtUtil.generateToken(authentication)

//    return ResponseEntity.ok(mapOf("message" to "Login successful"))
    return ResponseEntity.ok(mapOf(
      "message" to "Login successful",
      "token" to token
    ))
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PostMapping("/logout")
  fun logout(): ResponseEntity<*> {
    SecurityContextHolder.clearContext()
    return ResponseEntity.ok("Logout successful")
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PostMapping("/register")
  fun register(@RequestBody registerRequest: RegisterRequest): ResponseEntity<*> {
    // Check if the username already exists
    if (userRepository.existsByEmail(registerRequest.email)) {
      return ResponseEntity.badRequest().body("Username is already taken")
    }

    // Create a new user
    val user = User().apply{
      firstName =  registerRequest.firstName
      lastName =  registerRequest.lastName
      phone =  registerRequest.phone
      email = registerRequest.email
      username = registerRequest.email
      password = passwordEncoder.encode(registerRequest.password)
      role = registerRequest.role // Initialize and set a default role
  }

    // Save the user
    userRepository.save(user)

    return ResponseEntity.ok(mapOf("message" to "User registered successfully"))
  }
}
data class LoginRequest(
  val username: String,
  val password: String
)

data class RegisterRequest(
  val firstName: String,
  val lastName: String,
  val role: String,
  val phone: String,
  val email: String,
  val password: String,
)

