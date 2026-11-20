package edu.saferoute.Negocio_Service;

import java.util.List;
import org.springframework.stereotype.Service;
import edu.saferoute.Datos_Repository.EmergenciaRepository;
import edu.saferoute.Datos_Repository.EspacioRepository;
import edu.saferoute.Datos_Repository.CaracteristicaRiesgoRepository;
import edu.saferoute.Modelo_de_Dominio.PrioridadEspacio;
import edu.saferoute.Modelo_de_Dominio.ZonaAfectada;

@Service
public class PrioridadService {
    private final EmergenciaRepository emergencias;
    private final EspacioRepository espacios;
    private final CaracteristicaRiesgoRepository riesgos;
    public PrioridadService(EmergenciaRepository emergencias, EspacioRepository espacios, CaracteristicaRiesgoRepository riesgos) {
        this.emergencias=emergencias; this.espacios=espacios; this.riesgos=riesgos;
    }
    public List<PrioridadEspacio> consultar(String emergenciaId) {
        var e = emergencias.findById(emergenciaId).orElseThrow(() -> new RecursoNoEncontrado("Emergencia no encontrada"));
        return e.zonasAfectadas().stream().map(ZonaAfectada::espacioId).distinct().filter(espacios::existsById)
                .map(id -> new PrioridadEspacio(id, null, false,
                        riesgos.findByEspacioId(id).isEmpty() ? "Fórmula pendiente; faltan características de riesgo" : "Fórmula de prioridad pendiente de aprobación"))
                .toList();
    }
}
