package com.group4.ecommerceplatform.services.auth;

import com.group4.ecommerceplatform.entities.User;

public interface AuthService {
    User login(String email, String password);
    User register(User user);
    User findByEmail(String email);
}
