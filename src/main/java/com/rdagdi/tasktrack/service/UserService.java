package com.rdagdi.tasktrack.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rdagdi.tasktrack.repository.UserRepository;
import com.rdagdi.tasktrack.entity.User;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Create User
    public User createUser(User user) {
        return userRepository.save(user);
    }

    // Get User by ID
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    // Get All Users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Update User
    public User updateUser(Long Id) {
        System.out.println("User updated successfully");
        return userRepository.findById(Id).orElse(null);
    }

    // Delete User
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

}

// Think about: Should you return the entity directly or create DTOs?
