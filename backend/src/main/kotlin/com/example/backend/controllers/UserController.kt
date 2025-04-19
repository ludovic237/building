package com.example.backend.controllers

import com.example.backend.models.User
import com.example.backend.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
  private val userService: UserService
) {

  @CrossOrigin(origins = ["http://localhost:4200"])
  @GetMapping
  fun getAllUsers(): ResponseEntity<List<User>> {
    return ResponseEntity.ok(userService.getAllUsers())
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @GetMapping("/{id}")
  fun getUserById(@PathVariable id: Long): ResponseEntity<User> {
    return ResponseEntity.ok(userService.getUserById(id).orElseThrow { IllegalArgumentException("User not found") })
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PostMapping
  fun createUser(@RequestBody user: User): ResponseEntity<User> {
    return ResponseEntity.ok(userService.createUser(user))
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @PutMapping("/{id}")
  fun updateUser(@PathVariable id: Long, @RequestBody updatedUser: User): ResponseEntity<User> {
    return ResponseEntity.ok(userService.updateUser(id, updatedUser))
  }

  @CrossOrigin(origins = ["http://localhost:4200"])
  @DeleteMapping("/{id}")
  fun deleteUser(@PathVariable id: Long): ResponseEntity<Void> {
    userService.deleteUser(id)
    return ResponseEntity.noContent().build()
  }
}
