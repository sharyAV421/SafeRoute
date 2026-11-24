package edu.saferoute.configuracion;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import edu.saferoute.Datos_Repository.UsuarioRepository;
import edu.saferoute.Modelo_de_Dominio.Rol;
import edu.saferoute.Modelo_de_Dominio.Usuario;

@Component
public class AdminBootstrap implements ApplicationRunner {
    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final String correo;
    private String contrasenaInicial;
    private String contrasenaRestablecimiento;

    public AdminBootstrap(
            UsuarioRepository usuarios,
            PasswordEncoder encoder,
            @Value("${saferoute.bootstrap-admin-email:}") String correo,
            @Value("${saferoute.bootstrap-admin-password:}") String contrasenaInicial,
            @Value("${saferoute.admin-reset-password:}") String contrasenaRestablecimiento) {
        this.usuarios = usuarios;
        this.encoder = encoder;
        this.correo = correo == null ? "" : correo.trim();
        this.contrasenaInicial = contrasenaInicial == null ? "" : contrasenaInicial;
        this.contrasenaRestablecimiento = contrasenaRestablecimiento == null ? "" : contrasenaRestablecimiento;
    }

    @Override
    public void run(ApplicationArguments args) {
        boolean crearSiNoExiste = !contrasenaInicial.isBlank();
        boolean restablecerExistente = !contrasenaRestablecimiento.isBlank();
        try {
            if (!crearSiNoExiste && !restablecerExistente) {
                return;
            }
            if (correo.isBlank()) {
                throw new IllegalStateException("Define SAFEROUTE_BOOTSTRAP_ADMIN_EMAIL para aprovisionar o restablecer el administrador");
            }
            if (crearSiNoExiste && restablecerExistente) {
                throw new IllegalStateException("Usa solo una variable de contraseña: aprovisionamiento o restablecimiento");
            }

            String contrasena = restablecerExistente ? contrasenaRestablecimiento : contrasenaInicial;
            if (contrasena.length() < 12) {
                throw new IllegalStateException("La contraseña de administrador debe tener al menos 12 caracteres");
            }

            Optional<Usuario> existente = usuarios.findByCorreo(correo);
            if (restablecerExistente) {
                Usuario admin = existente
                        .filter(usuario -> usuario.rol() == Rol.ADMINISTRADOR)
                        .orElseThrow(() -> new IllegalStateException("El administrador indicado no existe o no tiene rol ADMINISTRADOR"));
                usuarios.save(new Usuario(admin.id(), admin.nombre(), admin.correo(), encoder.encode(contrasenaRestablecimiento), admin.rol()));
            } else if (existente.isEmpty()) {
                usuarios.save(new Usuario(null, "Administrador inicial", correo, encoder.encode(contrasenaInicial), Rol.ADMINISTRADOR));
            }
        } finally {
            contrasenaInicial = "";
            contrasenaRestablecimiento = "";
        }
    }
}
