package com.metnote.aspect;

import com.metnote.annotation.DisableOnCondition;
import com.metnote.config.properties.MetnoteProperties;
import com.metnote.exception.ForbiddenException;
import com.metnote.model.enums.Mode;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 自定义注解DisableApi的切面
 *
 * @author guqing
 * @date 2020-02-14 14:08
 */
@Aspect
@Slf4j
@Component
public class DisableOnConditionAspect {

    private final MetnoteProperties metnoteProperties;

    public DisableOnConditionAspect(MetnoteProperties metnoteProperties) {
        this.metnoteProperties = metnoteProperties;
    }

    @Pointcut("@annotation(com.metnote.annotation.DisableOnCondition)")
    public void pointcut() {
    }

    @Around("pointcut() && @annotation(disableApi)")
    public Object around(ProceedingJoinPoint joinPoint,
                         DisableOnCondition disableApi) throws Throwable {
        Mode mode = disableApi.mode();
        if (metnoteProperties.getMode().equals(mode)) {
            throw new ForbiddenException("禁止访问");
        }

        return joinPoint.proceed();
    }
}
