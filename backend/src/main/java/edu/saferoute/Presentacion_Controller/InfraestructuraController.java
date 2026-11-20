package edu.saferoute.Presentacion_Controller;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import edu.saferoute.Modelo_de_Dominio.*;
import edu.saferoute.Negocio_Service.InfraestructuraService;
@RestController @RequestMapping("/api/infraestructura")
public class InfraestructuraController {
 private final InfraestructuraService service;
 public InfraestructuraController(InfraestructuraService service){this.service=service;}
 @GetMapping @PreAuthorize("hasAnyAuthority('ADMINISTRADOR','RESPONSABLE_EMERGENCIAS','USUARIO_EVACUADO')") public InfraestructuraService.InfraestructuraResumen resumen(){return service.resumen();}
 @PostMapping("/instituciones") @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAuthority('ADMINISTRADOR')") public Institucion institucion(@Valid @RequestBody NombreBody b){return service.crear(new Institucion(null,b.nombre(),b.descripcion()));}
 @PostMapping("/edificios") @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAuthority('ADMINISTRADOR')") public Edificio edificio(@Valid @RequestBody EdificioBody b){return service.crear(new Edificio(null,b.institucionId(),b.nombre(),b.descripcion()));}
 @PostMapping("/pisos") @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAuthority('ADMINISTRADOR')") public Piso piso(@Valid @RequestBody PisoBody b){return service.crear(new Piso(null,b.edificioId(),b.nombre(),b.nivel()));}
 @PostMapping("/espacios") @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAuthority('ADMINISTRADOR')") public Espacio espacio(@Valid @RequestBody EspacioBody b){return service.crear(new Espacio(null,b.pisoId(),b.nombre(),b.tipo(),b.capacidad(),b.nivelRiesgo(),EstadoEspacio.DISPONIBLE));}
 @PutMapping("/instituciones/{id}") @PreAuthorize("hasAuthority('ADMINISTRADOR')") public Institucion actualizarInstitucion(@PathVariable String id,@Valid @RequestBody NombreBody b){return service.actualizarInstitucion(id,new Institucion(null,b.nombre(),b.descripcion()));}
 @PutMapping("/edificios/{id}") @PreAuthorize("hasAuthority('ADMINISTRADOR')") public Edificio actualizarEdificio(@PathVariable String id,@Valid @RequestBody EditarEdificioBody b){return service.actualizarEdificio(id,new Edificio(null,null,b.nombre(),b.descripcion()));}
 @PutMapping("/pisos/{id}") @PreAuthorize("hasAuthority('ADMINISTRADOR')") public Piso actualizarPiso(@PathVariable String id,@Valid @RequestBody EditarPisoBody b){return service.actualizarPiso(id,new Piso(null,null,b.nombre(),b.nivel()));}
 @PutMapping("/espacios/{id}") @PreAuthorize("hasAuthority('ADMINISTRADOR')") public Espacio actualizarEspacio(@PathVariable String id,@Valid @RequestBody EditarEspacioBody b){return service.actualizarEspacio(id,new Espacio(null,null,b.nombre(),b.tipo(),b.capacidad(),b.nivelRiesgo(),null));}
 @PatchMapping("/espacios/{id}/bloqueo") @PreAuthorize("hasAnyAuthority('ADMINISTRADOR','RESPONSABLE_EMERGENCIAS')") public Espacio bloqueo(@PathVariable String id,@RequestBody BloqueoBody b){return service.marcarBloqueo(id,b.bloqueado());}
 @PatchMapping("/espacios/{id}/nivel-riesgo") @PreAuthorize("hasAuthority('ADMINISTRADOR')") public Espacio nivelRiesgo(@PathVariable String id,@Valid @RequestBody NivelRiesgoBody b){return service.asignarNivelRiesgo(id,b.nivelRiesgo());}
 @PatchMapping("/conexiones/{id}/bloqueo") @PreAuthorize("hasAnyAuthority('ADMINISTRADOR','RESPONSABLE_EMERGENCIAS')") public Conexion bloqueoConexion(@PathVariable String id,@RequestBody BloqueoConexionBody b){return service.marcarBloqueoConexion(id,b.bloqueada());}
 @PostMapping("/conexiones") @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAuthority('ADMINISTRADOR')") public Conexion conexion(@Valid @RequestBody ConexionBody b){return service.crear(new Conexion(null,b.origenEspacioId(),b.destinoEspacioId(),b.distanciaMetros(),false));}
 @PostMapping("/salidas") @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAuthority('ADMINISTRADOR')") public SalidaEmergencia salida(@Valid @RequestBody SalidaBody b){return service.crear(new SalidaEmergencia(null,b.espacioId(),b.nombre(),b.ubicacion(),b.estado()));}
 @PostMapping("/puntos-encuentro") @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAuthority('ADMINISTRADOR')") public PuntoEncuentro punto(@Valid @RequestBody PuntoBody b){return service.crear(new PuntoEncuentro(null,b.espacioId(),b.nombre(),b.ubicacion(),b.capacidad(),b.estado()));}
 @PatchMapping("/salidas/{id}/disponibilidad") @PreAuthorize("hasAuthority('ADMINISTRADOR')") public SalidaEmergencia disponibilidadSalida(@PathVariable String id,@Valid @RequestBody DisponibilidadBody b){return service.actualizarDisponibilidadSalida(id,b.estado());}
 @PatchMapping("/puntos-encuentro/{id}/disponibilidad") @PreAuthorize("hasAuthority('ADMINISTRADOR')") public PuntoEncuentro disponibilidadPunto(@PathVariable String id,@Valid @RequestBody DisponibilidadBody b){return service.actualizarDisponibilidadPunto(id,b.estado());}
 public record NombreBody(@NotBlank String nombre,String descripcion){}
 public record EdificioBody(@NotBlank String institucionId,@NotBlank String nombre,String descripcion){}
 public record PisoBody(@NotBlank String edificioId,@NotBlank String nombre,Integer nivel){}
 public record EspacioBody(@NotBlank String pisoId,@NotBlank String nombre,@NotBlank String tipo,@jakarta.validation.constraints.PositiveOrZero int capacidad,@jakarta.validation.constraints.NotNull NivelRiesgo nivelRiesgo){}
 public record EditarEdificioBody(@NotBlank String nombre,String descripcion){}
 public record EditarPisoBody(@NotBlank String nombre,Integer nivel){}
 public record EditarEspacioBody(@NotBlank String nombre,@NotBlank String tipo,@jakarta.validation.constraints.PositiveOrZero int capacidad,@jakarta.validation.constraints.NotNull NivelRiesgo nivelRiesgo){}
 public record DisponibilidadBody(@jakarta.validation.constraints.NotNull EstadoDisponibilidad estado){}
 public record NivelRiesgoBody(@jakarta.validation.constraints.NotNull NivelRiesgo nivelRiesgo){}
 public record ConexionBody(@NotBlank String origenEspacioId,@NotBlank String destinoEspacioId,@Positive double distanciaMetros){}
 public record SalidaBody(@NotBlank String espacioId,@NotBlank String nombre,@NotBlank String ubicacion,@jakarta.validation.constraints.NotNull EstadoDisponibilidad estado){}
 public record PuntoBody(@NotBlank String espacioId,@NotBlank String nombre,@NotBlank String ubicacion,@jakarta.validation.constraints.PositiveOrZero int capacidad,@jakarta.validation.constraints.NotNull EstadoDisponibilidad estado){}
 public record BloqueoBody(boolean bloqueado){}
 public record BloqueoConexionBody(boolean bloqueada){}
}
