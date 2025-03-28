package com.merveartut.task_manager.service;

import com.merveartut.task_manager.enums.Role;
import com.merveartut.task_manager.model.User;
import com.merveartut.task_manager.repository.UserRepository;
import com.merveartut.task_manager.service.exception.UserNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public List<User> listUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(UUID id) throws UserNotFoundException{
      return userRepository.findById(id)
               .orElseThrow(() -> new UserNotFoundException());
    }

    @Override
    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    @Override
    public User updateUser(UUID id, User user) throws UserNotFoundException{
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException();
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

}
