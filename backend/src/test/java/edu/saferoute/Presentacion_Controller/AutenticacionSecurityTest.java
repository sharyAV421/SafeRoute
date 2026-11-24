package edu.saferoute.Presentacion_Controller;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import java.util.UUID;

import edu.saferoute.Datos_Repository.UsuarioRepository;
import edu.saferoute.Modelo_de_Dominio.Rol;
import edu.saferoute.Modelo_de_Dominio.Usuario;
import edu.saferoute.configuracion.JwtFilter;
import edu.saferoute.configuracion.JwtService;
import edu.saferoute.configuracion.MongoUserDetailsService;
import edu.saferoute.configuracion.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AutenticacionController.class)
@Import({SecurityConfig.class, JwtFilter.class, JwtService.class, MongoUserDetailsService.class})
@TestPropertySource(properties = {"saferoute.cors-origin=http://localhost:5173", "saferoute.jwt-ttl-minutes=20"})
class AutenticacionSecurityTest {
    private static final String JWT_SECRET = UUID.randomUUID() + UUID.randomUUID().toString();

    @DynamicPropertySource
    static void jwtSecret(DynamicPropertyRegistry registry) {
        registry.add("saferoute.jwt-secret", () -> JWT_SECRET);
    }

    @Autowired private MockMvc mvc;
    @Autowired private JwtService jwt;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired @Qualifier("jwtFilterServletRegistration") private FilterRegistrationBean<JwtFilter> jwtFilterServletRegistration;
    @MockBean private UsuarioRepository usuarios;

    @Test
    void loginIsPublicAndReturnsSessionWithoutJwtHeader() throws Exception {
        String testPassword = "Test-" + UUID.randomUUID() + "-Aa9!";
        when(usuarios.findByCorreo("admin@example.edu"))
                .thenReturn(Optional.of(new Usuario("user-1", "Administradora", "admin@example.edu",
                        passwordEncoder.encode(testPassword), Rol.ADMINISTRADOR)));

        MvcResult result = mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(java.util.Map.of(
                                "correo", "admin@example.edu", "contrasena", testPassword))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correo").value("admin@example.edu"))
                .andExpect(jsonPath("$.rol").value("ADMINISTRADOR"))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        AutenticacionController.Sesion sesion = new ObjectMapper().readValue(
                result.getResponse().getContentAsString(), AutenticacionController.Sesion.class);
        var claims = jwt.validar(sesion.token());
        assertEquals("admin@example.edu", claims.getSubject());
        assertEquals(Rol.ADMINISTRADOR.name(), claims.get("rol", String.class));

    }

    @Test
    void jwtFilterIsNotRegisteredAsASecondServletFilter() {
        assertFalse(jwtFilterServletRegistration.isEnabled());
    }

    @Test
    void protectedEndpointsStillRequireAuthentication() throws Exception {
        mvc.perform(get("/api/usuarios"))
                .andExpect(status().isForbidden());
    }
}
