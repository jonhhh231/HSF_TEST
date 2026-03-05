package com.group4.ecommerceplatform.controllers.api;

import com.group4.ecommerceplatform.entities.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserApiController {

    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("user");

        Map<String, Object> response = new HashMap<>();

        if (user != null) {
            response.put("id", user.getId());
            response.put("fullName", user.getFullName());
            response.put("email", user.getEmail());
            response.put("role", user.getRole());
        } else {
            response.put("id", null);
            response.put("fullName", "Guest");
        }

        return ResponseEntity.ok(response);
    }
}
