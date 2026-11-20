package edu.saferoute.Presentacion_Controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;
import edu.saferoute.Datos_Repository.UsuarioRepository;
import edu.saferoute.configuracion.JwtService;

@RestController
@RequestMapping("/api/auth")
public class AutenticacionController {
    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarios;
    private final JwtService jwt;
    public AutenticacionController(AuthenticationManager authenticationManager,UsuarioRepository usuarios,JwtService jwt){this.authenticationManager=authenticationManager;this.usuarios=usuarios;this.jwt=jwt;}
    @PostMapping("/login") public Sesion login(@Valid @RequestBody Credenciales credenciales){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(credenciales.correo(),credenciales.contrasena()));
        var user=usuarios.findByCorreo(credenciales.correo()).orElseThrow();
        return new Sesion(user.id(),user.nombre(),user.correo(),user.rol().name(),jwt.emitir(user.correo(),user.rol().name()));
    }
    public record Credenciales(@Email @NotBlank String correo,@NotBlank String contrasena){}
    public record Sesion(String id,String nombre,String correo,String rol,String token){}
}
