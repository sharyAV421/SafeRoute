package edu.saferoute.Negocio_Service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.saferoute.Datos_Repository.EmergenciaRepository;
import edu.saferoute.Datos_Repository.EspacioRepository;
import edu.saferoute.Modelo_de_Dominio.*;

@Service
@Transactional
public class EmergenciaService {
    private final EmergenciaRepository emergencias; private final EspacioRepository espacios;
    public EmergenciaService(EmergenciaRepository emergencias, EspacioRepository espacios) { this.emergencias=emergencias;this.espacios=espacios; }
    public List<Emergencia> listar() { return emergencias.findAll(); }
    public Emergencia actualizarDatos(String id, TipoEmergencia tipo, GravedadEmergencia gravedad, String descripcion) {
        Emergencia actual=emergencias.findById(id).orElseThrow(()->new RecursoNoEncontrado("Emergencia no encontrada"));
        return emergencias.save(new Emergencia(actual.id(),tipo,gravedad,descripcion,actual.estado(),actual.fechaHora(),actual.zonasAfectadas()));
    }
    public Emergencia crear(Emergencia x) {
        List<String> ids = x.zonasAfectadas()==null ? List.of() : x.zonasAfectadas().stream().map(ZonaAfectada::espacioId).distinct().toList();
        if (!espacios.findAllById(ids).stream().map(e->e.id()).toList().containsAll(ids)) throw new ReglaNegocioException("Uno o más espacios no existen");
        var zones=x.zonasAfectadas()==null?List.<ZonaAfectada>of():x.zonasAfectadas().stream().collect(java.util.stream.Collectors.toMap(ZonaAfectada::espacioId,z->z,(a,b)->a,java.util.LinkedHashMap::new)).values().stream().map(z->new ZonaAfectada(java.util.UUID.randomUUID().toString(),z.espacioId(),EstadoEspacio.AFECTADO,z.descripcion())).toList();
        return emergencias.save(new Emergencia(null,x.tipo(),x.gravedad(),x.descripcion(),EstadoEmergencia.ACTIVA,x.fechaHora()==null?LocalDateTime.now():x.fechaHora(),zones));
    }
    public Emergencia actualizarZonasAfectadas(String id, List<String> espacioIds) {
        Emergencia actual=emergencias.findById(id).orElseThrow(()->new RecursoNoEncontrado("Emergencia no encontrada"));
        List<String> ids=espacioIds.stream().distinct().toList();
        if(!espacios.findAllById(ids).stream().map(Espacio::id).toList().containsAll(ids))
            throw new ReglaNegocioException("Uno o más espacios no existen");
        var existentes=actual.zonasAfectadas()==null?java.util.Map.<String,ZonaAfectada>of():actual.zonasAfectadas().stream()
                .collect(java.util.stream.Collectors.toMap(ZonaAfectada::espacioId,z->z,(a,b)->a));
        List<ZonaAfectada> zonas=ids.stream().map(espacioId->{
            ZonaAfectada previa=existentes.get(espacioId);
            return new ZonaAfectada(previa==null?java.util.UUID.randomUUID().toString():previa.id(),espacioId,
                    EstadoEspacio.AFECTADO,previa==null?null:previa.descripcion());
        }).toList();
        return emergencias.save(new Emergencia(actual.id(),actual.tipo(),actual.gravedad(),actual.descripcion(),
                actual.estado(),actual.fechaHora(),zonas));
    }
    public Emergencia finalizar(String id) {
        Emergencia e=emergencias.findById(id).orElseThrow(()->new RecursoNoEncontrado("Emergencia no encontrada"));
        return emergencias.save(new Emergencia(e.id(),e.tipo(),e.gravedad(),e.descripcion(),EstadoEmergencia.FINALIZADA,e.fechaHora(),e.zonasAfectadas()));
    }
}
