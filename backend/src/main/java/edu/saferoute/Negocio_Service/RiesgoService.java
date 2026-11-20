package edu.saferoute.Negocio_Service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.saferoute.Datos_Repository.CaracteristicaRiesgoRepository;
import edu.saferoute.Datos_Repository.EspacioRepository;
import edu.saferoute.Modelo_de_Dominio.CaracteristicaRiesgo;
import edu.saferoute.Modelo_de_Dominio.NivelRiesgo;

@Service
@Transactional
public class RiesgoService {
    private final CaracteristicaRiesgoRepository riesgos; private final EspacioRepository espacios;
    public RiesgoService(CaracteristicaRiesgoRepository riesgos, EspacioRepository espacios) { this.riesgos=riesgos;this.espacios=espacios; }
    public List<CaracteristicaRiesgo> listar() { return riesgos.findAll(); }
    public List<CaracteristicaRiesgo> porEspacio(String espacioId) { return riesgos.findByEspacioId(espacioId); }
    public CaracteristicaRiesgo crear(CaracteristicaRiesgo x) {
        InfraestructuraService.require(espacios.existsById(x.espacioId()), "Espacio no encontrado");
        return riesgos.save(new CaracteristicaRiesgo(null,x.espacioId(),x.tipo(),x.descripcion()));
    }
}
