package edu.saferoute.configuracion;

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
    private final UsuarioRepository usuarios; private final PasswordEncoder encoder;
    @Value("${saferoute.bootstrap-admin-email:}") private String correo;
    @Value("${saferoute.bootstrap-admin-password:}") private String contrasena;
    public AdminBootstrap(UsuarioRepository usuarios,PasswordEncoder encoder){this.usuarios=usuarios;this.encoder=encoder;}
    @Override public void run(ApplicationArguments args){
        if(correo.isBlank()&&contrasena.isBlank()) return;
        if(correo.isBlank()||contrasena.length()<12) throw new IllegalStateException("Para aprovisionar el administrador, define correo y contraseña de al menos 12 caracteres en variables de entorno");
        if(usuarios.findByCorreo(correo).isEmpty()) usuarios.save(new Usuario(null,"Administrador inicial",correo,encoder.encode(contrasena),Rol.ADMINISTRADOR));
        correo="";contrasena="";
    }
}
