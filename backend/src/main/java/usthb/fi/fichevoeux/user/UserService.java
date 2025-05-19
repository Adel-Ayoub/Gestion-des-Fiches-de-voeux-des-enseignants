package usthb.fi.fichevoeux.user;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usthb.fi.fichevoeux.exception.ResourceNotFoundException;
import usthb.fi.fichevoeux.user.dto.UserDto;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private UserDto mapToDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserDto(user);
    }
    private String generateEmail(String name) {
        // Implement email generation logic here
        String generatedEmail = name.toLowerCase() + "@etu.usthb.dz"; // Example logic
        
        return generatedEmail; // Placeholder
    }
    private String generatePassword(String name) {
        // Implement password generation logic here
        String generatedPassword = name.toLowerCase() + "123"; // Example logic
        
        return generatedPassword; // Placeholder
    }
    private void updateEntityFromDto(User existingUser, UserDto dto) {
        existingUser.setEmail(dto.getEmail());
        existingUser.setName(dto.getName());
        existingUser.setRole(dto.getRole());
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        logger.debug("Request to get all Users");
        return userRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        logger.debug("Request to get User with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        return mapToDto(user);
    }

    @Transactional(readOnly = true)
    public UserDto getUserByEmail(String email) {
        logger.debug("Request to get User by email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));
        return mapToDto(user);
    }

    @Transactional
    public UserDto addUser( String name, Role role) {
        String email = generateEmail(name);
        String rawPassword = generatePassword(name);
        logger.info("Request to add new User with email: {}", email);
        if (userRepository.existsByEmail(email)) {
            logger.warn("Attempted to add user with existing email: {}", email);
            throw new RuntimeException("Email already exists: " + email);
        }

        try {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setRole(role);
            newUser.setPassword(passwordEncoder.encode(rawPassword));

            User savedUser = userRepository.save(newUser);
            logger.info("Successfully added User with ID: {}", savedUser.getId());
            return mapToDto(savedUser);
        } catch (Exception e) {
            logger.error("Error adding User: {}", e.getMessage(), e);
            throw new RuntimeException("Could not save User", e);
        }
    }

    @Transactional
    public void deleteUser(long id) {
        logger.warn("Request to delete User with ID: {}", id);
        if (!userRepository.existsById(id)) {
            logger.error("Attempted to delete non-existent User with id: {}", id);
            throw new ResourceNotFoundException("User not found with id " + id);
        }
        try {
            userRepository.deleteById(id);
            logger.info("Successfully deleted User with ID: {}", id);
        } catch (Exception e) {
            logger.error("Error deleting User with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Could not delete User with id " + id, e);
        }
    }

    @Transactional
    public UserDto updateUser(long id, UserDto userDto) {
        logger.info("Request to update User details (excluding password) for ID: {}", id);
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Update failed: User not found with id {}", id);
                    return new ResourceNotFoundException("User not found with id " + id);
                });

        if (!existingUser.getEmail().equals(userDto.getEmail()) && userRepository.existsByEmail(userDto.getEmail())) {
            logger.warn("Attempted to update user {} to email {} which already exists", id, userDto.getEmail());
            throw new RuntimeException("Email already exists: " + userDto.getEmail());
        }

        try {
            updateEntityFromDto(existingUser, userDto);
            User updatedUser = userRepository.save(existingUser);
            logger.info("Successfully updated User details for ID: {}", updatedUser.getId());
            return mapToDto(updatedUser);
        } catch (Exception e) {
            logger.error("Error updating User details for ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Could not update User with id " + id, e);
        }
    }

    @Transactional
    public void changeUserPassword(long id, String newRawPassword) {
        logger.warn("Request to change password for User ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        user.setPassword(passwordEncoder.encode(newRawPassword));
        userRepository.save(user);
        logger.info("Successfully changed password for User ID: {}", id);
    }
}
