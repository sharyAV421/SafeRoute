package edu.saferoute.Presentacion_Controller;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import edu.saferoute.Modelo_de_Dominio.Emergencia;
import edu.saferoute.Negocio_Service.EmergenciaService;
@RestController @RequestMapping("/api/emergencias")
public class EmergenciaController {
 private final EmergenciaService service;
 public EmergenciaController(EmergenciaService service){this.service=service;}
 @GetMapping @PreAuthorize("hasAnyAuthority('ADMINISTRADOR','RESPONSABLE_EMERGENCIAS')") public List<Emergencia> listar(){return service.listar();}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAuthority('RESPONSABLE_EMERGENCIAS')") public Emergencia crear(@Valid @RequestBody CrearEmergencia b){var zonas=b.espaciosAfectadosIds()==null?List.<edu.saferoute.Modelo_de_Dominio.ZonaAfectada>of():b.espaciosAfectadosIds().stream().map(id->new edu.saferoute.Modelo_de_Dominio.ZonaAfectada(null,id,edu.saferoute.Modelo_de_Dominio.EstadoEspacio.AFECTADO,null)).toList();return service.crear(new Emergencia(null,b.tipo(),b.gravedad(),b.descripcion(),edu.saferoute.Modelo_de_Dominio.EstadoEmergencia.ACTIVA,null,zonas));}
 @PostMapping("/{id}/finalizar") @PreAuthorize("hasAuthority('RESPONSABLE_EMERGENCIAS')") public Emergencia finalizar(@PathVariable String id){return service.finalizar(id);}
 @PutMapping("/{id}") @PreAuthorize("hasAuthority('RESPONSABLE_EMERGENCIAS')") public Emergencia actualizar(@PathVariable String id,@Valid @RequestBody ActualizarEmergenciaBody b){return service.actualizarDatos(id,b.tipo(),b.gravedad(),b.descripcion());}
 @PatchMapping("/{id}/zonas-afectadas") @PreAuthorize("hasAuthority('RESPONSABLE_EMERGENCIAS')") public Emergencia zonasAfectadas(@PathVariable String id,@Valid @RequestBody ActualizarZonasBody b){return service.actualizarZonasAfectadas(id,b.espaciosAfectadosIds());}
 public record CrearEmergencia(@jakarta.validation.constraints.NotNull edu.saferoute.Modelo_de_Dominio.TipoEmergencia tipo,@jakarta.validation.constraints.NotNull edu.saferoute.Modelo_de_Dominio.GravedadEmergencia gravedad,@NotBlank String descripcion,List<String> espaciosAfectadosIds){}
 public record ActualizarEmergenciaBody(@jakarta.validation.constraints.NotNull edu.saferoute.Modelo_de_Dominio.TipoEmergencia tipo,@jakarta.validation.constraints.NotNull edu.saferoute.Modelo_de_Dominio.GravedadEmergencia gravedad,@NotBlank String descripcion){}
 public record ActualizarZonasBody(@jakarta.validation.constraints.NotNull List<@NotBlank String> espaciosAfectadosIds){}
}
