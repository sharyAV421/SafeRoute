package edu.saferoute.Datos_Repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import edu.saferoute.Modelo_de_Dominio.SalidaEmergencia;

public interface SalidaEmergenciaRepository extends MongoRepository<SalidaEmergencia, String> {
    List<SalidaEmergencia> findByEstado(edu.saferoute.Modelo_de_Dominio.EstadoDisponibilidad estado);
}
