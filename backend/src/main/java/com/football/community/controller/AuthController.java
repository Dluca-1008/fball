package com.football.community.controller;

import com.football.community.dto.LoginDto;
import com.football.community.dto.RegisterDto;
import com.football.community.dto.ChangePasswordDto;
import com.football.community.dto.Result;
import com.football.community.dto.UpdateUserInfoDto;
import com.football.community.entity.User;
import com.football.community.security.CustomUserDetails;
import com.football.community.security.JwtTokenProvider;
import com.football.community.security.TokenSessionService;
import com.football.community.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "Auth认证", description = "注册、登录、权限管理接口")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private TokenSessionService tokenSessionService;

    @Operation(summary = "用户注册", description = "使用用户名和密码注册新账号")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "注册成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/register")
    public Result<User> register(@Valid @RequestBody @Parameter(description = "注册信息") RegisterDto dto) {
        User user = userService.register(dto);
        return Result.success(user);
    }

    @Operation(summary = "用户登录", description = "使用用户名和密码登录，返回JWT Token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "登录成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "用户名或密码错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody @Parameter(description = "登录信息") LoginDto dto) {
        User user = userService.login(dto);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);

        return Result.success(data);
    }

    @Operation(summary = "获取当前用户权限", description = "获取当前登录用户的权限列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/permissions")
    public Result<?> getPermissions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        return Result.success(user.getPermissions());
    }

    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/info")
    public Result<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        user.setPassword(null);
        return Result.success(user);
    }

    @Operation(summary = "更新当前用户资料", description = "更新当前登录用户的个人资料（昵称、邮箱、手机号、性别）")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/info")
    public Result<User> updateUserInfo(@RequestBody UpdateUserInfoDto dto,
                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userService.updateUserInfo(userDetails.getId(), dto);
        user.setPassword(null);
        return Result.success(user);
    }

    @Operation(summary = "修改密码", description = "修改当前登录用户的密码")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "修改成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/change-password")
    public Result<?> changePassword(@Valid @RequestBody @Parameter(description = "密码修改信息") ChangePasswordDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        userService.changePassword(username, dto);
        tokenSessionService.revokeUserTokens(user.getId());
        return Result.success();
    }
}
