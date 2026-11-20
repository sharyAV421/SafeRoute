package edu.saferoute.Negocio_Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import edu.saferoute.Datos_Repository.UsuarioRepository;
import edu.saferoute.Modelo_de_Dominio.Rol;
import edu.saferoute.Modelo_de_Dominio.Usuario;

class UsuarioServiceTest {
    private final UsuarioRepository usuarios=mock(UsuarioRepository.class);
    private final BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
    private final UsuarioService service=new UsuarioService(usuarios,encoder);

    @Test
    void storesPasswordHashAndNeverReturnsIt() {
        AtomicReference<Usuario> persisted=new AtomicReference<>();
        when(usuarios.findByCorreo("admin@example.edu")).thenReturn(Optional.empty());
        when(usuarios.save(any())).thenAnswer(invocation->{
            Usuario u=invocation.getArgument(0);
            persisted.set(u);
            return new Usuario("U1",u.nombre(),u.correo(),u.passwordHash(),u.rol());
        });

        Usuario result=service.crear("Admin","admin@example.edu","clave-segura-123",Rol.ADMINISTRADOR);

        assertTrue(encoder.matches("clave-segura-123",persisted.get().passwordHash()));
        assertNull(result.passwordHash());
        assertEquals("U1",result.id());
    }

    @Test
    void rejectsDuplicateEmail() {
        when(usuarios.findByCorreo("admin@example.edu"))
                .thenReturn(Optional.of(new Usuario("U1","Admin","admin@example.edu","hash",Rol.ADMINISTRADOR)));

        org.junit.jupiter.api.Assertions.assertThrows(ReglaNegocioException.class,
                ()->service.crear("Admin","admin@example.edu","clave-segura-123",Rol.ADMINISTRADOR));
    }
}
