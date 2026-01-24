package com.group4.ecommerceplatform.services.auth.impl;

import com.group4.ecommerceplatform.entities.User;
import com.group4.ecommerceplatform.exceptions.NotFoundException;
import com.group4.ecommerceplatform.repositories.UserRepository;
import com.group4.ecommerceplatform.services.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.regex.Pattern;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    @Override
    public User login(String email, String password) {
        // Validate input
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("Email hoặc mật khẩu không đúng"));

        if (!user.getIsActive()) {
            throw new RuntimeException("Tài khoản đã bị vô hiệu hóa");
        }

        // Simple password check (you should use BCrypt in production)
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Email hoặc mật khẩu không đúng");
        }

        return user;
    }

    @Override
    public User register(User user) {
        // Validate user data
        validateUserForRegistration(user);

        // Check if email already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email đã được sử dụng");
        }

        // Set default values
        if (user.getIsActive() == null) {
            user.setIsActive(true);
        }
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            user.setRole("CUSTOMER");
        }

        // In production, hash the password using BCrypt
        // user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống");
        }
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));
    }

    /**
     * Validate user data for registration
     */
    private void validateUserForRegistration(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Thông tin người dùng không được để trống");
        }

        // Validate full name
        if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Họ tên không được để trống");
        }
        if (user.getFullName().trim().length() < 2) {
            throw new IllegalArgumentException("Họ tên phải có ít nhất 2 ký tự");
        }
        if (user.getFullName().length() > 255) {
            throw new IllegalArgumentException("Họ tên không được vượt quá 255 ký tự");
        }

        // Validate email
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống");
        }
        if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            throw new IllegalArgumentException("Email không hợp lệ");
        }
        if (user.getEmail().length() > 255) {
            throw new IllegalArgumentException("Email không được vượt quá 255 ký tự");
        }

        // Validate password
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }
        if (user.getPassword().length() < 6) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 6 ký tự");
        }
        if (user.getPassword().length() > 100) {
            throw new IllegalArgumentException("Mật khẩu không được vượt quá 100 ký tự");
        }

        // Additional password strength validation
        if (!user.getPassword().matches(".*[A-Za-z].*")) {
            throw new IllegalArgumentException("Mật khẩu phải chứa ít nhất một chữ cái");
        }
    }
}

