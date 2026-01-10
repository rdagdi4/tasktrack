package com.rdagdi.tasktrack.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import com.rdagdi.tasktrack.service.UserService;
import com.rdagdi.tasktrack.entity.User;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserService userService;
 
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public User createUser(User user) {
        return userService.createUser(user);
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public User getUserById(Long Id) {
        return userService.getUserById(Id);
    }

    @PutMapping("/users/{id}")
    public User updateUser(Long Id) {
        return userService.updateUser(Id);
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(Long id) {
        userService.deleteUser(id);
    }

}
