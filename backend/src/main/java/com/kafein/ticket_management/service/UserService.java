package com.kafein.ticket_management.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kafein.ticket_management.aop.Audit;
import com.kafein.ticket_management.dto.request.RequestCreateUserDto;
import com.kafein.ticket_management.dto.response.ResponseUserDto;
import com.kafein.ticket_management.dto.response.ResponseUserForAssignmentDto;
import com.kafein.ticket_management.event.user.UserDeletedEvent;
import com.kafein.ticket_management.exception.ResourceNotFoundException;
import com.kafein.ticket_management.exception.UnauthorizedException;
import com.kafein.ticket_management.exception.UserAlreadyExistsException;
import com.kafein.ticket_management.mapper.UserMapper;
import com.kafein.ticket_management.model.User;
import com.kafein.ticket_management.model.enums.Role;
import com.kafein.ticket_management.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    private static final String SYSTEM_POOL_EMAIL = "unassignedpool@kafein.com";
    private static final String ROOT_ADMIN_EMAIL = "admin@kafein.com";

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder,
            ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    @Audit(action = "USER_CREATED")
    @CacheEvict(value = {"users", "analytics"}, allEntries = true)
    public ResponseUserDto createUser(RequestCreateUserDto requestCreateUserDto) {

        if (userRepository.existsByEmail(requestCreateUserDto.email())) {
            throw new UserAlreadyExistsException();
        }

        User user = User.builder()
                .name(requestCreateUserDto.name())
                .surname(requestCreateUserDto.surname())
                .email(requestCreateUserDto.email())
                .password(passwordEncoder.encode(requestCreateUserDto.password()))
                .build();

        userRepository.save(user);

        return userMapper.toDto(user);

    }

    public Optional<User> getUserById(UUID userId) {
        return userRepository.findById(userId);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public void createAdminUser() {
        if (!userRepository.existsByEmail(ROOT_ADMIN_EMAIL)) {
            User admin = User.builder()
                    .email(ROOT_ADMIN_EMAIL)
                    .password(passwordEncoder.encode("Adminkafein123!"))
                    .role(Role.ADMIN)
                    .name("Admin")
                    .surname("Kafein")
                    .build();
            userRepository.save(admin);
        }
    }

    @Transactional
    public void createSystemPool() {
        if (!userRepository.existsByEmail(SYSTEM_POOL_EMAIL)) {
            User systemPool = User.builder()
                    .email(SYSTEM_POOL_EMAIL)
                    .password(passwordEncoder.encode("Unassignedpool123!"))
                    .role(Role.SYSTEM)
                    .name("Unassigned")
                    .surname("Pool")
                    .active(false)
                    .build();
            userRepository.save(systemPool);
        }
    }

    public User getSystemPool() {
        return userRepository.findByEmail(SYSTEM_POOL_EMAIL)
                .orElseThrow(() -> new RuntimeException("Sistem havuz kullanıcısı bulunamadı!"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<ResponseUserDto> getAllUsers(String query, Pageable pageable) {

        String searchQuery = (query != null && !query.trim().isEmpty()) ? query : null;

        return userRepository.searchUsers(searchQuery, pageable)
                .map(user -> userMapper.toDto(user));
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            throw new UnauthorizedException();
        }

        User currentUser = (User) authentication.getPrincipal();
        return currentUser;
    }

    @Cacheable(value = "users", key = "#root.methodName")
    public List<ResponseUserForAssignmentDto> getAllUsersForAssignment() {
        return userRepository.findAllByActiveTrue()
                .stream()
                .map(user -> userMapper.toUserForAssignmentDto(user))
                .toList();
    }

    public ResponseUserDto getUser() {
        return userMapper.toDto(getCurrentUser());

    }

    public Long totalUserCount() {
        return userRepository.count();
    }

    @Transactional
    @Audit(action = "USER_DELETED")
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = "users", allEntries = true)
    public void softDelete(UUID id) {

        User deletedUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        if (deletedUser.getEmail().equals(ROOT_ADMIN_EMAIL)) {
            throw new RuntimeException("Root admin kullanıcısını silemezsiniz!");
        }

        deletedUser.setActive(false);

        userRepository.save(deletedUser);

        String fullName = String.format("%s %s", deletedUser.getName(), deletedUser.getSurname());
        UserDeletedEvent event = new UserDeletedEvent(id, deletedUser.getEmail(), fullName);

        eventPublisher.publishEvent(event);

    }

}
