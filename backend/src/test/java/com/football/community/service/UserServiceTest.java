package com.football.community.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.football.community.dto.LoginDto;
import com.football.community.dto.RegisterDto;
import com.football.community.entity.User;
import com.football.community.exception.BusinessException;
import com.football.community.repository.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private RegisterDto registerDto;

    @BeforeEach
    void setUp() {
        registerDto = new RegisterDto();
        registerDto.setUsername("testuser");
        registerDto.setPassword("test123456");
        registerDto.setEmail("test@example.com");
        registerDto.setNickname("测试用户");
    }

    @Test
    void testRegister() {
        User user = userService.register(registerDto);

        assertNotNull(user);
        assertNotNull(user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("测试用户", user.getNickname());
        assertTrue(passwordEncoder.matches("test123456", user.getPassword()));
    }

    @Test
    void testRegisterDuplicateUsername() {
        userService.register(registerDto);

        assertThrows(BusinessException.class, () -> {
            userService.register(registerDto);
        });
    }

    @Test
    void testFindByUsername() {
        userService.register(registerDto);

        User user = userService.findByUsername("testuser");

        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
    }

    @Test
    void testFindByUsernameNotFound() {
        User user = userService.findByUsername("nonexistent");

        assertNull(user);
    }

    @Test
    void testLogin() {
        userService.register(registerDto);

        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("testuser");
        loginDto.setPassword("test123456");

        User user = userService.login(loginDto);

        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
    }

    @Test
    void testLoginWrongPassword() {
        userService.register(registerDto);

        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("testuser");
        loginDto.setPassword("wrongpassword");

        assertThrows(BusinessException.class, () -> {
            userService.login(loginDto);
        });
    }

    @Test
    void testGetUserList() {
        userService.register(registerDto);

        IPage<User> users = userService.getUserList(1, 10, null);

        assertNotNull(users);
        assertTrue(users.getRecords().size() > 0);
    }

    @Test
    void testUpdateUserStatus() {
        User user = userService.register(registerDto);

        User updated = userService.updateUserStatus(user.getId(), 0);

        assertEquals(0, updated.getStatus());
    }
}
