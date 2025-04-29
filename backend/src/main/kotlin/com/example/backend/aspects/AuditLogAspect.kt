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

  @Pointcut(" !execution(* com.example.backend.controllers.AuthController.*(..))")
  fun excludeAuth() {
  }

  @Before("serviceMethods() && excludeAuth()")
  fun logBefore(joinPoint: JoinPoint) {
    try {
      val methodName = joinPoint.signature.name
      val args = joinPoint.args?.joinToString() ?: ""

      // Vérifiez si l'utilisateur est authentifié
      val userId = try {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication != null && authentication.isAuthenticated && authentication.principal != "anonymousUser") {
          userUtils.getCurrentUserId()?.toLong()
        } else {
          null // Utilisateur non authentifié
        }
      } catch (e: Exception) {
        null // En cas d'erreur, considérer l'utilisateur comme non authentifié
      }

      // Créez et enregistrez le log
      val userActionLog = AuditLog().apply {
        this.userId = userId
        this.action = "BEFORE"
        this.methodName = methodName
        this.arguments = args
      }
      auditLogRepository.save(userActionLog)
    } catch (e: Exception) {
      println("Error in logBefore: ${e.message}")
    }
  }

  @AfterReturning(value = "serviceMethods() && excludeAuth()", returning = "result")
  fun logAfterReturning(joinPoint: JoinPoint, result: Any?) {
    val methodName = joinPoint.signature.name
    val userId = try {
      val authentication = SecurityContextHolder.getContext().authentication
      if (authentication != null && authentication.isAuthenticated && authentication.principal != "anonymousUser") {
        userUtils.getCurrentUserId()?.toLong()
      } else {
        null // Utilisateur non authentifié
      }
    } catch (e: Exception) {
      null // En cas d'erreur, considérer l'utilisateur comme non authentifié
    }
    val userActionLog = AuditLog().apply {
      this.userId = userId
      this.action = "AFTER_RETURNING"
      this.methodName = methodName
      this.result = result?.toString()
    }
    auditLogRepository.save(userActionLog)
  }

  @AfterThrowing(value = "serviceMethods() && excludeAuth()", throwing = "exception")
  fun logAfterThrowing(joinPoint: JoinPoint, exception: Throwable) {
    val methodName = joinPoint.signature.name
    val userId = try {
      val authentication = SecurityContextHolder.getContext().authentication
      if (authentication != null && authentication.isAuthenticated && authentication.principal != "anonymousUser") {
        userUtils.getCurrentUserId()?.toLong()
      } else {
        null // Utilisateur non authentifié
      }
    } catch (e: Exception) {
      null // En cas d'erreur, considérer l'utilisateur comme non authentifié
    }
    val userActionLog = AuditLog().apply {
      this.userId = userId
      this.action = "AFTER_THROWING"
      this.methodName = methodName
      this.exception = exception.message
    }
    auditLogRepository.save(userActionLog)
  }

}
