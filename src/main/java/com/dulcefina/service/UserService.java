package com.dulcefina.service;

import com.dulcefina.dto.*; // Keep other DTO imports

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserResponse> findAll();
    Optional<UserResponse> findById(Long id);
    Optional<UserResponse> findByUsername(String username);

    // --- METODOS DE REGISTRO ---
    /**
     * Paso 1: Valida datos, genera y envía código de verificación.
     * No crea el usuario aún. Lanza excepción si user/email ya existen.
     */
    void requestVerificationCode(UserCreateRequest req);

    /**
     * Paso 2: Verifica el código y, si es válido, crea el usuario.
     * Lanza excepción si el código es inválido/expirado o si hay error al crear.
     */
    UserResponse verifyAndRegister(VerificationRequest req);

    // --- OTROS METODOS ---
    UserResponse update(Long id, UserUpdateRequest req);
    void delete(Long id);
    LoginResponse login(LoginRequest req);
}