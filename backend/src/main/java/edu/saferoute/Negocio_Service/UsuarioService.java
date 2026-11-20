package edu.saferoute.Negocio_Service;

import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.saferoute.Datos_Repository.UsuarioRepository;
import edu.saferoute.Modelo_de_Dominio.Usuario;

@Service
@Transactional
public class UsuarioService {
    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    public UsuarioService(UsuarioRepository usuarios, PasswordEncoder encoder) { this.usuarios=usuarios; this.encoder=encoder; }
    public List<Usuario> listar() { return usuarios.findAll().stream().map(this::sinHash).toList(); }
    public Usuario crear(String nombre, String correo, String password, edu.saferoute.Modelo_de_Dominio.Rol rol) {
        if (usuarios.findByCorreo(correo).isPresent()) throw new ReglaNegocioException("El correo ya está registrado");
        return sinHash(usuarios.save(new Usuario(null,nombre,correo,encoder.encode(password),rol)));
    }
    public Usuario asignarRol(String id, edu.saferoute.Modelo_de_Dominio.Rol rol) {
        Usuario actual=usuarios.findById(id).orElseThrow(()->new RecursoNoEncontrado("Usuario no encontrado"));
        if(rol==null) throw new ReglaNegocioException("Rol requerido");
        return sinHash(usuarios.save(new Usuario(actual.id(),actual.nombre(),actual.correo(),actual.passwordHash(),rol)));
    }
    private Usuario sinHash(Usuario u) { return new Usuario(u.id(),u.nombre(),u.correo(),null,u.rol()); }
}
