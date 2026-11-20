package edu.saferoute.configuracion;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import edu.saferoute.Datos_Repository.UsuarioRepository;

@Service
public class MongoUserDetailsService implements UserDetailsService {
    private final UsuarioRepository usuarios;
    public MongoUserDetailsService(UsuarioRepository usuarios) { this.usuarios=usuarios; }
    @Override public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var usuario=usuarios.findByCorreo(username).orElseThrow(()->new UsernameNotFoundException("Cuenta no encontrada"));
        if(usuario.passwordHash()==null || usuario.rol()==null) throw new UsernameNotFoundException("Cuenta sin credenciales válidas");
        return User.withUsername(usuario.correo()).password(usuario.passwordHash())
                .authorities(new SimpleGrantedAuthority(usuario.rol().name())).build();
    }
}
