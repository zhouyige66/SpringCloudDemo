package cn.roy.springcloud.gateway.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
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

    //切点，内部为切入点表达式
//    @Pointcut("execution(public * org.springframework.data.redis.cache.RedisCache.put(..))")
//    @Pointcut("execution(public * cn.roy.springcloud.gateway.controller.CacheTestController.test(..))")
    @Pointcut("execution(public * org.springframework.data.redis.cache.RedisCacheManager.getCache(..))")
    private void pointCutMethod() {
    }

    //声明前置通知
    @Before("pointCutMethod()")
    public void doBefore(JoinPoint point) {
        System.out.println("doBefore");
        return;
    }

    //声明后置通知
    @AfterReturning(pointcut = "pointCutMethod()", returning = "returnValue")
    public void doAfterReturning(JoinPoint point, Object returnValue) {
        System.out.println("doAfterReturning");
    }

    //声明例外通知
//    @AfterThrowing(pointcut = "pointCutMethod()", throwing = "e")
//    public void doAfterThrowing(Exception e) {
//        System.out.println("doAfterThrowing");
//    }

    //声明最终通知
//    @After("pointCutMethod()")
//    public void doAfter() {
//        System.out.println("doAfter");
//    }

    //声明环绕通知
//    @Around("pointCutMethod()")
//    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
//        Object obj = pjp.proceed();
//
//        System.out.println("doAround");
//        return obj;
//    }

}



