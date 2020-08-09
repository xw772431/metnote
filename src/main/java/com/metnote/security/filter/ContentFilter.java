package com.metnote.security.filter;

import com.metnote.cache.AbstractStringCacheStore;
import com.metnote.config.properties.MetnoteProperties;
import com.metnote.security.handler.ContentAuthenticationFailureHandler;
import com.metnote.security.service.OneTimeTokenService;
import com.metnote.service.OptionService;
import com.metnote.utils.MetnoteUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Content filter
 *
 * @author johnniang
 * @date 19-5-6
 */
@Component
@Order(-1)
public class ContentFilter extends AbstractAuthenticationFilter {

    public ContentFilter(MetnoteProperties metnoteProperties,
                         OptionService optionService,
                         AbstractStringCacheStore cacheStore,
                         OneTimeTokenService oneTimeTokenService) {
        super(metnoteProperties, optionService, cacheStore, oneTimeTokenService);

        addUrlPatterns("/**");

        String adminPattern = MetnoteUtils.ensureBoth(metnoteProperties.getAdminPath(), "/") + "**";
        addExcludeUrlPatterns(
                adminPattern,
                "/api/**",
                "/install",
                "/version",
                "/js/**",
                "/css/**");

        // set failure handler
        setFailureHandler(new ContentAuthenticationFailureHandler());
    }

    @Override
    protected String getTokenFromRequest(HttpServletRequest request) {
        return null;
    }

    @Override
    protected void doAuthenticate(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Do nothing
        filterChain.doFilter(request, response);
    }
}
