package com.dulcefina.service;

import com.dulcefina.config.CacheConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Optional;

@Service
public class VerificationCodeService {

    private final Cache verificationCodeCache;
    private final EmailService emailService;
    private final SecureRandom random = new SecureRandom();

    @Autowired
    public VerificationCodeService(CacheManager cacheManager, EmailService emailService) {
        this.verificationCodeCache = cacheManager.getCache(CacheConfig.VERIFICATION_CODE_CACHE);
        this.emailService = emailService;
    }

    /** Codigo random de 4 digitos */
    private String generateCode() {
        // Genera un número entre 1000 y 9999
        int code = 1000 + random.nextInt(9000);
        return String.valueOf(code);
    }

    /** Genera el codigo, se guarda en el cache asociado al email y lo envia */
    public void generateAndSendCode(String email) {
        String code = generateCode();
        // Guarda el código en el caché (sobrescribe si ya existe uno para ese email)
        verificationCodeCache.put(email, code);

        // Envía el correo
        emailService.sendVerificationCode(email, code);
    }

    /** Verifica que el codigo coincida. Si coinciden lo elimina del cache */
    public boolean verifyCode(String email, String providedCode) {
        String storedCode = verificationCodeCache.get(email, String.class);

        if (storedCode != null && storedCode.equals(providedCode)) {
            // Código correcto, lo eliminamos del caché para que no se reutilice
            verificationCodeCache.evict(email);
            return true;
        }
        // Código incorrecto o expirado (ya no está en el caché)
        return false;
    }
}