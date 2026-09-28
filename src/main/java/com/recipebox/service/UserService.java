package com.recipebox.service;

import com.recipebox.dto.UserDTO;
import com.recipebox.entity.User;
import com.recipebox.exception.BadRequestException;
import com.recipebox.exception.ResourceNotFoundException;
import com.recipebox.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDTO createUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new BadRequestException("User already exists with email: " + userDTO.getEmail());
        }
        User user = new User(userDTO.getName(), userDTO.getEmail(), userDTO.getPassword());
        User saved = userRepository.save(user);
        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return toDTO(user);
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public User getOrCreateDemoUser() {
        return userRepository.findByEmail("demo@recipebox.com")
                .orElseGet(() -> userRepository.save(new User("Demo User", "demo@recipebox.com", "password123")));
    }

    public User getUserEntity(Long id) {
        if (id == null) {
            return getOrCreateDemoUser();
        }
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private UserDTO toDTO(User user) {
        return new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getPassword());
    }
}
