package com.dulcefina.service.impl;

import com.dulcefina.dto.*;
import com.dulcefina.entity.Role;
import com.dulcefina.entity.UserAccount;
import com.dulcefina.repository.RoleRepository;
import com.dulcefina.repository.UserAccountRepository;
import com.dulcefina.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import com.dulcefina.service.VerificationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserAccountRepository userRepo;
    private final RoleRepository roleRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final byte[] jwtSecret = Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded(); // Secure key (256 bits)
    private final VerificationCodeService verificationCodeService;

    @Autowired
    public UserServiceImpl(UserAccountRepository userRepo,
                           RoleRepository roleRepo,
                           VerificationCodeService verificationCodeService) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.verificationCodeService = verificationCodeService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    private UserResponse toResponse(UserAccount u) {
        if (u == null) return null;
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        return UserResponse.builder()
                .userId(u.getUserId())
                .username(u.getUsername())
                .email(u.getEmail())
                .fullName(u.getFullName())
                .phone(u.getPhone())
                .roleId(u.getRole() != null ? u.getRole().getRoleId() : null)
                .roleName(u.getRole() != null ? u.getRole().getName() : null)
                .isActive(u.getIsActive())
                .createdAt(u.getCreatedAt() != null ? u.getCreatedAt().format(fmt) : null)
                .lastLogin(u.getLastLogin() != null ? u.getLastLogin().format(fmt) : null)
                .build();
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepo.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public Optional<UserResponse> findById(Long id) {
        return userRepo.findById(id).map(this::toResponse);
    }

    @Override
    public Optional<UserResponse> findByUsername(String username) {
        return userRepo.findByUsername(username).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public void requestVerificationCode(UserCreateRequest req) {
        logger.info("Solicitud de código de verificación para email: {}", req.getEmail());
        if (userRepo.existsByUsername(req.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso.");
        }
        if (userRepo.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("El correo electrónico ya está registrado.");
        }
        try {
            verificationCodeService.generateAndSendCode(req.getEmail());
            logger.info("Código de verificación enviado a {}", req.getEmail());
        } catch (RuntimeException e) {
            logger.error("Fallo al enviar el código de verificación a {}: {}", req.getEmail(), e.getMessage());
            throw new RuntimeException("No se pudo enviar el código de verificación. Intenta más tarde.");
        }
    }

    @Override
    @Transactional
    public UserResponse verifyAndRegister(VerificationRequest req) {
        logger.info("Intentando verificar y registrar para email: {}", req.getEmail());
        UserCreateRequest userData = req.getUserData();

        boolean isValidCode = verificationCodeService.verifyCode(req.getEmail(), req.getCode());
        if (!isValidCode) {
            logger.warn("Código de verificación inválido o expirado para {}", req.getEmail());
            throw new IllegalArgumentException("Código de verificación inválido o expirado.");
        }
        logger.info("Código verificado exitosamente para {}", req.getEmail());

        if (userData.getPassword() == null || userData.getPassword().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es requerida.");
        }

        Role role = roleRepo.findById(Optional.ofNullable(userData.getRoleId()).orElse(1))
                .orElseThrow(() -> new NoSuchElementException("Rol no encontrado: " + userData.getRoleId()));

        String hashed = passwordEncoder.encode(userData.getPassword());

        UserAccount u = UserAccount.builder()
                .username(userData.getUsername())
                .email(userData.getEmail())
                .passwordHash(hashed)
                .fullName(userData.getFullName())
                .phone(userData.getPhone())
                .role(role)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        try {
            UserAccount saved = userRepo.save(u);
            logger.info("Usuario registrado exitosamente: {}", saved.getEmail());
            return toResponse(saved);
        } catch (DataIntegrityViolationException e) {
            logger.error("Error de integridad al guardar usuario {}: {}", userData.getEmail(), e.getMessage());
            throw new IllegalArgumentException("El nombre de usuario o correo ya existe (error concurrente).");
        }
    }

    @Override
    public UserResponse update(Long id, UserUpdateRequest req) {
        UserAccount existing = userRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + id));

        if (req.getEmail() != null && !req.getEmail().equals(existing.getEmail())) {
            if (userRepo.existsByEmail(req.getEmail())) {
                throw new IllegalArgumentException("email already in use");
            }
            existing.setEmail(req.getEmail());
        }
        if (req.getFullName() != null) existing.setFullName(req.getFullName());
        if (req.getPhone() != null) existing.setPhone(req.getPhone());
        if (req.getIsActive() != null) existing.setIsActive(req.getIsActive());
        if (req.getRoleId() != null) {
            Role r = roleRepo.findById(req.getRoleId())
                    .orElseThrow(() -> new NoSuchElementException("Role not found: " + req.getRoleId()));
            existing.setRole(r);
        }

        UserAccount saved = userRepo.save(existing);
        return toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        if (!userRepo.existsById(id)) {
            throw new NoSuchElementException("User not found: " + id);
        }
        userRepo.deleteById(id);
    }

    @Override
    public LoginResponse login(LoginRequest req) {
        UserAccount user = userRepo.findByUsername(req.getUsernameOrEmail())
                .orElseGet(() -> userRepo.findByEmail(req.getUsernameOrEmail()).orElse(null));

        logger.info("Login attempt for: {}", req.getUsernameOrEmail());

        if (user == null) {
            logger.warn("User not found for: {}", req.getUsernameOrEmail());
            throw new IllegalArgumentException("Invalid credentials");
        }

        logger.info("Fetched user: {} with role: {}", user.getEmail(), user.getRole() != null ? user.getRole().getName() : "null");

        if (user.getRole() == null) {
            logger.error("User {} has no role configured", user.getEmail());
            throw new IllegalArgumentException("User role not configured");
        }

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            logger.warn("Invalid password for user: {}", user.getEmail());
            throw new IllegalArgumentException("Invalid credentials");
        }

        user.setLastLogin(LocalDateTime.now());
        userRepo.save(user);

        String token = Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(Keys.hmacShaKeyFor(jwtSecret), SignatureAlgorithm.HS256) //key
                .compact();

        UserResponse userResponse = toResponse(user);
        return new LoginResponse(token, userResponse);
    }
}
