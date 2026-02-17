package com.kafein.ticket_management.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kafein.ticket_management.dto.request.RequestCreateUserDto;
import com.kafein.ticket_management.dto.request.RequestLoginDto;
import com.kafein.ticket_management.dto.response.ResponseUserDto;
import com.kafein.ticket_management.mapper.UserMapper;
import com.kafein.ticket_management.model.User;
import com.kafein.ticket_management.repository.UserRepository;
import com.kafein.ticket_management.security.JwtUtil;

import jakarta.transaction.Transactional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // TODO : EXCEPTION KISMI DUZENLENECEK
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanici bulunamadi : " + email));
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

        return UserMapper.toResponseUserDto(user);

    }

    public Map<String, String> login(RequestLoginDto requestLoginDto) {
        
        User user = userRepository.findByEmail(requestLoginDto.getEmail())
                .orElseThrow(() -> new RuntimeException("Kullanici bulunamadi : " + requestLoginDto.getEmail()));

        if (!passwordEncoder.matches(requestLoginDto.getPassword(), user.getPassword())){
            throw new RuntimeException("Kullanici veya şifre hatali");
        }

        Map<String, String> tokens = new HashMap<>();

        String refreshToken = jwtUtil.generateRefreshToken(user);
        String accessToken = jwtUtil.generateToken(user);

        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return tokens;
       
    }






}
