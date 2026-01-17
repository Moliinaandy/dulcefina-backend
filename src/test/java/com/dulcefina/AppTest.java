package com.dulcefina;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Prueba de contexto moderna para Spring Boot 3.x.
 *
 * Esta prueba usa la anotación @SpringBootTest para cargar el contexto
 * completo de la aplicación. Si el contexto se carga sin errores,
 * significa que todas las configuraciones (incluyendo beans como el de Cloudinary)
 * y las dependencias están correctamente configuradas.
 */
@SpringBootTest
class DulcefinaApplicationTests {

    /**
     * Prueba simple para verificar que el contexto de la aplicación se carga correctamente.
     * Si la aplicación tiene problemas para inicializar beans (como el error de Cloudinary
     * que tenías antes), esta prueba fallará y te mostrará el error.
     */
    @Test
    void contextLoads() {
        // La prueba pasa si el contexto de Spring Boot se carga sin lanzar excepciones.
    }

}