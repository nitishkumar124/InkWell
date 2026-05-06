package com.inkwell.auth.controller;

import com.inkwell.auth.common.ApiResponse;
import com.inkwell.auth.entity.Role;
import com.inkwell.auth.entity.User;
import com.inkwell.auth.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    // GET ALL USERS
    @GetMapping("/users")
    public ApiResponse<List<User>> getAllUsers() {

        List<User> users = userRepository.findAll();

        return ApiResponse.<List<User>>builder()
                .success(true)
                .message("Users fetched successfully")
                .data(users)
                .build();
    }

    // DELETE USER
    @DeleteMapping("/users/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {

        userRepository.deleteById(id);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("User deleted successfully")
                .data(null)
                .build();
    }

    // UPDATE ROLE
    @PutMapping("/users/{id}/role")
    public ApiResponse<User> updateRole(@PathVariable Long id,
                                        @RequestParam Role role) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRole(role);
        User updated = userRepository.save(user);

        return ApiResponse.<User>builder()
                .success(true)
                .message("Role updated successfully")
                .data(updated)
                .build();
    }
}