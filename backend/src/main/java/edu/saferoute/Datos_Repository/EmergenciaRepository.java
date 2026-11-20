package edu.saferoute.Datos_Repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import edu.saferoute.Modelo_de_Dominio.Emergencia;

public interface EmergenciaRepository extends MongoRepository<Emergencia, String> {
    List<Emergencia> findByEstadoIgnoreCase(String estado);
}
