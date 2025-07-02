package com.saunderscox.taskolotl.config.database;

import com.saunderscox.taskolotl.entity.BaseEntity;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class RepositoryLoggingAspect {

  @Pointcut("execution(* org.springframework.data.repository.CrudRepository.save(..))")
  public void saveOperation() {
  }

  @Pointcut("execution(* org.springframework.data.repository.CrudRepository.delete*(..))")
  public void deleteOperation() {
  }

  @AfterReturning(pointcut = "saveOperation()", returning = "result")
  public void logAfterSave(JoinPoint joinPoint, Object result) {
    if (result instanceof BaseEntity entity) {
      String entityName = entity.getClass().getSimpleName();
      log.info("{} saved successfully with ID: {}", entityName, entity.getId());
    }
  }

  @AfterReturning(pointcut = "deleteOperation()")
  public void logAfterDelete(JoinPoint joinPoint) {
    String repositoryName = joinPoint.getSignature().getDeclaringType().getSimpleName();
    log.info("Entity deleted successfully from {}", repositoryName);
  }
}
