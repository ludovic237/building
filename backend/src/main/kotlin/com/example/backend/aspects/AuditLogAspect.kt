package com.example.backend.aspects

import com.example.backend.models.AuditLog
import com.example.backend.repositories.AuditLogRepository
import com.example.backend.services.AuditLogService
import com.example.backend.utility.UserUtils
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.*
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
@Aspect
class AuditLogAspect(
  private val auditLogService: AuditLogService,
  private val auditLogRepository: AuditLogRepository,
  private val userUtils: UserUtils
) {

  /*  @Before("execution(* com.example.backend.services.*.*(..))")
    fun logBefore(joinPoint: JoinPoint) {
      val methodName = joinPoint.signature.name
      val args = joinPoint.args.joinToString()
      val userActionLog = AuditLog()
      userActionLog.userId = userUtils.getCurrentUserId()
      userActionLog.action = "BEFORE"
      userActionLog.methodName = methodName
      userActionLog.arguments = args

      auditLogService.saveLog(userActionLog)
    }*/

//  @Before("execution(* com.example.backend.services.*.*(..))")
//  fun logBefore(joinPoint: JoinPoint) {
//      try {
//          val methodName = joinPoint.signature.name
//          val args = joinPoint.args.joinToString()
//          val userId = try {
//              userUtils.getCurrentUserId()
//          } catch (e: Exception) {
//              null // Ignorer si l'utilisateur n'est pas authentifié
//          }
//
//          val userActionLog = AuditLog().apply {
//              this.userId = userId
//              this.action = "BEFORE"
//              this.methodName = methodName
//              this.arguments = args
//          }
//
//          auditLogService.saveLog(userActionLog)
//      } catch (e: Exception) {
//          println("Erreur dans logBefore: ${e.message}")
//      }
//  }

  @Pointcut("execution(* com.example.backend.services.*.*(..))")
  fun serviceMethods() {
  }

  @Pointcut(" !execution(* com.example.backend.services.AuditLogService.*(..)) && !execution(* com.example.backend.controllers.AuthController.*(..)) && !execution(* com.example.backend.services.CustomUserDetailsService.*(..))")
  fun excludeAuth() {
  }

  @Before("serviceMethods() && excludeAuth()")
  fun logBefore(joinPoint: JoinPoint) {
    val methodName = joinPoint.signature.name
    val arguments = joinPoint.args.map { it?.toString() ?: "null" }.joinToString(",")
    val userActionLog = AuditLog()
    userActionLog.userId = userUtils.getCurrentUserId()
    userActionLog.action = "BEFORE"
    userActionLog.methodName = methodName
    userActionLog.arguments = arguments
    println("Captured arguments: ${joinPoint.args.map { it?.toString() ?: "null" }}")
    auditLogService.saveLog(userActionLog)
  }

  @AfterReturning(value = "serviceMethods() && excludeAuth()", returning = "result")
  fun logAfterReturning(joinPoint: JoinPoint, result: Any?) {
    val methodName = joinPoint.signature.name
    val userActionLog = AuditLog()
    userActionLog.userId = userUtils.getCurrentUserId()
    userActionLog.action = "AFTER_RETURNING"
    userActionLog.methodName = methodName
    userActionLog.result = result?.toString()
    auditLogService.saveLog(userActionLog)
  }

  @AfterThrowing(value = "serviceMethods() && excludeAuth()", throwing = "exception")
  fun logAfterThrowing(joinPoint: JoinPoint, exception: Throwable) {
    val methodName = joinPoint.signature.name
    val userActionLog = AuditLog()
    userActionLog.userId = userUtils.getCurrentUserId()
    userActionLog.action = "AFTER_THROWING"
    userActionLog.methodName = methodName
    userActionLog.exception = exception.message

    auditLogService.saveLog(userActionLog)
  }

}
