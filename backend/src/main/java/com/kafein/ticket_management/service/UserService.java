package com.kafein.ticket_management.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder,
            ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    @Transactional
    @Audit(action = "USER_CREATED")
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
        String adminEmail = "admin@kafein.com";
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = User.builder()
                    .email(adminEmail)
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
        String poolEmail = "unassignedpool@kafein.com";

        if (!userRepository.existsByEmail(poolEmail)) {
            User systemPool = User.builder()
                    .email(poolEmail)
                    .password(passwordEncoder.encode("Unassignedpool123!"))
                    .role(Role.SYSTEM)
                    .name("Unassigned")
                    .surname("Pool")
                    .active(false)
                    .build();
            userRepository.save(systemPool);
        }
    }

    public User getSystemPool(){
        return userRepository.findByEmail("unassignedpool@kafein.com")
            .orElseThrow(() -> new RuntimeException("Sistem havuz kullanıcısı bulunamadı!"));
    }

    public Page<ResponseUserDto> getAllUsers(String query, Pageable pageable) {
        if (!isAdmin()) {
            throw new AccessDeniedException("Bu işlemi yapmak için ADMIN yetkisine sahip olmalısınız!");
        }

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

    public boolean isAdmin() {
        User currentUser = getCurrentUser();

        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return true;
        }
        return false;
    }

    public List<ResponseUserForAssignmentDto> getAllUsersForAssignment() {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.isActive() == true)
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
    public void softDelete(UUID id) {

        if (!isAdmin()) {
            throw new AccessDeniedException("Bu işlemi yapmak için ADMIN yetkisine sahip olmalısınız!");
        }

        User deletedUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        if (deletedUser.getEmail().equals("admin@kafein.com")) {
            throw new RuntimeException("Root admin kullanıcısını silemezsiniz!");
        }

        deletedUser.setActive(false);

        userRepository.save(deletedUser);

        String fullName = String.format("%s %s", deletedUser.getName(), deletedUser.getSurname());
        UserDeletedEvent event = new UserDeletedEvent(id, deletedUser.getEmail(), fullName);

        eventPublisher.publishEvent(event);

    }

}
