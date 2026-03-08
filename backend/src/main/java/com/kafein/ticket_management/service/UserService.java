package com.kafein.ticket_management.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    @Transactional // TODO : HANGİ FRAMEWORK BAK
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

    public Optional<User> getUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

    @Transactional
    public void createAdminUser() {
        if (!userRepository.existsByEmail("admin@kafein.com")) {
            User admin = User.builder()
                    .email("admin@kafein.com")
                    .password(passwordEncoder.encode("Adminkafein123!"))
                    .role(Role.ADMIN)
                    .name("Admin")
                    .surname("Kafein")
                    .build();
            userRepository.save(admin);
        }
    }

    public List<ResponseUserDto> getAllUsers() {
        if (!isAdmin()) {
            throw new AccessDeniedException("Bu işlemi yapmak için ADMIN yetkisine sahip olmalısınız!");
        }

        return userRepository.findAll()
                .stream()
                .filter(user -> user.getRole() == Role.USER)
                .map(user -> userMapper.toDto(user))
                .toList();
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
                .filter(user -> user.getRole() == Role.USER)
                .map(user -> userMapper.toUserForAssignmentDto(user))
                .toList();
    }

    public ResponseUserDto getUser() {
        return userMapper.toDto(getCurrentUser());
        
    }

   

}
