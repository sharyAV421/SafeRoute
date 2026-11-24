package edu.saferoute.configuracion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import edu.saferoute.Datos_Repository.UsuarioRepository;
import edu.saferoute.Modelo_de_Dominio.Rol;
import edu.saferoute.Modelo_de_Dominio.Usuario;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class AdminBootstrapTest {
    private static final String EMAIL = "ejemplo1@gmail.com";
    private static String testPassword() { return "Test-" + UUID.randomUUID() + "-Aa9!"; }

    @Test
    void createsNewAdminWithPasswordEncodedByInjectedEncoder() {
        UsuarioRepository usuarios = org.mockito.Mockito.mock(UsuarioRepository.class);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String initialPassword = testPassword();
        when(usuarios.findByCorreo(EMAIL)).thenReturn(Optional.empty());
        AdminBootstrap bootstrap = new AdminBootstrap(usuarios, encoder, EMAIL, initialPassword, "");

        bootstrap.run(new DefaultApplicationArguments(new String[0]));

        ArgumentCaptor<Usuario> saved = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarios).save(saved.capture());
        assertEquals(EMAIL, saved.getValue().correo());
        assertEquals(Rol.ADMINISTRADOR, saved.getValue().rol());
        assertTrue(encoder.matches(initialPassword, saved.getValue().passwordHash()));
    }

    @Test
    void resetsExistingAdminPasswordAndPreservesAccountAndRole() {
        UsuarioRepository usuarios = org.mockito.Mockito.mock(UsuarioRepository.class);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String oldPassword = testPassword();
        String resetPassword = testPassword();
        Usuario current = new Usuario("admin-id", "Admin", EMAIL, encoder.encode(oldPassword), Rol.ADMINISTRADOR);
        when(usuarios.findByCorreo(EMAIL)).thenReturn(Optional.of(current));
        AdminBootstrap bootstrap = new AdminBootstrap(usuarios, encoder, EMAIL, "", resetPassword);

        bootstrap.run(new DefaultApplicationArguments(new String[0]));

        ArgumentCaptor<Usuario> saved = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarios).save(saved.capture());
        Usuario updated = saved.getValue();
        assertEquals(current.id(), updated.id());
        assertEquals(current.nombre(), updated.nombre());
        assertEquals(current.correo(), updated.correo());
        assertEquals(Rol.ADMINISTRADOR, updated.rol());
        assertTrue(encoder.matches(resetPassword, updated.passwordHash()));
        assertFalse(encoder.matches(oldPassword, updated.passwordHash()));
    }
}
