package com.kafein.ticket_management.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException.BadRequest;

import com.kafein.ticket_management.dto.request.RequestCreateUserDto;
import com.kafein.ticket_management.dto.request.RequestLoginDto;
import com.kafein.ticket_management.dto.response.ResponseUserDto;
import com.kafein.ticket_management.exception.ResourceNotFoundException;
import com.kafein.ticket_management.mapper.UserMapper;
import com.kafein.ticket_management.model.User;
import com.kafein.ticket_management.model.enums.Role;
import com.kafein.ticket_management.repository.UserRepository;
import com.kafein.ticket_management.security.JwtUtil;

import jakarta.transaction.Transactional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, UserMapper userMapper) {
        this.userRepository = userRepository;
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
    public ResponseUserDto createUser(RequestCreateUserDto requestCreateUserDto) {

        User user = User.builder()
                .name(requestCreateUserDto.getName())
                .surname(requestCreateUserDto.getSurname())
                .email(requestCreateUserDto.getEmail())
                .password(passwordEncoder.encode(requestCreateUserDto.getPassword()))
                .build();

        userRepository.save(user);

        return userMapper.toDto(user);

    }

    public Map<String, String> login(RequestLoginDto requestLoginDto) {
        
        User user = userRepository.findByEmail(requestLoginDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", requestLoginDto.getEmail()));

        if (!passwordEncoder.matches(requestLoginDto.getPassword(), user.getPassword())){
            throw new BadCredentialsException("Email veya şifre hatali");
        }

        Map<String, String> tokens = new HashMap<>();

        String refreshToken = jwtUtil.generateRefreshToken(user);
        String accessToken = jwtUtil.generateToken(user);

        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return tokens;
       
    }

    public Optional<User> getUserById(UUID assignedToId) {
        return userRepository.findById(assignedToId);
    }


    @Transactional
    public void createAdminUser() {
        if (!userRepository.existsByEmail("admin@kafein.com")) {
            User admin = User.builder()
                    .email("admin@kafein.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .name("Admin")
                    .surname("Kafein")
                    .build();
            userRepository.save(admin);
        }
    }

    public List<ResponseUserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getRole() == Role.USER)
                .map(user -> userMapper.toDto(user))
                .toList();
    }






}
