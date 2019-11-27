package cn.roy.springcloud.cache.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019/11/1 11:14
 * @Version: v1.0
 */
@Aspect
@Component
public class CacheAspect {
    private static final Logger logger = LoggerFactory.getLogger(CacheAspect.class);

    //切点，内部为切入点表达式
    @Pointcut("execution(public * cn.roy.springcloud.cache.controller.*.*(..))")
    private void pointCutMethod() {
    }

    //声明前置通知
    @Before("pointCutMethod()")
    public void doBefore(JoinPoint point) {
        logger.info("doBefore");
        return;
    }

    //声明最终通知
    @After("pointCutMethod()")
    public void doAfter(JoinPoint point) {
        logger.info("doAfter");
    }

    //声明后置通知
    @AfterReturning(pointcut = "pointCutMethod()", returning = "returnValue")
    public void doAfterReturning(JoinPoint point, Object returnValue) {
        logger.info("doAfterReturning");
    }

    //声明例外通知
    @AfterThrowing(pointcut = "pointCutMethod()", throwing = "e")
    public void doAfterThrowing(Exception e) {
        logger.info("doAfterThrowing");
    }

    //声明环绕通知
    @Around("pointCutMethod()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        logger.info("doAround");
        Object obj = pjp.proceed();
        return obj;
    }

}



