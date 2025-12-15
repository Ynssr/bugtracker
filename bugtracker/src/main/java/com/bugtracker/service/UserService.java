package com.bugtracker.service;

import com.bugtracker.entity.User;
import com.bugtracker.enums.UserRole;
import com.bugtracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public User createUser(User user, Long requesterId) {

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("İşlemi yapan kullanıcı bulunamadı!"));

        if (requester.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("Bu işlemi yapmaya yetkiniz yok! Sadece YÖNETİCİ yeni kullanıcı ekleyebilir.");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Bu kullanıcı adı zaten kullanılıyor!");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Bu email zaten kullanılıyor!");
        }

        return userRepository.save(user);
    }

    // OKuma
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getActiveUsers() {
        return userRepository.findByIsActiveTrue();
    }

    public List<User> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }

    // GÜncelleme
    public User updateUser(Long id, User updatedUser) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Kullanıcı bulunamadı!"));

        // Email değişiyorsa kontrol et
        if (!existingUser.getEmail().equals(updatedUser.getEmail())) {
            if (userRepository.existsByEmail(updatedUser.getEmail())) {
                throw new IllegalArgumentException("Bu email zaten kullanılıyor!");
            }
            existingUser.setEmail(updatedUser.getEmail());
        }

        // Güncellenebilir alanlar
        existingUser.setFullName(updatedUser.getFullName());
        existingUser.setPassword(updatedUser.getPassword());

        return userRepository.save(existingUser);
    }

    // Silme
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("Kullanıcı bulunamadı!");
        }
        userRepository.deleteById(id);
    }

    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Kullanıcı bulunamadı!"));

        user.setActive(false);
        userRepository.save(user);
    }

    // Arama
    public List<User> searchUsersByName(String name) {
        return userRepository.findByFullNameContainingIgnoreCase(name);
    }
}