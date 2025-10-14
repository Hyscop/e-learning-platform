package com.elearning.user.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.elearning.user.exception.EmailAlreadyExistsException;
import com.elearning.user.exception.UserNotFoundException;
import com.elearning.user.model.Role;
import com.elearning.user.model.User;
import com.elearning.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService {

  /**
   * Dependicies
   */

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  /**
   * Creat new user
   * 
   * @param user
   * @return
   * @throws EmailAlreadyExistsException if email already exists
   */
  public User createUser(User user) {

    log.info("Creating new user with email: {}", user.getEmail());

    if (userRepository.existsByEmail(user.getEmail())) {
      log.error("Email already exists: {}", user.getEmail());
      throw new EmailAlreadyExistsException(user.getEmail());
    }

    String encodedPassword = passwordEncoder.encode(user.getPassword());
    user.setPassword(encodedPassword);

    User savedUser = userRepository.save(user);
    log.info("User created successfully with ID-Name: {}{}", savedUser.getId(), savedUser.getFirstName());

    return savedUser;
  }

  /**
   * Register new User - for public registration endpoint
   * 
   * @param user
   * @return registered user
   */
  public User registerUser(User user) {
    log.info("Registered new student: {}", user.getEmail());

    user.setRole(Role.STUDENT);
    user.setEnabled(true);

    return createUser(user);
  }

  /**
   * Get user by ID
   * 
   * @param id
   * @return Optional<User> - might exists or not
   */
  @Transactional(readOnly = true)
  public Optional<User> getUserById(Long id) {
    log.debug("Fetching user by ID: {}", id);
    return userRepository.findById(id);
  }

  /**
   * Get user by email - for login
   * 
   * @param email
   * @return Optional<User>
   */
  @Transactional(readOnly = true)
  public Optional<User> getUserByEmail(String email) {
    log.debug("Fetching user by email");
    return userRepository.findByEmail(email);
  }

  /**
   * Get all users
   * 
   * @return list of all users
   * 
   *         TODO: In production will be used pagination!
   */
  @Transactional(readOnly = true)
  public List<User> getAllUsers() {
    log.debug("Fetching all users");
    return userRepository.findAll();
  }

  /**
   * Get users by role:
   * 
   * TODO: pagination!
   * 
   * @param role
   * @return list of users with role
   */
  @Transactional(readOnly = true)
  public List<User> getUsersByRole(Role role) {
    log.debug("Fetching all users");
    return userRepository.findByRole(role);
  }

  /**
   * Get acitve users by role
   * 
   * TODO: pagination
   * 
   * @param role
   * @return List of enabled users
   */
  @Transactional(readOnly = true)
  public List<User> getActiveUsersByRole(Role role) {
    log.debug("Fetching active users by role");
    return userRepository.findByRoleAndEnabledTrue(role);
  }

  /**
   * Count Users by Role
   * 
   * @param role
   * @return number of users
   */
  @Transactional(readOnly = true)
  public long countUsersByRole(Role role) {
    return userRepository.countByRole(role);
  }

  /**
   * Check if user exists by email
   * 
   * @param email
   * @return true if exists, false otherwise
   */
  @Transactional(readOnly = true)
  public boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }

  /**
   * Update user
   * 
   * Check if user exists
   * Update
   * Save
   * 
   * @param id
   * @param updatedUser
   * @return Updated User
   * @throws UserNotFoundException if user not found
   */
  public User updateUser(Long id, User updatedUser) {
    log.info("Updating user with ID: {}", id);

    User existingUser = userRepository.findById(id).orElseThrow(() -> {
      log.error("User not found with ID: {}", id);
      return new UserNotFoundException(id);
    });

    existingUser.setFirstName(updatedUser.getFirstName());
    existingUser.setLastName(updatedUser.getLastName());
    existingUser.setEmail(updatedUser.getEmail());

    User savedUser = userRepository.save(existingUser);
    log.info("User updated successfully: {}", savedUser.getId());

    return savedUser;
  }

  /**
   * Uodate user password
   * 
   * @param id
   * @param newPassword
   * @return updated user
   */
  public User updatePassword(Long id, String newPassword) {
    log.info("Updateing password for user ID:{}", id);
    User user = userRepository.findById(id).orElseThrow(() -> {
      log.error("User not found with ID: {}", id);
      return new UserNotFoundException(id);
    });

    String encodedPassword = passwordEncoder.encode(newPassword);
    user.setPassword(encodedPassword);

    return userRepository.save(user);
  }

  /**
   * Enable/Disable account
   * 
   * @param id
   * @param enabled true to enable
   * @return Updated user
   */
  public User setUserEnabled(Long id, boolean enabled) {
    log.info("Setting user ID {} enabled status to: {}", id, enabled);

    User user = userRepository.findById(id)
        .orElseThrow(() -> {
          log.error("User not found with ID: {}", id);
          return new UserNotFoundException(id);
        });

    user.setEnabled(enabled);
    return userRepository.save(user);

  }

  /**
   * Delete user - hard delete
   * 
   * permanently deletes
   * 
   * @param id
   */
  public void deleteUser(Long id) {
    log.warn("Deleteing user with ID: {}", id);

    if (!userRepository.existsById(id)) {
      throw new UserNotFoundException(id);
    }

    userRepository.deleteById(id);
    log.info("User deleted: {}", id);
  }

  /**
   * Soft delete - disable account
   * 
   * @param id
   * @return disabeld user
   */
  public User disableUser(Long id) {
    log.info("Disabling user account: {}", id);
    return setUserEnabled(id, false);
  }

  /**
   * Get total User Count
   * 
   * @return total number of users
   */
  @Transactional(readOnly = true)
  public long getTotalUserCount() {
    return userRepository.count();
  }

}
