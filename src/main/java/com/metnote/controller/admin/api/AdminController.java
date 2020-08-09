package com.metnote.controller.admin.api;

import com.metnote.annotation.DisableOnCondition;
import com.metnote.cache.lock.CacheLock;
import com.metnote.exception.BadRequestException;
import com.metnote.model.dto.EnvironmentDTO;
import com.metnote.model.dto.LoginPreCheckDTO;
import com.metnote.model.dto.StatisticDTO;
import com.metnote.model.entity.User;
import com.metnote.model.enums.MFAType;
import com.metnote.model.params.LoginParam;
import com.metnote.model.params.ResetPasswordParam;
import com.metnote.model.properties.PrimaryProperties;
import com.metnote.model.support.BaseResponse;
import com.metnote.security.token.AuthToken;
import com.metnote.service.AdminService;
import com.metnote.service.OptionService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Admin controller.
 *
 * @author johnniang
 * @author ryanwang
 * @date 2019-03-19
 */
@Slf4j
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    private final OptionService optionService;

    public AdminController(AdminService adminService, OptionService optionService) {
        this.adminService = adminService;
        this.optionService = optionService;
    }

    @GetMapping(value = "/is_installed")
    @ApiOperation("Checks Installation status")
    public boolean isInstall() {
        return optionService.getByPropertyOrDefault(PrimaryProperties.IS_INSTALLED, Boolean.class, false);
    }

    @PostMapping("login/precheck")
    @ApiOperation("Login")
    @CacheLock(autoDelete = false, prefix = "login_precheck")
    public LoginPreCheckDTO authPreCheck(@RequestBody @Valid LoginParam loginParam) {
        final User user = adminService.authenticate(loginParam);
        return new LoginPreCheckDTO(MFAType.useMFA(user.getMfaType()));
    }

    @PostMapping("login")
    @ApiOperation("Login")
    @CacheLock(autoDelete = false, prefix = "login_auth")
    public AuthToken auth(@RequestBody @Valid LoginParam loginParam) {
        return adminService.authCodeCheck(loginParam);
    }

    @PostMapping("logout")
    @ApiOperation("Logs out (Clear session)")
    @CacheLock(autoDelete = false)
    public void logout() {
        adminService.clearToken();
    }

    @PostMapping("password/code")
    @ApiOperation("Sends reset password verify code")
    @CacheLock(autoDelete = false)
    @DisableOnCondition
    public void sendResetCode(@RequestBody @Valid ResetPasswordParam param) {
        adminService.sendResetPasswordCode(param);
    }

    @PutMapping("password/reset")
    @ApiOperation("Resets password by verify code")
    @CacheLock(autoDelete = false)
    @DisableOnCondition
    public void resetPassword(@RequestBody @Valid ResetPasswordParam param) {
        adminService.resetPasswordByCode(param);
    }

    @PostMapping("refresh/{refreshToken}")
    @ApiOperation("Refreshes token")
    @CacheLock(autoDelete = false)
    public AuthToken refresh(@PathVariable("refreshToken") String refreshToken) {
        return adminService.refreshToken(refreshToken);
    }

    @GetMapping("counts")
    @ApiOperation("Gets count info")
    @Deprecated
    public StatisticDTO getCount() {
        return adminService.getCount();
    }

    @GetMapping("environments")
    @ApiOperation("Gets environments info")
    public EnvironmentDTO getEnvironments() {
        return adminService.getEnvironments();
    }

    @PutMapping("halo-admin")
    @ApiOperation("Updates halo-admin manually")
    @Deprecated
    public void updateAdmin() {
        adminService.updateAdminAssets();
    }

    @GetMapping("spring/application.yaml")
    @ApiOperation("Gets application config content")
    @DisableOnCondition
    public BaseResponse<String> getSpringApplicationConfig() {
        return BaseResponse.ok(HttpStatus.OK.getReasonPhrase(), adminService.getApplicationConfig());
    }

    @PutMapping("spring/application.yaml")
    @ApiOperation("Updates application config content")
    @DisableOnCondition
    public void updateSpringApplicationConfig(@RequestParam(name = "content") String content) {
        adminService.updateApplicationConfig(content);
    }

    @PostMapping(value = {"halo/restart", "spring/restart"})
    @ApiOperation("Restarts halo server")
    @DisableOnCondition
    @Deprecated
    public void restartApplication() {
        throw new BadRequestException("此前的重启方案存在性能问题，故暂不支持重启功能！");
    }

    @GetMapping(value = "halo/logfile")
    @ApiOperation("Gets halo log file content")
    @DisableOnCondition
    public BaseResponse<String> getLogFiles(@RequestParam("lines") Long lines) {
        return BaseResponse.ok(HttpStatus.OK.getReasonPhrase(), adminService.getLogFiles(lines));
    }
}
