package com.metnote.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metnote.cache.AbstractStringCacheStore;
import com.metnote.config.properties.MetnoteProperties;
import com.metnote.exception.AuthenticationException;
import com.metnote.model.entity.User;
import com.metnote.model.support.MetnoteConst;
import com.metnote.security.authentication.AuthenticationImpl;
import com.metnote.security.context.SecurityContextHolder;
import com.metnote.security.context.SecurityContextImpl;
import com.metnote.security.handler.DefaultAuthenticationFailureHandler;
import com.metnote.security.service.OneTimeTokenService;
import com.metnote.security.support.UserDetail;
import com.metnote.security.util.SecurityUtils;
import com.metnote.service.OptionService;
import com.metnote.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * Admin authentication filter.
 *
 * @author johnniang
 */
@Slf4j
@Component
@Order(1)
public class AdminAuthenticationFilter extends AbstractAuthenticationFilter {

    private final MetnoteProperties metnoteProperties;

    private final UserService userService;

    public AdminAuthenticationFilter(AbstractStringCacheStore cacheStore,
                                     UserService userService,
                                     MetnoteProperties metnoteProperties,
                                     OptionService optionService,
                                     OneTimeTokenService oneTimeTokenService,
                                     ObjectMapper objectMapper) {
        super(metnoteProperties, optionService, cacheStore, oneTimeTokenService);
        this.userService = userService;
        this.metnoteProperties = metnoteProperties;

        addUrlPatterns("/api/admin/**", "/api/content/comments");

        addExcludeUrlPatterns(
                "/api/admin/login",
                "/api/admin/refresh/*",
                "/api/admin/installations",
                "/api/admin/migrations/halo",
                "/api/admin/is_installed",
                "/api/admin/password/code",
                "/api/admin/password/reset",
                "/api/admin/login/precheck"
        );

        // set failure handler
        DefaultAuthenticationFailureHandler failureHandler = new DefaultAuthenticationFailureHandler();
        failureHandler.setProductionEnv(metnoteProperties.isProductionEnv());
        failureHandler.setObjectMapper(objectMapper);

        setFailureHandler(failureHandler);

    }

    @Override
    protected void doAuthenticate(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (!metnoteProperties.isAuthEnabled()) {
            // Set security
            userService.getCurrentUser().ifPresent(user ->
                    SecurityContextHolder.setContext(new SecurityContextImpl(new AuthenticationImpl(new UserDetail(user)))));

            // Do filter
            filterChain.doFilter(request, response);
            return;
        }

        // Get token from request
        String token = getTokenFromRequest(request);

        if (StringUtils.isBlank(token)) {
            throw new AuthenticationException("未登录，请登录后访问");
        }

        // Get user id from cache
        Optional<Integer> optionalUserId = cacheStore.getAny(SecurityUtils.buildTokenAccessKey(token), Integer.class);

        if (!optionalUserId.isPresent()) {
            throw new AuthenticationException("Token 已过期或不存在").setErrorData(token);
        }

        // Get the user
        User user = userService.getById(optionalUserId.get());

        // Build user detail
        UserDetail userDetail = new UserDetail(user);

        // Set security
        SecurityContextHolder.setContext(new SecurityContextImpl(new AuthenticationImpl(userDetail)));

        // Do filter
        filterChain.doFilter(request, response);
    }

    @Override
    protected String getTokenFromRequest(@NonNull HttpServletRequest request) {
        return getTokenFromRequest(request, MetnoteConst.ADMIN_TOKEN_QUERY_NAME, MetnoteConst.ADMIN_TOKEN_HEADER_NAME);
    }

}
