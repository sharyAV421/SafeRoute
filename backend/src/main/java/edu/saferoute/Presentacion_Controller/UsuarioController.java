package edu.saferoute.Presentacion_Controller;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import edu.saferoute.Modelo_de_Dominio.Rol;
import edu.saferoute.Modelo_de_Dominio.Usuario;
import edu.saferoute.Negocio_Service.UsuarioService;
@RestController @RequestMapping("/api/usuarios")
public class UsuarioController {
 private final UsuarioService service;
 public UsuarioController(UsuarioService service){this.service=service;}
 @GetMapping @PreAuthorize("hasAuthority('ADMINISTRADOR')") public List<Usuario> listar(){return service.listar();}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAuthority('ADMINISTRADOR')") public Usuario crear(@Valid @RequestBody CrearUsuario b){return service.crear(b.nombre(),b.correo(),b.contrasena(),b.rol());}
 @PatchMapping("/{id}/rol") @PreAuthorize("hasAuthority('ADMINISTRADOR')") public Usuario rol(@PathVariable String id,@Valid @RequestBody RolBody b){return service.asignarRol(id,b.rol());}
 public record CrearUsuario(@NotBlank String nombre,@Email @NotBlank String correo,@NotBlank @Size(min=10,max=100) String contrasena,@NotNull Rol rol){}
 public record RolBody(@NotNull Rol rol){}
}
