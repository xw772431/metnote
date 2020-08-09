package com.metnote.controller.content;

import com.metnote.config.properties.MetnoteProperties;
import com.metnote.exception.ServiceException;
import com.metnote.model.entity.User;
import com.metnote.model.properties.BlogProperties;
import com.metnote.model.support.MetnoteConst;
import com.metnote.service.OptionService;
import com.metnote.service.UserService;
import com.metnote.utils.MetnoteUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Main controller.
 *
 * @author ryanwang
 * @date 2019-04-23
 */
@Controller
public class MainController {

    /**
     * Index redirect uri.
     */
    private final static String INDEX_REDIRECT_URI = "index.html";

    /**
     * Install redirect uri.
     */
    private final static String INSTALL_REDIRECT_URI = INDEX_REDIRECT_URI + "#install";

    private final UserService userService;

    private final OptionService optionService;

    private final MetnoteProperties metnoteProperties;

    public MainController(UserService userService, OptionService optionService, MetnoteProperties metnoteProperties) {
        this.userService = userService;
        this.optionService = optionService;
        this.metnoteProperties = metnoteProperties;
    }

    @GetMapping("${halo.admin-path:admin}")
    public void admin(HttpServletResponse response) throws IOException {
        String adminIndexRedirectUri = MetnoteUtils.ensureBoth(metnoteProperties.getAdminPath(), MetnoteUtils.URL_SEPARATOR) + INDEX_REDIRECT_URI;
        response.sendRedirect(adminIndexRedirectUri);
    }

    @GetMapping("version")
    @ResponseBody
    public String version() {
        return MetnoteConst.METNOTE_VERSION;
    }

    @GetMapping("install")
    public void installation(HttpServletResponse response) throws IOException {
        String installRedirectUri = StringUtils.appendIfMissing(this.metnoteProperties.getAdminPath(), "/") + INSTALL_REDIRECT_URI;
        response.sendRedirect(installRedirectUri);
    }

    @GetMapping("avatar")
    public void avatar(HttpServletResponse response) throws IOException {
        User user = userService.getCurrentUser().orElseThrow(() -> new ServiceException("未查询到博主信息"));
        if (StringUtils.isNotEmpty(user.getAvatar())) {
            response.sendRedirect(MetnoteUtils.normalizeUrl(user.getAvatar()));
        }
    }

    @GetMapping("logo")
    public void logo(HttpServletResponse response) throws IOException {
        String blogLogo = optionService.getByProperty(BlogProperties.BLOG_LOGO).orElse("").toString();
        if (StringUtils.isNotEmpty(blogLogo)) {
            response.sendRedirect(MetnoteUtils.normalizeUrl(blogLogo));
        }
    }

    @GetMapping("favicon.ico")
    public void favicon(HttpServletResponse response) throws IOException {
        String favicon = optionService.getByProperty(BlogProperties.BLOG_FAVICON).orElse("").toString();
        if (StringUtils.isNotEmpty(favicon)) {
            response.sendRedirect(MetnoteUtils.normalizeUrl(favicon));
        }
    }
}
