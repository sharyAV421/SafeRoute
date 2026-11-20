package edu.saferoute.Presentacion_Controller;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import edu.saferoute.Modelo_de_Dominio.*;
import edu.saferoute.Negocio_Service.*;
@RestController @RequestMapping("/api/riesgos")
public class RiesgoController {
 private final RiesgoService riesgos; private final PrioridadService prioridades;
 public RiesgoController(RiesgoService riesgos,PrioridadService prioridades){this.riesgos=riesgos;this.prioridades=prioridades;}
 @GetMapping @PreAuthorize("hasAnyAuthority('ADMINISTRADOR','RESPONSABLE_EMERGENCIAS')") public List<CaracteristicaRiesgo> listar(){return riesgos.listar();}
 @GetMapping("/espacio/{id}") @PreAuthorize("hasAnyAuthority('ADMINISTRADOR','RESPONSABLE_EMERGENCIAS')") public List<CaracteristicaRiesgo> porEspacio(@PathVariable String id){return riesgos.porEspacio(id);}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAuthority('ADMINISTRADOR')") public CaracteristicaRiesgo crear(@Valid @RequestBody CaracteristicaBody b){return riesgos.crear(new CaracteristicaRiesgo(null,b.espacioId(),b.tipo(),b.descripcion()));}
 @GetMapping("/prioridades/{emergenciaId}") @PreAuthorize("hasAnyAuthority('ADMINISTRADOR','RESPONSABLE_EMERGENCIAS')") public List<PrioridadEspacio> prioridades(@PathVariable String emergenciaId){return prioridades.consultar(emergenciaId);}
 @GetMapping("/niveles") public List<NivelRiesgo> niveles(){return List.of(NivelRiesgo.values());}
 public record CaracteristicaBody(@NotBlank String espacioId,@NotNull TipoCaracteristicaRiesgo tipo,@NotBlank String descripcion){}
}
