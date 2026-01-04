package com.rdagdi.tasktrack.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import com.rdagdi.tasktrack.service.UserService;
import com.rdagdi.tasktrack.entity.User;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public User createUser(User user) {
        return userService.createUser(user);
    }

    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/id")
    public User getUserById(Long Id) {
        return userService.getUserById(Id);
    }

    @PostMapping("/update")
    public User updateUser(User user) {
        return userService.updateUser(user);
    }

    @PostMapping("/delete")
    public void deleteUser(Long id) {
        userService.deleteUser(id);
    }

}
