package edu.saferoute.configuracion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import edu.saferoute.Datos_Repository.UsuarioRepository;
import edu.saferoute.Modelo_de_Dominio.Rol;
import edu.saferoute.Modelo_de_Dominio.Usuario;

class JwtFilterTest {
    private static final String SECRET = java.util.UUID.randomUUID().toString() + java.util.UUID.randomUUID();

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void usesCurrentPersistedRoleInsteadOfRoleEmbeddedInOlderToken() throws Exception {
        JwtService jwt = new JwtService(SECRET, 20);
        UsuarioRepository users = mock(UsuarioRepository.class);
        when(users.findByCorreo("person@example.edu"))
                .thenReturn(Optional.of(new Usuario("user-1", "Persona", "person@example.edu", "hash", Rol.USUARIO_EVACUADO)));
        JwtFilter filter = new JwtFilter(jwt, users);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + jwt.emitir("person@example.edu", Rol.ADMINISTRADOR.name()));
        filter.doFilter(request, new MockHttpServletResponse(), (req, res) -> {});

        List<String> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(authority -> authority.getAuthority()).toList();
        assertEquals(List.of(Rol.USUARIO_EVACUADO.name()), authorities);
    }

    @Test
    void skipsLoginPathEvenWhenAuthorizationHeaderIsPresent() throws Exception {
        JwtService jwt = new JwtService(SECRET, 20);
        UsuarioRepository users = mock(UsuarioRepository.class);
        JwtFilter filter = new JwtFilter(jwt, users);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/auth/login");
        request.addHeader("Authorization", "Bearer " + jwt.emitir("person@example.edu", Rol.ADMINISTRADOR.name()));
        var chainInvoked = new boolean[1];

        filter.doFilter(request, new MockHttpServletResponse(), (req, res) -> chainInvoked[0] = true);

        assertTrue(chainInvoked[0]);
        verifyNoInteractions(users);
    }
}
