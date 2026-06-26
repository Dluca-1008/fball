package com.football.community.controller;

import com.football.community.dto.LoginDto;
import com.football.community.dto.RegisterDto;
import com.football.community.dto.ChangePasswordDto;
import com.football.community.dto.Result;
import com.football.community.entity.User;
import com.football.community.security.JwtTokenProvider;
import com.football.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public Result<User> register(@Valid @RequestBody RegisterDto dto) {
        User user = userService.register(dto);
        return Result.success(user);
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDto dto) {
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

    @GetMapping("/permissions")
    public Result<?> getPermissions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        return Result.success(user.getPermissions());
    }

    @GetMapping("/info")
    public Result<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        user.setPassword(null);
        return Result.success(user);
    }

    @PostMapping("/change-password")
    public Result<?> changePassword(@Valid @RequestBody ChangePasswordDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        userService.changePassword(username, dto);
        return Result.success();
    }
}
