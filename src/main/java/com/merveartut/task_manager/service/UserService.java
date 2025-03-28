package com.merveartut.task_manager.service;

import com.merveartut.task_manager.enums.Role;
import com.merveartut.task_manager.model.User;
import com.merveartut.task_manager.service.exception.UserNotFoundException;

import java.util.List;
import java.util.UUID;

public interface UserService {

    User createUser(User user);

    List<User> listUsers();

    User getUserById(UUID id) throws UserNotFoundException;

    List <User> getUsersByRole(Role role);

    User updateUser(UUID id, User user) throws UserNotFoundException;
    
}
