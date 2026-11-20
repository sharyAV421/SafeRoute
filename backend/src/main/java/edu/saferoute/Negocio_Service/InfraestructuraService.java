package edu.saferoute.Negocio_Service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.saferoute.Datos_Repository.*;
import edu.saferoute.Modelo_de_Dominio.*;

@Service
@Transactional
public class InfraestructuraService {
    private final InstitucionRepository instituciones;
    private final EdificioRepository edificios;
    private final PisoRepository pisos;
    private final EspacioRepository espacios;
    private final ConexionRepository conexiones;
    private final SalidaEmergenciaRepository salidas;
    private final PuntoEncuentroRepository puntos;

    public InfraestructuraService(InstitucionRepository instituciones, EdificioRepository edificios,
            PisoRepository pisos, EspacioRepository espacios, ConexionRepository conexiones,
            SalidaEmergenciaRepository salidas, PuntoEncuentroRepository puntos) {
        this.instituciones = instituciones; this.edificios = edificios; this.pisos = pisos;
        this.espacios = espacios; this.conexiones = conexiones; this.salidas = salidas; this.puntos = puntos;
    }

    public InfraestructuraResumen resumen() {
        return new InfraestructuraResumen(instituciones.findAll(), edificios.findAll(), pisos.findAll(),
                espacios.findAll(), conexiones.findAll(), salidas.findAll(), puntos.findAll());
    }
    public Institucion crear(Institucion x) { return instituciones.save(new Institucion(null, x.nombre(), x.descripcion())); }
    public Institucion actualizarInstitucion(String id, Institucion x) {
        Institucion actual=instituciones.findById(id).orElseThrow(()->new RecursoNoEncontrado("Institución no encontrada"));
        return instituciones.save(new Institucion(actual.id(),x.nombre(),x.descripcion()));
    }
    public Edificio crear(Edificio x) { require(instituciones.existsById(x.institucionId()), "Institución no encontrada"); return edificios.save(new Edificio(null,x.institucionId(),x.nombre(),x.descripcion())); }
    public Piso crear(Piso x) { require(edificios.existsById(x.edificioId()), "Edificio no encontrado"); return pisos.save(new Piso(null,x.edificioId(),x.nombre(),x.nivel())); }
    public Espacio crear(Espacio x) { require(pisos.existsById(x.pisoId()), "Piso no encontrado"); require(x.capacidad()>=0,"La capacidad no puede ser negativa"); return espacios.save(new Espacio(null,x.pisoId(),x.nombre(),x.tipo(),x.capacidad(),x.nivelRiesgo(),x.estado()==null?EstadoEspacio.DISPONIBLE:x.estado())); }
    public Edificio actualizarEdificio(String id, Edificio x) {
        Edificio actual=edificios.findById(id).orElseThrow(()->new RecursoNoEncontrado("Edificio no encontrado"));
        return edificios.save(new Edificio(actual.id(),actual.institucionId(),x.nombre(),x.descripcion()));
    }
    public Piso actualizarPiso(String id, Piso x) {
        Piso actual=pisos.findById(id).orElseThrow(()->new RecursoNoEncontrado("Piso no encontrado"));
        return pisos.save(new Piso(actual.id(),actual.edificioId(),x.nombre(),x.nivel()));
    }
    public Espacio actualizarEspacio(String id, Espacio x) {
        Espacio actual=espacios.findById(id).orElseThrow(()->new RecursoNoEncontrado("Espacio no encontrado"));
        require(x.capacidad()>=0,"La capacidad no puede ser negativa");
        require(x.nivelRiesgo()!=null,"Nivel de riesgo requerido");
        return espacios.save(new Espacio(actual.id(),actual.pisoId(),x.nombre(),x.tipo(),x.capacidad(),x.nivelRiesgo(),actual.estado()));
    }
    public Conexion crear(Conexion x) {
        require(espacios.existsById(x.origenEspacioId()) && espacios.existsById(x.destinoEspacioId()), "Espacio de conexión no encontrado");
        require(!x.origenEspacioId().equals(x.destinoEspacioId()), "La conexión debe unir espacios distintos");
        require(Double.isFinite(x.distanciaMetros()) && x.distanciaMetros() > 0, "La distancia debe ser positiva y finita");
        return conexiones.save(new Conexion(null,x.origenEspacioId(),x.destinoEspacioId(),x.distanciaMetros(),x.bloqueada()));
    }
    public SalidaEmergencia crear(SalidaEmergencia x) { require(espacios.existsById(x.espacioId()), "Espacio no encontrado"); return salidas.save(new SalidaEmergencia(null,x.espacioId(),x.nombre(),x.ubicacion(),x.estado())); }
    public PuntoEncuentro crear(PuntoEncuentro x) { require(espacios.existsById(x.espacioId()), "Espacio no encontrado"); require(x.capacidad()>=0,"La capacidad no puede ser negativa"); return puntos.save(new PuntoEncuentro(null,x.espacioId(),x.nombre(),x.ubicacion(),x.capacidad(),x.estado())); }
    public SalidaEmergencia actualizarDisponibilidadSalida(String id, EstadoDisponibilidad estado) {
        SalidaEmergencia actual=salidas.findById(id).orElseThrow(()->new RecursoNoEncontrado("Salida de emergencia no encontrada"));
        require(estado!=null,"Estado de disponibilidad requerido");
        return salidas.save(new SalidaEmergencia(actual.id(),actual.espacioId(),actual.nombre(),actual.ubicacion(),estado));
    }
    public PuntoEncuentro actualizarDisponibilidadPunto(String id, EstadoDisponibilidad estado) {
        PuntoEncuentro actual=puntos.findById(id).orElseThrow(()->new RecursoNoEncontrado("Punto de encuentro no encontrado"));
        require(estado!=null,"Estado de disponibilidad requerido");
        return puntos.save(new PuntoEncuentro(actual.id(),actual.espacioId(),actual.nombre(),actual.ubicacion(),actual.capacidad(),estado));
    }
    public Espacio marcarBloqueo(String id, boolean bloqueado) {
        Espacio actual = espacios.findById(id).orElseThrow(() -> new RecursoNoEncontrado("Espacio no encontrado"));
        return espacios.save(new Espacio(actual.id(),actual.pisoId(),actual.nombre(),actual.tipo(),actual.capacidad(),actual.nivelRiesgo(),bloqueado?EstadoEspacio.BLOQUEADO:EstadoEspacio.DISPONIBLE));
    }
    public Espacio asignarNivelRiesgo(String id,NivelRiesgo nivel){
        Espacio actual=espacios.findById(id).orElseThrow(()->new RecursoNoEncontrado("Espacio no encontrado"));
        require(nivel!=null,"Nivel de riesgo requerido");
        return espacios.save(new Espacio(actual.id(),actual.pisoId(),actual.nombre(),actual.tipo(),actual.capacidad(),nivel,actual.estado()));
    }
    public Conexion marcarBloqueoConexion(String id, boolean bloqueada) {
        Conexion actual=conexiones.findById(id).orElseThrow(()->new RecursoNoEncontrado("Conexión no encontrada"));
        return conexiones.save(new Conexion(actual.id(),actual.origenEspacioId(),actual.destinoEspacioId(),actual.distanciaMetros(),bloqueada));
    }
    public record InfraestructuraResumen(List<Institucion> instituciones, List<Edificio> edificios,
            List<Piso> pisos, List<Espacio> espacios, List<Conexion> conexiones,
            List<SalidaEmergencia> salidas, List<PuntoEncuentro> puntosEncuentro) {}
    static void require(boolean ok, String message) { if (!ok) throw new ReglaNegocioException(message); }
}
