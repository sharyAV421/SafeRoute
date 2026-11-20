package edu.saferoute.Presentacion_Controller;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import edu.saferoute.Modelo_de_Dominio.RutaEvacuacion;
import edu.saferoute.Negocio_Service.RutaService;
import edu.saferoute.Datos_Repository.EspacioRepository;
@RestController @RequestMapping("/api/rutas")
public class RutaController {
 private final RutaService service; private final EspacioRepository espacios;
 public RutaController(RutaService service,EspacioRepository espacios){this.service=service;this.espacios=espacios;}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAuthority('USUARIO_EVACUADO')") public RutaDTO calcular(@Valid @RequestBody SolicitudRuta b){
  RutaEvacuacion r=service.calcular(b.origenEspacioId());
  return new RutaDTO(r.id(),r.origenEspacioId(),r.destinoEspacioId(),r.destinoNombre(),r.tipoDestino(),r.distanciaTotal(),r.cantidadEspacios(),r.tiempoEstimado(),
    r.pasos().stream().map(p->espacios.findById(p.espacioId()).map(e->new PasoRutaDTO(p.espacioId(),e.nombre(),p.orden(),p.distanciaAcumuladaMetros())).orElse(new PasoRutaDTO(p.espacioId(),p.espacioId(),p.orden(),p.distanciaAcumuladaMetros()))).toList());
 }
 public record SolicitudRuta(@NotBlank String origenEspacioId){}
 public record RutaDTO(String id,String origenEspacioId,String destinoEspacioId,String destinoNombre,String tipoDestino,double distanciaTotal,int cantidadEspacios,String tiempoEstimado,java.util.List<PasoRutaDTO> pasos){}
 public record PasoRutaDTO(String espacioId,String nombreEspacio,int orden,double distanciaAcumuladaMetros){}
}
