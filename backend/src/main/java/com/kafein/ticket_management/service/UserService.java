package com.kafein.ticket_management.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kafein.ticket_management.aop.Audit;
import com.kafein.ticket_management.dto.request.RequestCreateUserDto;
import com.kafein.ticket_management.dto.request.RequestLoginDto;
import com.kafein.ticket_management.dto.response.ResponseUserDto;
import com.kafein.ticket_management.dto.response.ResponseUserForAssignmentDto;
import com.kafein.ticket_management.exception.ResourceNotFoundException;
import com.kafein.ticket_management.exception.UnauthorizedException;
import com.kafein.ticket_management.exception.UserAlreadyExistsException;
import com.kafein.ticket_management.mapper.UserMapper;
import com.kafein.ticket_management.model.User;
import com.kafein.ticket_management.model.enums.Role;
import com.kafein.ticket_management.repository.UserRepository;
import com.kafein.ticket_management.security.JwtUtil;

import jakarta.transaction.Transactional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
            UserMapper userMapper, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
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

    @Transactional
    @Audit(action = "USER_LOGIN")
    public Map<String, String> login(RequestLoginDto requestLoginDto) {

        User user = userRepository.findByEmail(requestLoginDto.email())
                .filter(u -> passwordEncoder.matches(requestLoginDto.password(), u.getPassword()))
                .orElseThrow(() -> new BadCredentialsException("Email veya şifre hatalı"));

        Map<String, String> tokens = new HashMap<>();

        String refreshToken = jwtUtil.generateRefreshToken(user);
        String accessToken = jwtUtil.generateToken(user);

        refreshTokenService.saveRefreshToken(refreshToken, user.getId());

        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return tokens;

    }

    @Transactional
    public void logout(String currentRefreshToken) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User) {
            refreshTokenService.revokeRefreshToken(currentRefreshToken);
        } else {
            throw new UnauthorizedException();
        }

    }

    @Transactional
    public void logoutAll() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User user) {
            refreshTokenService.revokeAllRefreshToken(user);
        } else {
            throw new UnauthorizedException();
        }

    }

    public Optional<User> getUserById(UUID userId) {
        return userRepository.findById(userId);
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
